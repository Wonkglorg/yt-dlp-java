package com.wonkglorg.ytdlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wonkglorg.ytdlp.callback.DownloadProgressCallback;
import com.wonkglorg.ytdlp.exception.YtDlpException;
import com.wonkglorg.ytdlp.mapper.Format;
import com.wonkglorg.ytdlp.mapper.json.PlaylistInfo;
import com.wonkglorg.ytdlp.mapper.json.PlaylistPreviewInfo;
import com.wonkglorg.ytdlp.mapper.json.VideoInfo;
import com.wonkglorg.ytdlp.utils.AudioOption;
import com.wonkglorg.ytdlp.utils.StreamGobbler;
import com.wonkglorg.ytdlp.utils.StreamProcessExtractor;
import com.wonkglorg.ytdlp.utils.VideoOption;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//todo preview downloads not correctly working

/**
 * Provide an interface for yt-dlp executable
 *
 * <p>For more information on yt-dlp, please see <a
 * href="https://github.com/yt-dlp/yt-dlp/blob/master/README.md">yt-dlp Documentation</a>
 */
public class YtDlp {
    private static final Logger log = Logger.getLogger(YtDlp.class.getName());
    private static DownloadProgressCallback globalCallBack = defaultCallBack();
    private static final Pattern outputFileNamePattern = Pattern.compile("\\[Merger] Merging formats into \"(?<filename>.+)\"");
    private static final Pattern hasBeenDownloadedPattern = Pattern.compile("\\[download] (?<filename>.+) has already been downloaded");


    private YtDlp() {
        // Private constructor is here to encourage static usage of this class
    }

    /**
     * yt-dlp executable name
     */
    protected static String executablePath = "yt-dlp";

    /**
     * Append executable name to command
     *
     * @param command Command string
     * @return Command string
     */
    private static String buildCommand(String command) {
        return String.format("%s %s", executablePath, command);
    }


    /**
     * Execute yt-dlp request
     *
     * @param request request object
     * @return response object
     */
    public static YtDlpResponse execute(YtDlpRequest request) throws YtDlpException {
        return execute(request, null);
    }

    /**
     * Execute yt-dlp request
     *
     * @param request  request object
     * @param callback callback to monitor download progress
     * @return response object
     */
    public static YtDlpResponse execute(YtDlpRequest request, DownloadProgressCallback callback) throws YtDlpException {

        String command = buildCommand(request.buildOptions());
        String directory = request.getDirectory();
        Map<String, String> options = request.getOption();

        Process process;
        int exitCode;
        StringBuilder outBuffer = new StringBuilder(); // stdout
        StringBuilder errBuffer = new StringBuilder(); // stderr
        long startTime = System.nanoTime();

        String[] split = command.split(" ");

        ProcessBuilder processBuilder = new ProcessBuilder(split);

        // Define directory if one is passed
        if (directory != null) processBuilder.directory(new File(directory));

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new YtDlpException(e);
        }

        InputStream outStream = process.getInputStream();
        InputStream errStream = process.getErrorStream();

        StreamProcessExtractor stdOutProcessor = new StreamProcessExtractor(outBuffer, outStream, callback);
        StreamGobbler stdErrProcessor = new StreamGobbler(errBuffer, errStream);

        try {
            stdOutProcessor.join();
            stdErrProcessor.join();
            exitCode = process.waitFor();
        } catch (InterruptedException e) {

            // process exited for some reason
            throw new YtDlpException(e);
        }

        String out = outBuffer.toString();
        String err = errBuffer.toString();

        if (exitCode > 0) {
            throw new YtDlpException(err);
        }

        int elapsedTime = (int) ((System.nanoTime() - startTime) / 1000000);

        return new YtDlpResponse(String.join(" ", command), options, directory, exitCode, elapsedTime, out, err);
    }

    /**
     * Get yt-dlp executable version
     *
     * @return version string
     */
    public static String getVersion() throws YtDlpException {
        YtDlpRequest request = new YtDlpRequest().addOption("--version");
        return YtDlp.execute(request).getOut();
    }


    /**
     * Download a video from a URL with full metadata
     *
     * @param videoUrl    The video url
     * @param path        The path to save the video
     * @param fileName    The name of the file
     * @param videoOption The Video Format to download the url in
     * @return A Map of the saved file and the video info related to it
     */
    public static Map<String, VideoInfo> downloadVideo(String videoUrl, String path, String fileName, VideoOption videoOption) throws YtDlpException {
        return download(videoUrl, fileName, videoOption.getExtension(), new YtDlpRequest(videoUrl)//
                .setDirectory(path)//
                .addOption("--output", fileName + "." + videoOption.getExtension()));
    }

    /**
     * Download a video from a URL with full metadata user
     *
     * @param videoUrl    The video url
     * @param path        The path to save the video
     * @param videoOption The Video Format to download the url in
     * @return A Map of the saved file and the video info related to it
     * @throws YtDlpException If the video cannot be downloaded
     */
    public static Map<String, VideoInfo> downloadVideo(String videoUrl, String path, VideoOption videoOption) throws YtDlpException {
        return downloadVideo(videoUrl, path, "%(title)s", videoOption);
    }

    /**
     * Download a video in mp4 from a URL with full metadata user
     *
     * @param videoUrl The video url
     * @param path     The path to save the video
     * @return A Map of the saved file and the video info related to it
     * @throws YtDlpException If the video cannot be downloaded
     */
    public static Map<String, VideoInfo> downloadVideo(String videoUrl, String path) throws YtDlpException {
        return downloadVideo(videoUrl, path, "%(title)s", VideoOption.MP4);
    }

    /**
     * Downloads the mp3 audio from a URL with full metadata
     *
     * @param videoUrl    The video url
     * @param path        The path to save the video
     * @param audioOption The Audio Format to download the url in
     * @return A Map of the saved file and the video info related to it
     * @throws YtDlpException If the audio cannot be downloaded
     */
    public static Map<String, VideoInfo> downloadAudio(String videoUrl, String path, String fileName, AudioOption audioOption) throws YtDlpException {
        return download(videoUrl, fileName, audioOption.getExtension(), new YtDlpRequest(videoUrl)//
                .setDirectory(path)//
                .addOption("--extract-audio")//
                .addOption(audioOption.getParams())//
                .addOption("--output", fileName + "+" + audioOption.getExtension()));
    }

    /**
     * Downloads audio from a URL with full metadata
     *
     * @param videoUrl    The video url
     * @param path        The path to save the video
     * @param audioOption The Audio Format to download the url in
     * @return A Map of the saved file and the video info related to it
     * @throws YtDlpException If the audio cannot be downloaded
     */
    public static Map<String, VideoInfo> downloadAudio(String videoUrl, String path, AudioOption audioOption) throws YtDlpException {
        return downloadAudio(videoUrl, path, "%(title)s", audioOption);
    }

    /**
     * Downloads mp3 audio from a URL with full metadata
     *
     * @param videoUrl The video url
     * @param path     The path to save the video
     * @return A Map of the saved file and the video info related to it
     * @throws YtDlpException If the audio cannot be downloaded
     */
    public static Map<String, VideoInfo> downloadAudio(String videoUrl, String path) throws YtDlpException {
        return downloadAudio(videoUrl, path, "%(title)s", AudioOption.MP3);
    }

    /**
     * Helper method to download playlists with full metadata information
     *
     * @param playlistUrl          The playlist url
     * @param path                 The path to save the videos
     * @param subDirectoryPlaylist If true each playlist gets its own subdirectory with the name of the playlist as its name
     * @return A Map of the saved files and the video info related to it
     * @throws YtDlpException
     */
    private static Map<String, VideoInfo> downloadPlaylist(String playlistUrl, String path, boolean subDirectoryPlaylist, BiFunction<String, String, Map<String, VideoInfo>> downloader) throws YtDlpException {
        Optional<PlaylistInfo> playlistInfoOptional = getPlaylistInfo(playlistUrl);
        if (playlistInfoOptional.isEmpty()) {
            throw new YtDlpException("Playlist not found");
        }

        PlaylistInfo playlistInfo = playlistInfoOptional.get();
        List<VideoInfo> videoInfos = playlistInfo.getEntries();

        if (subDirectoryPlaylist) path = path + "/" + playlistInfo.getTitle();

        Path.of(path).toFile().mkdirs();

        List<Callable<Map<String, VideoInfo>>> tasks = new ArrayList<>();
        System.out.println("Downloading " + videoInfos.size() + " videos");
        for (VideoInfo videoInfo : videoInfos) {
            String finalPath = path;
            tasks.add(() -> downloader.apply(videoInfo.getOriginalUrl(), finalPath));
        }

        return executeTasks(tasks);
    }

    public static Map<String, VideoInfo> downloadPlaylistVideo(String playlistUrl, String path, boolean subDirectoryPlaylist) {
        try {
            return downloadPlaylist(playlistUrl, path, subDirectoryPlaylist, YtDlp::downloadVideo);
        } catch (YtDlpException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    public static Map<String, VideoInfo> downloadPlaylistAudio(String playlistUrl, String path, boolean subDirectoryPlaylist) throws YtDlpException {
        return downloadPlaylist(playlistUrl, path, subDirectoryPlaylist, YtDlp::downloadAudio);
    }

    /**
     * Get available formats for a video
     *
     * @param url
     */
    public static List<Format> getFormats(String url) {
        YtDlpRequest request = new YtDlpRequest(url);
        request.addOption("--list-formats");
        try {
            YtDlpResponse response = YtDlp.execute(request);
            System.out.println(response.getOut());
            Format.parse(response.getOut()).forEach(System.out::println);
        } catch (YtDlpException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Download a thumbnail from a video
     *
     * @param videoUrl The video url
     * @param path     The path to save the thumbnail
     * @return The path to the thumbnail
     */
    public static void downloadThumbnail(String videoUrl, String path) {
        YtDlpRequest request = new YtDlpRequest(videoUrl);
        request.setDirectory(path);
        request.addOption("--skip-download");
        request.addOption("--write-thumbnail");
        request.addOption("--output", "%(title)s.%(ext)s");
        try {
            YtDlp.execute(request);
        } catch (YtDlpException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the full Information regarding all videos in a playlist as seen in {@link #getVideoInfo(String)}
     * <br>
     * Note: if this method is called on a non playlist url it will return a playlist object with 1 entry being the video information, if the video is a direct link to a video in a playlist it will return the entire playlist
     *
     * @param url The Playlist url
     * @return {@link PlaylistInfo}
     */
    public static Optional<PlaylistInfo> getPlaylistInfo(String url) throws YtDlpException {
        Optional<PlaylistInfo> optionalPlaylistPreviewInfo = getPlaylistInfoSetup(url);

        if (optionalPlaylistPreviewInfo.isEmpty()) {
            return Optional.empty();
        }

        PlaylistInfo playlistPreviewInfo = optionalPlaylistPreviewInfo.get();

        YtDlpRequest request = new YtDlpRequest(url);
        request.addOption("--dump-json");
        YtDlpResponse response = YtDlp.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        List<VideoInfo> videoInfos = new ArrayList<>();
        String responseOutput = response.getOut();

        //splits them by their outer brackets, because the format is not valid json but each video entry is a seperate json all in 1 response.
        String[] jsonObjects = responseOutput.split("}\\s*\\{");

        if (jsonObjects.length < 1) return Optional.empty();

        for (int i = 0; i < jsonObjects.length; i++) {
            if (i == 0) {
                jsonObjects[i] = jsonObjects[i] + "}";
            } else if (i == jsonObjects.length - 1) {
                jsonObjects[i] = "{" + jsonObjects[i] + "}";
            } else {
                jsonObjects[i] = "{" + jsonObjects[i];
            }
        }

        try {
            for (String jsonObject : jsonObjects) {
                VideoInfo videoInfo = objectMapper.readValue(jsonObject + "}", VideoInfo.class);
                videoInfos.add(videoInfo);
            }
        } catch (IOException e) {
            throw new YtDlpException("Unable to parse video information: " + e.getMessage());
        }

        playlistPreviewInfo.setEntries(videoInfos);

        return Optional.of(playlistPreviewInfo);
    }

    /**
     * Returns limited Information regarding a playlist if more information is needed use {@link #getPlaylistInfo(String)} instead at the cost of time needed to obtain
     *
     * @param url The Playlist url
     * @return {@link PlaylistPreviewInfo}
     * @throws YtDlpException If the
     */
    public static Optional<PlaylistPreviewInfo> getPlaylistPreviewInfo(String url) throws YtDlpException {
        YtDlpRequest request = new YtDlpRequest(url);
        request.addOption("--dump-single-json");
        request.addOption("--flat-playlist");
        request.addOption("--skip-download");
        YtDlpResponse response = YtDlp.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        PlaylistPreviewInfo videoInfo;

        try {
            videoInfo = objectMapper.readValue(response.getOut(), PlaylistPreviewInfo.class);
        } catch (IOException e) {
            throw new YtDlpException("Unable to parse video information: " + e.getMessage());
        }

        return Optional.of(videoInfo);
    }

    /**
     * Setup method to get the base playlist information
     */
    private static Optional<PlaylistInfo> getPlaylistInfoSetup(String url) throws YtDlpException {
        YtDlpRequest request = new YtDlpRequest(url);
        request.addOption("--dump-single-json");
        request.addOption("--flat-playlist");
        request.addOption("--skip-download");
        YtDlpResponse response = YtDlp.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        PlaylistInfo videoInfo;

        try {
            videoInfo = objectMapper.readValue(response.getOut(), PlaylistInfo.class);
        } catch (IOException e) {
            throw new YtDlpException("Unable to parse video information: " + e.getMessage());
        }

        return Optional.of(videoInfo);
    }

    /**
     * Checks weather the URL is a playlist or not (do not call too often, takes a few seconds on average to calculate)
     *
     * @param url Video Url
     * @return true if the video url belongs to a playlist, false if the video is not a playlist or an invalid link
     * @throws YtDlpException
     */
    public static boolean isPlaylist(String url) throws YtDlpException {
        //works faster on playlists than individual videos duo to the data retrieved is there a flag to set that doesn't output as much info?
        YtDlpRequest request = new YtDlpRequest(url);
        request.addOption("--dump-single-json");
        request.addOption("--flat-playlist");
        request.addOption("--skip-download");
        YtDlpResponse response = YtDlp.execute(request);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(response.getOut());
        } catch (IOException e) {
            throw new YtDlpException("Unable to parse video information: " + e.getMessage());
        }

        // Check if the JSON response contains playlist-specific fields
        return jsonNode.has("entries");
    }

    /**
     * Retrieve all information available on a video (if the link refers to a playlist get information about the first video in the list)
     * <br>
     * If the link is a direct link to a playlist it will return the first video in the playlist
     *
     * @param url Video url
     * @return Video info
     */
    public static Optional<VideoInfo> getVideoInfo(String url) throws YtDlpException {

        // Build request
        YtDlpRequest request = new YtDlpRequest(url);
        request.addOption("--dump-json");
        request.addOption("--no-playlist");
        YtDlpResponse response = YtDlp.execute(request);

        // Parse result
        ObjectMapper objectMapper = new ObjectMapper();
        VideoInfo videoInfo;

        try {
            videoInfo = objectMapper.readValue(response.getOut(), VideoInfo.class);
        } catch (IOException e) {
            throw new YtDlpException("Unable to parse video information: " + e.getMessage());
        }

        return Optional.of(videoInfo);
    }


    /**
     * Get command executable or path to the executable
     *
     * @return path string
     */
    public static String getExecutablePath() {
        return executablePath;
    }

    /**
     * Set path to use for the command
     *
     * @param path String path to the executable
     */
    public static void setExecutablePath(String path) {
        executablePath = path;
    }

    private static <T> Map<String, T> executeTasks(List<Callable<Map<String, T>>> tasks) throws YtDlpException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Map<String, T>>> futures = new ArrayList<>();

        for (Callable<Map<String, T>> task : tasks) {
            futures.add(executorService.submit(task));
        }

        Map<String, T> results = new HashMap<>();
        for (Future<Map<String, T>> future : futures) {
            try {
                results.putAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new YtDlpException(e);
            }
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor service did not terminate");
                }
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return results;
    }

    public static DownloadProgressCallback defaultCallBack() {
        return (progress) -> {
            System.out.print("\r" + String.format("[download] %s | %s of ~  %s at    %s ETA %ss (frag %s/%s)", progress.fileName().split("\\.")[0], progress.progressPercent(), progress.totalFileSize(), progress.downloadSpeed(), progress.etaSeconds(), progress.currFragment(), progress.totalFragments()));
        };
    }


    /**
     * Helper method to download media
     */
    private static Map<String, VideoInfo> download(String videoUrl, String fileName, String format, YtDlpRequest request) throws YtDlpException {
        Optional<VideoInfo> videoInfoOptional = getVideoInfo(videoUrl);

        if (videoInfoOptional.isEmpty()) throw new YtDlpException("Video not found");

        VideoInfo videoInfo = videoInfoOptional.get();

        var response = YtDlp.execute(request, globalCallBack);

        String outputFileName = hasFileBeenDownloaded(response.getOut());
        if (outputFileName != null) {
            log.warning("File has already been downloaded");
        } else {
            outputFileName = extractFileName(response.getOut());
            if (outputFileName == null) {
                outputFileName = fileName;
                log.warning("Could not extract filename from output, using default filename");
            }
        }
        return Map.of(outputFileName, videoInfo);
    }

    /**
     * Checks if the file has already been downloaded
     *
     * @param output The output of the yt-dlp command
     * @return True if the file has already been downloaded
     */
    private static String hasFileBeenDownloaded(String output) {

        for (String line : output.split("\n")) {
            Matcher matcher = hasBeenDownloadedPattern.matcher(line);
            if (matcher.matches()) {
                return matcher.group("filename");
            }
        }
        return null;
    }

    /**
     * Finds the filename from the output of the yt-dlp command
     *
     * @param output The output of the yt-dlp command
     * @return The filename
     */
    private static String extractFileName(String output) {
        for (String line : output.split("\n")) {
            Matcher matcher = outputFileNamePattern.matcher(line);
            if (matcher.matches()) {
                return matcher.group("filename");
            }
        }
        return null;
    }

    /**
     * Sets the global callback function for all predefined download methods
     *
     * @param callback
     */
    public void setGlobalCallBack(DownloadProgressCallback callback) {
        globalCallBack = callback;
    }

}

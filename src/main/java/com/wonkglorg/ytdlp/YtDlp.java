package com.wonkglorg.ytdlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wonkglorg.ytdlp.callback.DownloadProgressCallback;
import com.wonkglorg.ytdlp.exception.YtDlpException;
import com.wonkglorg.ytdlp.functional.TriFunction;
import com.wonkglorg.ytdlp.mapper.Format;
import com.wonkglorg.ytdlp.mapper.json.PlaylistInfo;
import com.wonkglorg.ytdlp.mapper.json.PlaylistPreviewInfo;
import com.wonkglorg.ytdlp.mapper.json.VideoInfo;
import com.wonkglorg.ytdlp.mapper.json.VideoPreviewInfo;
import com.wonkglorg.ytdlp.utils.StreamGobbler;
import com.wonkglorg.ytdlp.utils.StreamProcessExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;

/**
 * Provide an interface for yt-dlp executable
 *
 * <p>For more information on yt-dlp, please see <a
 * href="https://github.com/yt-dlp/yt-dlp/blob/master/README.md">yt-dlp Documentation</a>
 */
public class YtDlp {

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
    protected static String buildCommand(String command) {
        return String.format("%s %s", executablePath, command);
    }


    /**
     * Execute yt-dlp request
     *
     * @param request request object
     * @return response object
     * @throws YtDlpException
     */
    public static YtDlpResponse execute(YtDlpRequest request) throws YtDlpException {
        return execute(request, null);
    }

    /**
     * Execute yt-dlp request
     *
     * @param request  request object
     * @param callback callback
     * @return response object
     * @throws YtDlpException
     */
    public static YtDlpResponse execute(YtDlpRequest request, DownloadProgressCallback callback) throws YtDlpException {

        String command = buildCommand(request.buildOptions());
        String directory = request.getDirectory();
        Map<String, String> options = request.getOption();

        YtDlpResponse ytDlpResponse;
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

        ytDlpResponse = new YtDlpResponse(String.join(" ", command), options, directory, exitCode, elapsedTime, out, err);

        return ytDlpResponse;
    }

    /**
     * Get yt-dlp executable version
     *
     * @return version string
     * @throws YtDlpException
     */
    public static String getVersion() throws YtDlpException {
        YtDlpRequest request = new YtDlpRequest().setOption("--version");
        return YtDlp.execute(request).getOut();
    }

    /**
     * Helper method to download media
     */
    private static Map<String, VideoInfo> download(String videoUrl, String fileName, String format, YtDlpRequest request) {
        Optional<VideoInfo> videoInfoOptional = Optional.empty();
        try {
            videoInfoOptional = getVideoInfo(videoUrl);
        } catch (YtDlpException e) {
            e.printStackTrace();
        }

        VideoInfo videoInfo = videoInfoOptional.get();
        String filePath = fileName == null ? videoInfo.getFileName() : fileName + "." + format;

        try {
            YtDlp.execute(request);
        } catch (YtDlpException e) {
            e.printStackTrace();

        }

        return Map.of(filePath, videoInfo);
    }

    /**
     * Download a video from a URL with full metadata
     *
     * @param videoUrl The video url
     * @param path     The path to save the video
     * @param fileName The name of the file
     * @return A Map of the saved file and the video info related to it
     */
    public static Map<String, VideoInfo> downloadVideo(String videoUrl, String path, String fileName) {
        return download(videoUrl, fileName, "mp4", new YtDlpRequest(videoUrl).setOption("--output", path + "\\" + fileName + ".mp4"));
    }

    /**
     * Download a video from a URL with full metadata user
     *
     * @param videoUrl The video url
     * @param path     The path to save the video
     * @return A Map of the saved file and the video info related to it
     * @throws YtDlpException If the video cannot be downloaded
     */
    public static Map<String, VideoInfo> downloadVideo(String videoUrl, String path) {
        return downloadVideo(videoUrl, path, "%(title)s");
    }

    /**
     * Downloads audio from a URL with full metadata
     *
     * @param videoUrl The video url
     * @param path     The path to save the video
     * @return A Map of the saved file and the video info related to it
     * @throws YtDlpException If the video cannot be downloaded
     */
    public static Map<String, VideoInfo> downloadAudio(String videoUrl, String path, String fileName) {
        return download(videoUrl, fileName, "mp3", new YtDlpRequest(videoUrl).setOption("--extract-audio").setOption("--audio-format", "mp3").setOption("--output", path + "\\" + fileName + ".mp3"));
    }

    /**
     * Downloads audio from a URL with full metadata
     *
     * @param videoUrl The video url
     * @param path     The path to save the video
     * @return A Map of the saved file and the video info related to it
     * @throws YtDlpException If the video cannot be downloaded
     */
    public static Map<String, VideoInfo> downloadAudio(String videoUrl, String path) {
        return downloadAudio(videoUrl, path, "%(title)s");
    }


    /**
     * Helper method to download videos with preview info
     */
    private static Map<String, VideoPreviewInfo> downloadPreview(String path, VideoPreviewInfo videoPreviewInfo, YtDlpRequest request) {
        try {
            YtDlp.execute(request);
        } catch (YtDlpException e) {
            e.printStackTrace();

        }

        return Map.of(path + "/" + videoPreviewInfo.getTitle() + ".mp4", videoPreviewInfo);
    }

    /**
     * Download a video from a URL with full metadata user {@link #downloadVideo(String, String, String)} for a faster download but with less available metadata
     *
     * @param videoUrl         The video url
     * @param path             The path to save the video
     * @param videoPreviewInfo The video preview info
     * @return A Map of the saved file and the video preview info related to it
     */
    private static Map<String, VideoPreviewInfo> downloadVideoPreview(String videoUrl, String path, VideoPreviewInfo videoPreviewInfo) {
        return downloadPreview(path, videoPreviewInfo, new YtDlpRequest(videoUrl).setOption("--output", path + "/%(title)s.mp4").setOption("--format", "bestvideo+bestaudio/best"));
    }

    /**
     * Download a video from a URL with full metadata user {@link #downloadVideo(String, String, String)} for a faster download but with less available metadata
     *
     * @param videoUrl         The video url
     * @param path             The path to save the audio
     * @param videoPreviewInfo The video preview info
     * @return A Map of the saved file and the video preview info related to it
     */
    private static Map<String, VideoPreviewInfo> downloadAudioPreview(String videoUrl, String path, VideoPreviewInfo videoPreviewInfo) {
        return downloadPreview(videoUrl, videoPreviewInfo, new YtDlpRequest(videoUrl).setOption("--extract-audio").setOption("--audio-format", "mp3").setOption("--output", path + "/%(title)s.mp3"));
    }


    /**
     * Helper method to download playlists with preview info
     */
    private static Map<String, VideoPreviewInfo> downloadPlaylistPreview(String playlistUrl, String path, boolean subDirectoryPlaylist, TriFunction<String, String, VideoPreviewInfo, Map<String, VideoPreviewInfo>> downloader) throws YtDlpException {
        Optional<PlaylistPreviewInfo> playlistInfoOptional = getPlaylistPreviewInfo(playlistUrl);
        if (playlistInfoOptional.isEmpty()) {
            throw new YtDlpException("Playlist not found");
        }

        PlaylistPreviewInfo playlistInfo = playlistInfoOptional.get();
        List<VideoPreviewInfo> videoInfos = playlistInfo.getEntries();

        if (subDirectoryPlaylist) path = path + "/" + playlistInfo.getTitle();

        List<Callable<Map<String, VideoPreviewInfo>>> tasks = new ArrayList<>();
        for (VideoPreviewInfo videoInfo : videoInfos) {
            String finalPath = path;
            tasks.add(() -> downloader.apply(videoInfo.getUrl(), finalPath, videoInfo));
        }

        return executeTasks(tasks);
    }

    /**
     * Downloads videos from a playlist, with lower metadata information (Use {@link #downloadPlaylistVideo(String, String, boolean)} for full metadata information at the cost of slower speed
     *
     * @param playlistUrl          The playlist url
     * @param path                 The path to save the videos
     * @param subDirectoryPlaylist If true each playlist gets its own subdirectory with the name of the playlist as its name
     * @return A Map of the saved files and the video info related to it
     * @throws YtDlpException
     */
    public static Map<String, VideoPreviewInfo> downloadPlaylistVideoPreview(String playlistUrl, String path, boolean subDirectoryPlaylist) throws YtDlpException {
        return downloadPlaylistPreview(playlistUrl, path, subDirectoryPlaylist, YtDlp::downloadVideoPreview);
    }

    /**
     * Downloads audio from a playlist, with lower metadata information (Use {@link #downloadPlaylistAudio(String, String, boolean)} for full metadata information at the cost of slower speed
     *
     * @param playlistUrl          The playlist url
     * @param path                 The path to save the videos
     * @param subDirectoryPlaylist If true each playlist gets its own subdirectory with the name of the playlist as its name
     * @return A Map of the saved files and the audio info related to it
     * @throws YtDlpException
     */
    public static Map<String, VideoPreviewInfo> downloadPlaylistAudioPreview(String playlistUrl, String path, boolean subDirectoryPlaylist) throws YtDlpException {
        return downloadPlaylistPreview(playlistUrl, path, subDirectoryPlaylist, YtDlp::downloadAudioPreview);
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
            e.printStackTrace();
            return null;
        }

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
        request.setOption("--list-formats");
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
        request.setOption("--skip-download");
        request.setOption("--write-thumbnail");
        request.setOption("--output", path + "/%(title)s.%(ext)s");
        try {
            YtDlp.execute(request);
        } catch (YtDlpException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the full Information regarding all videos in a playlist as seen in {@link #getVideoInfo(String)}
     *
     * @param url
     * @return
     * @throws YtDlpException
     */
    public static Optional<PlaylistInfo> getPlaylistInfo(String url) throws YtDlpException {
        Optional<PlaylistInfo> optionalPlaylistPreviewInfo = getPlaylistInfoSetup(url);

        if (optionalPlaylistPreviewInfo.isEmpty()) {
            return Optional.empty();
        }

        PlaylistInfo playlistPreviewInfo = optionalPlaylistPreviewInfo.get();

        YtDlpRequest request = new YtDlpRequest(url);
        request.setOption("--dump-json");
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
        request.setOption("--dump-single-json");
        request.setOption("--flat-playlist");
        request.setOption("--skip-download");
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
        request.setOption("--dump-single-json");
        request.setOption("--flat-playlist");
        request.setOption("--skip-download");
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
        request.setOption("--dump-single-json");
        request.setOption("--flat-playlist");
        request.setOption("--skip-download");
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
     *
     * @param url Video url
     * @return Video info
     * @throws YtDlpException
     */
    public static Optional<VideoInfo> getVideoInfo(String url) throws YtDlpException {

        // Build request
        YtDlpRequest request = new YtDlpRequest(url);
        request.setOption("--dump-json");
        request.setOption("--no-playlist");
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
            System.out.print("\r" + String.format("[download] %s of ~  %s at    %s ETA %ss (frag %s/%s)", progress.progressPercent(), progress.totalFileSize(), progress.downloadSpeed(), progress.etaSeconds(), progress.currFragment(), progress.totalFragments()));
        };
    }

}

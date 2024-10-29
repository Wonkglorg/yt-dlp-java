package com.wonkglorg.ytdlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wonkglorg.ytdlp.callback.DownloadProgressCallback;
import com.wonkglorg.ytdlp.exception.YtDlpException;
import com.wonkglorg.ytdlp.mapper.Format;
import com.wonkglorg.ytdlp.mapper.json.PlaylistInfo;
import com.wonkglorg.ytdlp.mapper.json.PlaylistPreviewInfo;
import com.wonkglorg.ytdlp.mapper.json.VideoInfo;
import com.wonkglorg.ytdlp.utils.StreamGobbler;
import com.wonkglorg.ytdlp.utils.StreamProcessExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static com.wonkglorg.ytdlp.utils.ConsoleColor.*;

/**
 * Provide an interface for yt-dlp executable
 *
 * <p>For more information on yt-dlp, please see <a
 * href="https://github.com/yt-dlp/yt-dlp/blob/master/README.md">yt-dlp Documentation</a>
 */
public class YtDlp {
    private static final Logger log = Logger.getLogger(YtDlp.class.getName());


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

        String command = buildCommand(request.buildOptions());
        String directory = request.getDirectory();
        Map<String, String> options = request.getOption();

        Process process;
        int exitCode;
        StringBuilder outBuffer = new StringBuilder(); // stdout
        StringBuilder errBuffer = new StringBuilder(); // stderr
        long startTime = System.nanoTime();

        //if multiple empty lines happen correctly split em up
        String[] split = Arrays.stream(command.split(" "))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

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

        StreamProcessExtractor stdOutProcessor = new StreamProcessExtractor(outBuffer, outStream, request);
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

    /**
     * Default callback function for download progress
     *
     * @return Default callback function
     */
    public static DownloadProgressCallback defaultCallBack() {
        return (progress) ->//
        {

            String text = String.format("[download] %s | %s of ~  %s at    %s ETA %ss", //
                    progress.fileName().split("\\.")[0], //
                    BRIGHT_BLUE + "" + progress.progressPercent() + RESET, //
                    GREEN + progress.totalFileSize() + RESET,//
                    BLUE + progress.downloadSpeed() + RESET, //
                    CYAN + "" + progress.etaSeconds() + RESET);

            if (progress.totalFragments() > 0) {
                text += String.format(" | %s of %s fragments", //
                        BRIGHT_RED + "" + progress.currFragment() + RESET, //
                        RED + "" + progress.totalFragments() + RESET);
            }

            System.out.print("\r" + text);
        };
    }
}

package com.wonkglorg.ytdlp.builder;

import com.wonkglorg.ytdlp.YtDlp;
import com.wonkglorg.ytdlp.YtDlpRequest;
import com.wonkglorg.ytdlp.callback.DownloadEndCallback;
import com.wonkglorg.ytdlp.callback.DownloadProgressCallback;
import com.wonkglorg.ytdlp.callback.DownloadStartCallback;
import com.wonkglorg.ytdlp.exception.YtDlpException;
import com.wonkglorg.ytdlp.mapper.PlaylistInfoData;
import com.wonkglorg.ytdlp.mapper.VideoFileInfo;
import com.wonkglorg.ytdlp.mapper.json.PlaylistInfo;
import com.wonkglorg.ytdlp.mapper.json.PlaylistPreviewInfo;
import com.wonkglorg.ytdlp.mapper.json.VideoInfo;
import com.wonkglorg.ytdlp.mapper.json.VideoPreviewInfo;
import com.wonkglorg.ytdlp.utils.FormatOption;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wonkglorg.ytdlp.YtDlp.*;

public class DownloadBuilder {
    private static final Logger log = Logger.getLogger(DownloadBuilder.class.getName());
    /**
     * The Pattern to identify the already downloaded message
     */
    private static final Pattern hasBeenDownloadedPattern = Pattern.compile("\\[download] (?<filename>.+) has already been downloaded");
    /**
     * The Pattern to identify the output filename
     */
    private static final Pattern outputFileNamePattern = Pattern.compile("\\[Merger] Merging formats into \"(?<filename>.+)\"");
    /**
     * The Pattern to identify the output filename for audio only downloads
     */
    private static final Pattern outputFileNameAudioPattern = Pattern.compile("\\[ExtractAudio] Destination: (?<filename>.+)");
    /**
     * The url to download
     */
    private String url;
    /**
     * The output directory
     */
    private String outputDir;
    /**
     * The output name
     */
    private String outputName = "%(title)s";
    /**
     * The format option
     */
    private FormatOption formatOption = FormatOption.MP4;
    /**
     * The download start callback
     */
    private DownloadStartCallback downloadStartCallback = null;
    /**
     * The download progress callback
     */
    private DownloadProgressCallback downloadProgressCallback = YtDlp.defaultCallBack();
    /**
     * The download end callback
     */
    private DownloadEndCallback downloadEndCallback = null;
    /**
     * True to download playlist videos into a subdirectory named after the playlist
     */
    private boolean subDirectoryPlaylist = false;


    public DownloadBuilder(String url, String outputDir) {
        this.url = url;
        this.outputDir = outputDir;
    }


    public DownloadBuilder setOutputName(String outputName) {
        this.outputName = outputName;
        return this;
    }

    /**
     * Sets the format option (default mp4)
     *
     * @return this
     */
    public DownloadBuilder setFormatOption(FormatOption formatOption) {
        this.formatOption = formatOption;
        return this;
    }

    /**
     * Sets the url
     *
     * @return this
     */
    public DownloadBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Sets the output directory
     *
     * @return this
     */
    public DownloadBuilder setOutputDir(String outputDir) {
        this.outputDir = outputDir;
        return this;
    }

    /**
     * Sets the download start callback
     *
     * @return this
     */
    public DownloadBuilder setDownloadStartCallback(DownloadStartCallback downloadStartCallback) {
        this.downloadStartCallback = downloadStartCallback;
        return this;
    }

    /**
     * Sets the download progress callback
     *
     * @return this
     */
    public DownloadBuilder setDownloadProgressCallback(DownloadProgressCallback downloadProgressCallback) {
        this.downloadProgressCallback = downloadProgressCallback;
        return this;
    }

    /**
     * Sets the download end callback
     */
    public DownloadBuilder setDownloadEndCallback(DownloadEndCallback downloadEndCallback) {
        this.downloadEndCallback = downloadEndCallback;
        return this;
    }

    /**
     * @param subDirectoryPlaylist true to download playlist videos into a subdirectory named after the playlist
     * @return this
     */
    public DownloadBuilder setSubDirectoryPlaylist(boolean subDirectoryPlaylist) {
        this.subDirectoryPlaylist = subDirectoryPlaylist;
        return this;
    }

    /**
     * Downloads a single video (if the url points to a playlist, the first video will be downloaded)
     *
     * @return the video info
     */
    public VideoFileInfo<VideoInfo> download() {
        Optional<VideoInfo> videoInfoOptional = getVideoInfo(requestBuilder().getUrl());
        if (videoInfoOptional.isEmpty()) throw new YtDlpException("Video not found");
        return download(videoInfoOptional.get(), requestBuilder());
    }

    /**
     * Downloads a playlist (for faster speeds but less information use {@link #downloadPlaylistShortened(boolean)} instead)
     * <br>
     * When the url points to a video as part of a playlist, the whole playlist will be downloaded
     *
     * @param parallel true to download videos in parallel (causes issues with the default progress callback implementation)
     * @return the playlist info
     * @throws YtDlpException when the playlist is not found / the url is not a playlist for non playlist urls use {@link #download()} instead
     */
    public PlaylistInfoData<PlaylistInfo, VideoInfo> downloadPlaylist(boolean parallel) {
        return downloadPlaylist(requestBuilder(), subDirectoryPlaylist, parallel, this::download);
    }

    /**
     * Downloads a playlist with shortened video info (a lot faster than using {@link #downloadPlaylist(boolean)}, at the cost of reduced video information)
     * <br>
     * When the url points to a video as part of a playlist, the whole playlist will be downloaded
     *
     * @param parallel true to download videos in parallel (causes issues with the default progress callback implementation)
     * @return the playlist info
     * @throws YtDlpException when the playlist is not found / the url is not a playlist for non playlist urls use {@link #download()} instead
     */
    public PlaylistInfoData<PlaylistPreviewInfo, VideoPreviewInfo> downloadPlaylistShortened(boolean parallel) throws YtDlpException {
        return downloadPlaylistShortened(requestBuilder(), subDirectoryPlaylist, parallel, this::download);
    }


    private YtDlpRequest requestBuilder() {
        YtDlpRequest request = new YtDlpRequest(url, outputDir);
        request.setDownloadStartCallback(downloadStartCallback);
        request.setDownloadProgressCallback(downloadProgressCallback);
        request.setDownloadEndCallback(downloadEndCallback);
        for (String param : formatOption.getParams()) {
            request.addOption(param);
        }
        request.addOption("--output", outputName + "." + formatOption.getExtension());

        return request;
    }

    private PlaylistInfoData<PlaylistInfo, VideoInfo> downloadPlaylist(YtDlpRequest request, boolean subDirectoryPlaylist, boolean parallel, BiFunction<VideoInfo, YtDlpRequest, VideoFileInfo<VideoInfo>> downloader) throws YtDlpException {
        PlaylistInfo playlistInfo = getPlaylistInfo(request.getUrl()).orElseThrow(() -> new YtDlpException("Playlist not found"));
        List<VideoInfo> videoInfos = playlistInfo.getEntries();


        String path = request.getDirectory();
        if (subDirectoryPlaylist) path = path + "\\" + playlistInfo.getTitle();
        Path.of(path).toFile().mkdirs();

        System.out.println("Downloading " + playlistInfo.getEntries().size() + " videos");
        if (!parallel) {
            List<VideoFileInfo<VideoInfo>> results = new ArrayList<>();
            for (VideoInfo videoInfo : videoInfos) {
                results.add(downloader.apply(videoInfo, request.clone().setDirectory(path).setUrl(videoInfo.getOriginalUrl())));
            }
            return new PlaylistInfoData<>(playlistInfo, results);
        }

        List<Callable<VideoFileInfo<VideoInfo>>> tasks = new ArrayList<>();

        String finalPath = path;
        for (VideoInfo videoInfo : videoInfos) {
            tasks.add(() -> downloader.apply(videoInfo, request.clone().setDirectory(finalPath).setUrl(videoInfo.getOriginalUrl())));
        }


        return new PlaylistInfoData<>(playlistInfo, executeTasks(tasks));
    }

    private PlaylistInfoData<PlaylistPreviewInfo, VideoPreviewInfo> downloadPlaylistShortened(YtDlpRequest request, boolean subDirectoryPlaylist, boolean parallel, BiFunction<VideoPreviewInfo, YtDlpRequest, VideoFileInfo<VideoPreviewInfo>> downloader) throws YtDlpException {
        PlaylistPreviewInfo playlistInfo = getPlaylistPreviewInfo(request.getUrl()).orElseThrow(() -> new YtDlpException("Playlist not found"));
        List<VideoPreviewInfo> videoInfos = playlistInfo.getEntries();
        if (videoInfos == null || videoInfos.isEmpty()) throw new YtDlpException("Not a Playlist!");
        String path = request.getDirectory();
        if (subDirectoryPlaylist) path = path + "\\" + playlistInfo.getTitle();
        Path.of(path).toFile().mkdirs();

        if (!parallel) {
            List<VideoFileInfo<VideoPreviewInfo>> results = new ArrayList<>();
            for (VideoPreviewInfo videoInfo : videoInfos) {
                results.add(downloader.apply(videoInfo, request.clone().setDirectory(path).setUrl(videoInfo.getUrl())));
            }
            return new PlaylistInfoData<>(playlistInfo, results);
        }

        List<Callable<VideoFileInfo<VideoPreviewInfo>>> tasks = new ArrayList<>();

        String finalPath = path;
        for (VideoPreviewInfo videoInfo : videoInfos) {
            tasks.add(() -> downloader.apply(videoInfo, request.clone().setDirectory(finalPath).setUrl(videoInfo.getUrl())));
        }

        System.out.println("Downloading " + playlistInfo.getEntries().size() + " videos");

        return new PlaylistInfoData<>(playlistInfo, executeTasks(tasks));

    }

    /**
     * Helper method to download media
     */
    private <T> VideoFileInfo<T> download(T info, YtDlpRequest request) throws YtDlpException {
        var response = YtDlp.execute(request);
        Path path = Path.of(request.getDirectory());

        String outputFileName = hasFileBeenDownloaded(response.getOut());
        if (outputFileName != null) {
            path = path.resolve(outputFileName);
            log.warning("File has already been downloaded");
        } else {
            String outputFile = extractFileName(response.getOut());
            if (outputFile == null) {
                if (info instanceof VideoInfo videoInfo) {
                    path = path.resolve(videoInfo.getTitle() + "." + formatOption.getExtension());
                } else if (info instanceof VideoPreviewInfo previewInfo) {
                    path = path.resolve(previewInfo.getTitle() + "." + formatOption.getExtension());
                }
                log.warning("Could not extract filename from output, using default video title instead (File output reference could point to a wrong location)");
            } else {
                path = Path.of(outputFile);
            }
        }
        return new VideoFileInfo<>(path.toFile(), info);
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
            Matcher audioMatcher = outputFileNameAudioPattern.matcher(line);
            if (audioMatcher.matches()) {
                return audioMatcher.group("filename");
            }
        }
        return null;
    }

    private static <T> List<VideoFileInfo<T>> executeTasks(List<Callable<VideoFileInfo<T>>> tasks) throws YtDlpException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<VideoFileInfo<T>>> futures = new ArrayList<>();

        for (Callable<VideoFileInfo<T>> task : tasks) {
            futures.add(executorService.submit(task));
        }

        List<VideoFileInfo<T>> results = new ArrayList<>();
        for (Future<VideoFileInfo<T>> future : futures) {
            try {
                results.add(future.get());
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

}

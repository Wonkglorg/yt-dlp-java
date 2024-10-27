package com.wonkglorg.ytdlp.callback;

/**
 * Called every update to the progress obtained from yt-dlp output
 */
@FunctionalInterface
public interface DownloadProgressCallback {
    void onProgressUpdate(ProgressCallBackData data);
}

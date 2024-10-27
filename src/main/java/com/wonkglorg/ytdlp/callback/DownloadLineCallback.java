package com.wonkglorg.ytdlp.callback;

/**
 * Called every line output from yt-dlp
 */
@FunctionalInterface
public interface DownloadLineCallback {
    void onLineOutput(String line);
}

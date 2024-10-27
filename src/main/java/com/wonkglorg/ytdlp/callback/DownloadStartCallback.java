package com.wonkglorg.ytdlp.callback;

/**
 * Gets called when a download starts
 */
@FunctionalInterface
public interface DownloadStartCallback {
    void onDownloadStart(String videoName, String videoUrl);
}

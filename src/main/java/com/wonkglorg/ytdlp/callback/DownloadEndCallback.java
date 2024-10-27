package com.wonkglorg.ytdlp.callback;

/**
 * Gets called when a download ended
 */
@FunctionalInterface
public interface DownloadEndCallback {
    void onDownloadEnd(String videoName, String videoUrl, long timeTakenMs);
}

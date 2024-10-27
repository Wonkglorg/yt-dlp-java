package com.wonkglorg.ytdlp.callback;

@FunctionalInterface
public interface DownloadEndCallback {
    void onDownloadEnd(String videoName, String videoUrl, long timeTakenMs);
}

package com.wonkglorg.ytdlp.callback;

@FunctionalInterface
public interface DownloadStartCallback {
    void onDownloadStart(String videoName, String videoUrl);
}

package com.wonkglorg.ytdlp.callback;

@FunctionalInterface
public interface DownloadProgressCallback {
    void onProgressUpdate(ProgressCallBackData data);
}

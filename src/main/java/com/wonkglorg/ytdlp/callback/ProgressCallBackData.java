package com.wonkglorg.ytdlp.callback;

public record ProgressCallBackData(String url, String fileName, float progressPercent, String totalFileSize,
                                   String downloadSpeed, long etaSeconds, int currFragment, int totalFragments) {
}

package com.wonkglorg.ytdlp.callback;

public record ProgressCallBackData(float progressPercent, String totalFileSize,
                                   String downloadSpeed, long etaSeconds,
                                   int currFragment, int totalFragments) {
}

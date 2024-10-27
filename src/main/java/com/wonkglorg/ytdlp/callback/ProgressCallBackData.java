package com.wonkglorg.ytdlp.callback;

import java.util.Objects;

public record ProgressCallBackData(String url, String fileName, float progressPercent, String totalFileSize,
                                   String downloadSpeed, long etaSeconds, int currFragment, int totalFragments) {
    @Override
    public String toString() {
        return "ProgressCallBackData{" +
                "url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", progressPercent=" + progressPercent +
                ", totalFileSize='" + totalFileSize + '\'' +
                ", downloadSpeed='" + downloadSpeed + '\'' +
                ", etaSeconds=" + etaSeconds +
                ", currFragment=" + currFragment +
                ", totalFragments=" + totalFragments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgressCallBackData that)) return false;
        return etaSeconds == that.etaSeconds && currFragment == that.currFragment && totalFragments == that.totalFragments && Float.compare(progressPercent, that.progressPercent) == 0 && Objects.equals(url, that.url) && Objects.equals(fileName, that.fileName) && Objects.equals(totalFileSize, that.totalFileSize) && Objects.equals(downloadSpeed, that.downloadSpeed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, fileName, progressPercent, totalFileSize, downloadSpeed, etaSeconds, currFragment, totalFragments);
    }
}

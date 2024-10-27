package com.wonkglorg.ytdlp.mapper;

import java.io.File;
import java.util.Objects;

public record VideoFileInfo<T>(File file, T videoInfo) {
    @Override
    public String toString() {
        return "VideoFileInfo{" +
                "file=" + file +
                ", videoInfo=" + videoInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoFileInfo<?> that)) return false;
        return Objects.equals(file, that.file) && Objects.equals(videoInfo, that.videoInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, videoInfo);
    }
}

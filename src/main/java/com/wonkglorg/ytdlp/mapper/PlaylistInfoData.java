package com.wonkglorg.ytdlp.mapper;

import java.util.List;
import java.util.Objects;

public record PlaylistInfoData<T, U>(T playlistData, List<VideoFileInfo<U>> videoFileInfo) {
    @Override
    public String toString() {
        return "PlaylistInfoData{" +
                "playlistData=" + playlistData +
                ", videoFileInfo=" + videoFileInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaylistInfoData<?, ?> that)) return false;
        return Objects.equals(playlistData, that.playlistData) && Objects.equals(videoFileInfo, that.videoFileInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistData, videoFileInfo);
    }
}

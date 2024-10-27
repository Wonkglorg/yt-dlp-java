package com.wonkglorg.ytdlp.mapper;

import java.util.List;

public record PlaylistInfoData<T, U>(T playlistData, List<VideoFileInfo<U>> videoFileInfo) {
}

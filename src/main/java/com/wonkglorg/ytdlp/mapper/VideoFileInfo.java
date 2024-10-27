package com.wonkglorg.ytdlp.mapper;

import java.io.File;

public record VideoFileInfo<T>(File file, T videoInfo) {
}

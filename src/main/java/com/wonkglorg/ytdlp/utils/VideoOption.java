package com.wonkglorg.ytdlp.utils;

public enum VideoOption {
    MP4("mp4"),

    WEBM("webm");

    private final String extension;

    VideoOption(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}

package com.wonkglorg.ytdlp.utils;

public enum AudioOption {
    MP3("mp3", "--audio-format mp3"),
    FLAC("flac", "--audio-format flac"),
    WAV("wav", "--audio-format wav"),
    OPUS("opus", "--audio-format opus");

    private final String extension;
    private final String params;

    AudioOption(String extension, String params) {
        this.extension = extension;
        this.params = params;
    }

    public String getParams() {
        return params;
    }

    public String getExtension() {
        return extension;
    }
}

package com.wonkglorg.ytdlp.utils;

public enum FormatOption {
    MP3("mp3", "--extract-audio", "--audio-format mp3"),
    FLAC("flac", "--extract-audio", "--audio-format flac"),
    WAV("wav", "--extract-audio", "--audio-format wav"),
    OPUS("opus", "--extract-audio", "--audio-format opus"),
    MP4("mp4", ""),
    WEBM("webm", "");;

    private final String extension;
    private final String[] params;

    FormatOption(String extension, String... params) {
        this.extension = extension;
        this.params = params;
    }

    public String[] getParams() {
        return params;
    }

    public String getExtension() {
        return extension;
    }
}

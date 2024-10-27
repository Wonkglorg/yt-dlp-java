package com.wonkglorg.ytdlp.utils;

public enum ConsoleColor {
    RESET("\033[0m"),
    RED("\033[31m"),
    GREEN("\033[32m"),
    YELLOW("\033[33m"),
    BLUE("\033[34m"),
    MAGENTA("\033[35m"),
    CYAN("\033[36m"),
    GRAY("\033[37m"),

    BRIGHT_GRAY("\033[90m"),
    BRIGHT_RED("\033[91m"),
    BRIGHT_GREEN("\033[92m"),
    BRIGHT_YELLOW("\033[93m"),
    BRIGHT_BLUE("\033[94m"),
    BRIGHT_MAGENTA("\033[95m"),
    BRIGHT_CYAN("\033[96m"),
    BRIGHT_WHITE("\033[97m"),

    BACKGROUND_BLACK("\033[40m"),
    BACKGROUND_RED("\033[41m"),
    BACKGROUND_GREEN("\033[42m"),
    BACKGROUND_YELLOW("\033[43m"),
    BACKGROUND_BLUE("\033[44m"),
    BACKGROUND_MAGENTA("\033[45m"),
    BACKGROUND_CYAN("\033[46m"),
    BACKGROUND_WHITE("\033[47m"),

    BRIGHT_BACKGROUND_BLACK("\033[100m"),
    BRIGHT_BACKGROUND_RED("\033[101m"),
    BRIGHT_BACKGROUND_GREEN("\033[102m"),
    BRIGHT_BACKGROUND_YELLOW("\033[103m"),
    BRIGHT_BACKGROUND_BLUE("\033[104m"),
    BRIGHT_BACKGROUND_MAGENTA("\033[105m"),
    BRIGHT_BACKGROUND_CYAN("\033[106m"),
    BRIGHT_BACKGROUND_WHITE("\033[107m");;

    private final String code;

    ConsoleColor(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}

package com.wonkglorg.ytdlp.utils;

import com.wonkglorg.ytdlp.callback.DownloadProgressCallback;
import com.wonkglorg.ytdlp.callback.ProgressCallBackData;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamProcessExtractor extends Thread {
    private static final String GROUP_SIZE = "size";
    private static final String GROUP_SPEED = "speed";
    private static final String GROUP_FRAG_CURRENT = "fragCurrent";
    private static final String GROUP_FRAG_MAX = "fragMax";
    private static final String GROUP_PERCENT = "percent";
    private static final String GROUP_MINUTES = "minutes";
    private static final String GROUP_SECONDS = "seconds";
    private final InputStream stream;
    private final StringBuilder buffer;
    private final DownloadProgressCallback callback;

    private Pattern pattern =
            Pattern.compile(
                    "\\[download]\\s+(?<percent>\\d+\\.\\d+)%\\s+of\\s+~?\\s+(?<size>\\d+\\.\\d+\\w+)\\s+at\\s+(?<speed>\\d+\\.\\d+\\w+/s)\\s+ETA\\s+(?<minutes>\\d+):(?<seconds>\\d+)\\s+\\(frag\\s+(?<fragCurrent>\\d+)/(?<fragMax>\\d+)\\)");

    public StreamProcessExtractor(
            StringBuilder buffer, InputStream stream, DownloadProgressCallback callback) {
        this.stream = stream;
        this.buffer = buffer;
        this.callback = callback;
        this.start();
    }

    @Override
    public void run() {
        try {
            StringBuilder currentLine = new StringBuilder();
            int nextChar;
            while ((nextChar = stream.read()) != -1) {
                buffer.append((char) nextChar);
                if (nextChar == '\r' && callback != null) {
                    processOutputLine(currentLine.toString());
                    currentLine.setLength(0);
                    continue;
                }
                currentLine.append((char) nextChar);
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    private void processOutputLine(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            callback.onProgressUpdate(constructCallBackData(matcher));
        }
    }

    private ProgressCallBackData constructCallBackData(Matcher matcher) {
        float progress = Float.parseFloat(matcher.group(GROUP_PERCENT));
        long eta = convertToSeconds(matcher.group(GROUP_MINUTES), matcher.group(GROUP_SECONDS));
        String speed = matcher.group(GROUP_SPEED);
        String totalFileSize = matcher.group(GROUP_SIZE);
        int currFragment = Integer.parseInt(matcher.group(GROUP_FRAG_CURRENT));
        int totalFragments = Integer.parseInt(matcher.group(GROUP_FRAG_MAX));
        return new ProgressCallBackData(progress, totalFileSize, speed, eta, currFragment, totalFragments);
    }

    private int convertToSeconds(String minutes, String seconds) {
        return Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds);
    }
}

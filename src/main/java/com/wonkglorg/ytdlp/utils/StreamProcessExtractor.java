package com.wonkglorg.ytdlp.utils;

import com.wonkglorg.ytdlp.callback.DownloadProgressCallback;
import com.wonkglorg.ytdlp.callback.ProgressCallBackData;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamProcessExtractor extends Thread {
    private static final Logger log = Logger.getLogger(StreamProcessExtractor.class.getName());
    private static final String GROUP_SIZE = "size";
    private static final String GROUP_SPEED = "speed";
    private static final String GROUP_FRAG_CURRENT = "fragCurrent";
    private static final String GROUP_FRAG_MAX = "fragMax";
    private static final String GROUP_PERCENT = "percent";
    private static final String GROUP_MINUTES = "minutes";
    private static final String GROUP_SECONDS = "seconds";
    private static final String GROUP_URL = "url";
    private static final String GROUP_FILENAME = "filename";
    private final InputStream stream;
    private final StringBuilder buffer;
    private final DownloadProgressCallback callback;
    private String destinationFile = null;
    private String url = null;
    private boolean hasReadHeader = false;

    private final Pattern downloadProgressPattern =
            Pattern.compile(
                    "\\[download]\\s+(?<percent>\\d+\\.\\d+)%\\s+of\\s+~?\\s+(?<size>\\d+\\.\\d+\\w+)\\s+at\\s+(?<speed>\\d+\\.\\d+\\w+/s)\\s+ETA\\s+(?<minutes>\\d+):(?<seconds>\\d+)\\s+\\(frag\\s+(?<fragCurrent>\\d+)/(?<fragMax>\\d+)\\)");

    private final Pattern urlPattern = Pattern.compile("\\[youtube] Extracting URL: (?<url>https?://\\S+)");

    private final Pattern destinationPattern = Pattern.compile("\\[download] Destination: (?<filename>.+)");

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
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private void processOutputLine(String line) {
        Matcher downloadMatcher = downloadProgressPattern.matcher(line);
        if (downloadMatcher.matches()) {
            callback.onProgressUpdate(constructCallBackData(downloadMatcher));
            return;
        }
        //reads header data (its sent as 1 line with \n separated lines)
        if (!hasReadHeader) {
            extractHeaderData(line);
            hasReadHeader = true;
        }
    }

    private void extractHeaderData(String line) {
        String[] headers = line.split("\n");
        for (String header : headers) {
            Matcher urlMatcher = urlPattern.matcher(header);
            if (urlMatcher.matches()) {
                url = urlMatcher.group(GROUP_URL);
                continue;
            }

            Matcher destinationMatcher = destinationPattern.matcher(header);
            if (destinationMatcher.matches()) {
                String file = destinationMatcher.group(GROUP_FILENAME);

                if (url != null && url.split("=").length > 1) {
                    file = file.replace("[" + url.split("=")[1] + "]", "");
                }
                file = file.replace(" .f616", "");
                destinationFile = file;
            }
        }
    }

    private ProgressCallBackData constructCallBackData(Matcher matcher) {
        float progress = Float.parseFloat(matcher.group(GROUP_PERCENT));
        long eta = convertToSeconds(matcher.group(GROUP_MINUTES), matcher.group(GROUP_SECONDS));
        String speed = matcher.group(GROUP_SPEED);
        String totalFileSize = matcher.group(GROUP_SIZE);
        int currFragment = Integer.parseInt(matcher.group(GROUP_FRAG_CURRENT));
        int totalFragments = Integer.parseInt(matcher.group(GROUP_FRAG_MAX));
        return new ProgressCallBackData(url, destinationFile, progress, totalFileSize, speed, eta, currFragment, totalFragments);
    }

    private int convertToSeconds(String minutes, String seconds) {
        return Integer.parseInt(minutes) * 60 + Integer.parseInt(seconds);
    }
}

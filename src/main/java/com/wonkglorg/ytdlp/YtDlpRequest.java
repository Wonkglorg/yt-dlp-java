package com.wonkglorg.ytdlp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * YtDlp request
 */
public class YtDlpRequest {

    /**
     * Executable working directory
     */
    private String directory;

    /**
     * Video Url
     */
    private String url;

    /**
     * List of executable options
     */
    private final Map<String, String> options = new HashMap<>();

    /**
     * Constructor
     */
    public YtDlpRequest() {
    }

    /**
     * Construct a request with a videoUrl
     *
     * @param url
     */
    public YtDlpRequest(String url) {
        this.url = url;
    }


    /**
     * Construct a request with a videoUrl and working directory
     *
     * @param url
     * @param directory
     */
    public YtDlpRequest(String url, String directory) {
        this.url = url;
        this.directory = directory;
    }

    /**
     * Get the working directory in which the executable will be run
     *
     * @return Working directory
     */

    public String getDirectory() {
        return directory;
    }

    /**
     * Sets the working directory in which the executable will be run
     *
     * @param directory Working directory
     * @return YtDlpRequest
     */
    public YtDlpRequest setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public YtDlpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<String, String> getOption() {
        return options;
    }

    public YtDlpRequest addOption(String key) {
        options.put(key, null);
        return this;
    }

    public YtDlpRequest addOption(String key, String value) {
        options.put(key, value);
        return this;
    }

    public YtDlpRequest addOption(String key, int value) {
        options.put(key, String.valueOf(value));
        return this;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    /**
     * Transform options to a string that the executable will execute
     *
     * @return Command string
     */
    protected String buildOptions() {

        StringBuilder builder = new StringBuilder();

        // Set Url
        if (url != null) builder.append(url).append(" ");

        // Build options strings
        Iterator<Entry<String, String>> it = options.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> option = it.next();

            String name = option.getKey();
            String value = option.getValue();

            if (value == null) {
                String optionFormatted = String.format("%s", name).trim();
                builder.append(optionFormatted).append(" ");
                it.remove();
                continue;
            }

            String optionFormatted = String.format("%s %s", name, value).trim();
            builder.append(optionFormatted).append(" ");

            it.remove();
        }

        return builder.toString().trim();
    }
}

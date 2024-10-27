package com.wonkglorg.ytdlp;

import com.wonkglorg.ytdlp.callback.DownloadEndCallback;
import com.wonkglorg.ytdlp.callback.DownloadLineCallback;
import com.wonkglorg.ytdlp.callback.DownloadProgressCallback;
import com.wonkglorg.ytdlp.callback.DownloadStartCallback;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * YtDlp request
 */
public class YtDlpRequest implements Cloneable {

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

    private DownloadStartCallback downloadStartCallback;
    private DownloadEndCallback downloadEndCallback;
    private DownloadProgressCallback downloadProgressCallback = YtDlp.defaultCallBack();
    private DownloadLineCallback downloadLineCallback;

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

    /**
     * @return the video url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the video url (required)
     *
     * @param url the video url
     */
    public YtDlpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Gets the options set for this request
     */
    public Map<String, String> getOption() {
        return options;
    }

    /**
     * Add an option to the request
     *
     * @param key the option key
     * @return YtDlpRequest
     */
    public YtDlpRequest addOption(String key) {
        options.put(key, null);
        return this;
    }

    /**
     * Add an option to the request
     *
     * @param key   the option key
     * @param value the option value
     * @return YtDlpRequest
     */
    public YtDlpRequest addOption(String key, String value) {
        options.put(key, value);
        return this;
    }

    /**
     * Add an option to the request
     *
     * @param key   the option key
     * @param value the option value
     * @return YtDlpRequest
     */
    public YtDlpRequest addOption(String key, int value) {
        options.put(key, String.valueOf(value));
        return this;
    }


    /**
     * @return {@link DownloadStartCallback}
     */
    public DownloadStartCallback getDownloadStartCallback() {
        return downloadStartCallback;
    }

    /**
     * Set the download start callback
     *
     * @param downloadStartCallback {@link DownloadStartCallback}
     */
    public void setDownloadStartCallback(DownloadStartCallback downloadStartCallback) {
        this.downloadStartCallback = downloadStartCallback;
    }

    /**
     * @return {@link DownloadEndCallback}
     */
    public DownloadEndCallback getDownloadEndCallback() {
        return downloadEndCallback;
    }

    /**
     * Set the download end callback
     *
     * @param downloadEndCallback {@link DownloadEndCallback}
     */
    public void setDownloadEndCallback(DownloadEndCallback downloadEndCallback) {
        this.downloadEndCallback = downloadEndCallback;
    }

    /**
     * @return {@link #downloadProgressCallback}
     */
    public DownloadProgressCallback getDownloadProgressCallback() {
        return downloadProgressCallback;
    }

    /**
     * Set the download progress callback
     *
     * @param downloadProgressCallback {@link DownloadProgressCallback}
     */
    public void setDownloadProgressCallback(DownloadProgressCallback downloadProgressCallback) {
        this.downloadProgressCallback = downloadProgressCallback;
    }

    /**
     * @return {@link com.wonkglorg.ytdlp.callback.DownloadLineCallback}
     */

    public DownloadLineCallback getDownloadLineCallback() {
        return downloadLineCallback;
    }

    /**
     * Set the download line callback
     *
     * @param downloadLineCallback {@link com.wonkglorg.ytdlp.callback.DownloadLineCallback}
     */
    public void setDownloadLineCallback(DownloadLineCallback downloadLineCallback) {
        this.downloadLineCallback = downloadLineCallback;
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

    @Override
    public YtDlpRequest clone() {
        try {
            YtDlpRequest request = (YtDlpRequest) super.clone();
            YtDlpRequest clone = new YtDlpRequest();
            clone.directory = this.directory;
            clone.url = this.url;
            clone.options.putAll(this.options);
            clone.downloadStartCallback = this.downloadStartCallback;
            clone.downloadEndCallback = this.downloadEndCallback;
            clone.downloadProgressCallback = this.downloadProgressCallback;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "YtDlpRequest{" +
                "directory='" + directory + '\'' +
                ", url='" + url + '\'' +
                ", options=" + options +
                ", downloadStartCallback=" + downloadStartCallback +
                ", downloadEndCallback=" + downloadEndCallback +
                ", downloadProgressCallback=" + downloadProgressCallback +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YtDlpRequest request)) return false;
        return Objects.equals(directory, request.directory) && Objects.equals(url, request.url) && Objects.equals(options, request.options) && Objects.equals(downloadStartCallback, request.downloadStartCallback) && Objects.equals(downloadEndCallback, request.downloadEndCallback) && Objects.equals(downloadProgressCallback, request.downloadProgressCallback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directory, url, options, downloadStartCallback, downloadEndCallback, downloadProgressCallback);
    }
}

package com.wonkglorg.ytdlp.mapper.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseVideoInfo {
    @JsonProperty("id")
    protected String id;
    @JsonProperty("title")
    protected String title;
    @JsonProperty("description")
    protected String description;
    @JsonProperty("thumbnails")
    protected List<Thumbnail> thumbnails;
    @JsonProperty("channel")
    protected String channel;
    @JsonProperty("channel_id")
    protected String channelId;
    @JsonProperty("channel_url")
    protected String channelUrl;
    @JsonProperty("duration")
    protected Long duration;
    @JsonProperty("view_count")
    protected Long viewCount;
    @JsonProperty("live_status")
    protected String liveStatus;
    @JsonProperty("release_timestamp")
    protected Long releaseTimestamp;
    @JsonProperty("timestamp")
    protected Long timestamp;
    @JsonProperty("availability")
    protected String availability;
    @JsonProperty("uploader")
    protected String uploader;
    @JsonProperty("uploader_id")
    protected String uploaderId;
    @JsonProperty("uploader_url")
    protected String uploaderUrl;
    @JsonProperty("_type")
    protected String type;
    @JsonProperty("channel_is_verified")
    protected boolean channelIsVerified;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Thumbnail> getThumbnails() {
        return thumbnails;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public Long getDuration() {
        return duration;
    }

    public long getViewCount() {
        return viewCount;
    }


    public String getLiveStatus() {
        return liveStatus;
    }

    public long getReleaseTimestamp() {
        return releaseTimestamp;
    }

    public String getChannel() {
        return channel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAvailability() {
        return availability;
    }


    public String getUploader() {
        return uploader;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public String getUploaderUrl() {
        return uploaderUrl;
    }

    public String getType() {
        return type;
    }

    public boolean isChannelIsVerified() {
        return channelIsVerified;
    }
}

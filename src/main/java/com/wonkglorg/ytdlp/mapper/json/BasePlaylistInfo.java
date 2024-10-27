package com.wonkglorg.ytdlp.mapper.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the data of a playlist
 */
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BasePlaylistInfo{

    /**
     * Playlist ID
     */
    @JsonProperty("id")
    protected String id;

    /**
     * Playlist title
     */
    @JsonProperty("title")
    protected String title;

    /**
     * Playlist availability (Public, Unlisted, Private)
     */
    @JsonProperty("availability")
    protected String availability;

    /**
     * Number of followers of the channel
     */
    @JsonProperty("channel_follower_count")
    protected Integer channelFollowerCount;

    /**
     * Playlist description
     */
    @JsonProperty("description")
    protected String description;

    /**
     * Playlist tags
     */
    @JsonProperty("tags")
    protected List<String> tags;

    /**
     * Playlist thumbnails
     */
    @JsonProperty("thumbnails")
    protected List<Thumbnail> thumbnails;

    /**
     * Date of last modification
     */
    @JsonProperty("modified_date")
    protected String modifiedDate;

    /**
     * Number of views
     */
    @JsonProperty("view_count")
    protected Integer viewCount;

    /**
     * Number of Videos in the playlist
     */
    @JsonProperty("playlist_count")
    protected Integer playlistCount;

    /**
     * Channel name
     */
    @JsonProperty("channel")
    protected String channel;

    /**
     * Channel ID
     */
    @JsonProperty("channel_id")
    protected String channelId;

    /**
     * Uploader ID
     */
    @JsonProperty("uploader_id")
    protected String uploaderId;

    /**
     * Uploader name
     */
    @JsonProperty("uploader")
    protected String uploader;

    /**
     * Channel URL
     */
    @JsonProperty("channel_url")
    protected String channelUrl;

    /**
     * Uploader URL
     */
    @JsonProperty("uploader_url")
    protected String uploaderUrl;

    /**
     * Type, always "playlist" in case of a playlist
     */
    @JsonProperty("_type")
    protected String type;

    /**
     * Extractor key of the playlist
     */
    @JsonProperty("extractor_key")
    protected String extractorKey;

    /**
     * Extractor of the playlist
     */
    @JsonProperty("extractor")
    protected String extractor;

    /**
     * Webpage URL of the playlist
     */
    @JsonProperty("webpage_url")
    protected String webpageUrl;

    /**
     * Original URL of the playlist
     */
    @JsonProperty("original_url")
    protected String originalUrl;

    @JsonProperty("webpage_url_basename")
    protected String webpageUrlBasename;

    @JsonProperty("webpage_url_domain")
    protected String webpageUrlDomain;

    @JsonProperty("release_year")
    protected Integer releaseYear;

    @JsonProperty("epoch")
    protected Long epoch;

    @JsonProperty("__files_to_move")
    protected Object filesToMove;

    @JsonProperty("_version")
    protected DownloaderVersion version;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAvailability() {
        return availability;
    }

    public Integer getChannelFollowerCount() {
        return channelFollowerCount;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<Thumbnail> getThumbnails() {
        return thumbnails;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public Integer getPlaylistCount() {
        return playlistCount;
    }

    public String getChannel() {
        return channel;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public String getUploader() {
        return uploader;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public String getUploaderUrl() {
        return uploaderUrl;
    }

    public String getType() {
        return type;
    }

    public String getExtractorKey() {
        return extractorKey;
    }

    public String getExtractor() {
        return extractor;
    }

    public String getWebpageUrl() {
        return webpageUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getWebpageUrlBasename() {
        return webpageUrlBasename;
    }

    public String getWebpageUrlDomain() {
        return webpageUrlDomain;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Long getEpoch() {
        return epoch;
    }

    public Object getFilesToMove() {
        return filesToMove;
    }

    public DownloaderVersion getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "PlaylistPreviewInfo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", availability='" + availability + '\'' +
                ", channelFollowerCount=" + channelFollowerCount +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", thumbnails=" + thumbnails +
                ", modifiedDate='" + modifiedDate + '\'' +
                ", viewCount=" + viewCount +
                ", playlistCount=" + playlistCount +
                ", channel='" + channel + '\'' +
                ", channelId='" + channelId + '\'' +
                ", uploaderId='" + uploaderId + '\'' +
                ", uploader='" + uploader + '\'' +
                ", channelUrl='" + channelUrl + '\'' +
                ", uploaderUrl='" + uploaderUrl + '\'' +
                ", type='" + type + '\'' +
                ", extractorKey='" + extractorKey + '\'' +
                ", extractor='" + extractor + '\'' +
                ", webpageUrl='" + webpageUrl + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", webpageUrlBasename='" + webpageUrlBasename + '\'' +
                ", webpageUrlDomain='" + webpageUrlDomain + '\'' +
                ", releaseYear=" + releaseYear +
                ", epoch=" + epoch +
                ", filesToMove=" + filesToMove +
                ", version=" + version +
                '}';
    }
}

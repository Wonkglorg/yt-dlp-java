package com.wonkglorg.ytdlp.mapper.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoInfo extends BaseVideoInfo {

    @JsonProperty("formats")
    private List<VideoFormat> formats;
    @JsonProperty("thumbnail")
    private String thumbnail;
    @JsonProperty("average_rating")
    private String averageRating;
    @JsonProperty("age_limit")
    private int ageLimit;
    @JsonProperty("webpage_url")
    private String webpageUrl;
    @JsonProperty("categories")
    private List<String> categories;
    @JsonProperty("tags")
    private List<String> tags;
    @JsonProperty("playable_in_embed")
    private Boolean playableInEmbed;
    @JsonProperty("_format_sort_fields")
    private List<String> formatSortFields;
    @JsonProperty("automatic_captions")
    private Map<String, List<Caption>> automaticCaptions;
    @JsonProperty("subtitles")
    private Map<String, List<Subtitle>> subtitles;
    @JsonProperty("comment_count")
    private Long commentCount;
    @JsonProperty("chapters")
    private Object chapters;  // Assuming chapters can be null, Object type can be adjusted
    @JsonProperty("heatmap")
    private List<HeatmapData> heatmap;
    @JsonProperty("like_count")
    private Long likeCount;
    @JsonProperty("channel_follower_count")
    private Long channelFollowerCount;
    @JsonProperty("upload_date")
    private String uploadDate;
    @JsonProperty("original_url")
    private String originalUrl;
    @JsonProperty("webpage_url_basename")
    private String webpageUrlBasename;
    @JsonProperty("webpage_url_domain")
    private String webpageUrlDomain;
    @JsonProperty("extractor")
    private String extractor;
    @JsonProperty("extractor_key")
    private String extractorKey;
    @JsonProperty("playlist")
    private String playlist;
    @JsonProperty("playlist_uploader")
    private String playlistUploader;
    @JsonProperty("playlist_uploader_id")
    private String playlistUploaderId;
    @JsonProperty("playlist_channel")
    private String playlistChannel;
    @JsonProperty("playlist_channel_id")
    private String playlistChannelId;
    @JsonProperty("playlist_autonumber")
    private int playlistAutonumber;
    @JsonProperty("playlist_index")
    private Object playlistIndex;  // Assuming playlistIndex can be null, Object type can be adjusted
    @JsonProperty("display_id")
    private String displayId;
    @JsonProperty("fulltitle")
    private String fullTitle;
    @JsonProperty("duration_string")
    private String durationString;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("release_year")
    private Integer releaseYear;
    @JsonProperty("is_live")
    private Boolean isLive;
    @JsonProperty("was_live")
    private Boolean wasLive;
    @JsonProperty("requested_subtitles")
    private Object requestedSubtitles;  // Assuming requestedSubtitles can be null, Object type can be adjusted
    @JsonProperty("_has_drm")
    private Object hasDrm;  // Assuming hasDrm can be null, Object type can be adjusted
    @JsonProperty("epoch")
    private Long epoch;
    @JsonProperty("requested_formats")
    private List<RequestedFormat> requestedFormats;
    @JsonProperty("format")
    private String format;
    @JsonProperty("format_id")
    private String formatId;
    @JsonProperty("ext")
    private String ext;
    @JsonProperty("protocol")
    private String protocol;
    @JsonProperty("language")
    private String language;
    @JsonProperty("format_note")
    private String formatNote;
    @JsonProperty("filesize_approx")
    private Long filesizeApprox;
    @JsonProperty("tbr")
    private Double tbr;
    @JsonProperty("width")
    private Integer width;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("resolution")
    private String resolution;
    @JsonProperty("fps")
    private Double fps;
    @JsonProperty("dynamic_range")
    private String dynamicRange;
    @JsonProperty("vcodec")
    private String vcodec;
    @JsonProperty("vbr")
    private double vbr;
    @JsonProperty("stretched_ratio")
    private Object stretchedRatio;  // Assuming stretchedRatio can be null, Object type can be adjusted
    @JsonProperty("aspect_ratio")
    private double aspectRatio;
    @JsonProperty("acodec")
    private String acodec;
    @JsonProperty("abr")
    private double abr;
    @JsonProperty("asr")
    private int asr;
    @JsonProperty("audio_channels")
    private int audioChannels;
    @JsonProperty("_filename")
    private String filename;
    @JsonProperty("filename")
    private String fileName;
    @JsonProperty("_version")
    private DownloaderVersion version;

    public List<VideoFormat> getFormats() {
        return formats;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    public String getWebpageUrl() {
        return webpageUrl;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isPlayableInEmbed() {
        return playableInEmbed;
    }


    public List<String> getFormatSortFields() {
        return formatSortFields;
    }

    public Map<String, List<Caption>> getAutomaticCaptions() {
        return automaticCaptions;
    }

    public Map<String, List<Subtitle>> getSubtitles() {
        return subtitles;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public Object getChapters() {
        return chapters;
    }

    public List<HeatmapData> getHeatmap() {
        return heatmap;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public long getChannelFollowerCount() {
        return channelFollowerCount;
    }

    public String getUploadDate() {
        return uploadDate;
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

    public String getExtractor() {
        return extractor;
    }

    public String getExtractorKey() {
        return extractorKey;
    }

    public String getPlaylist() {
        return playlist;
    }

    public Object getPlaylistIndex() {
        return playlistIndex;
    }

    public String getDisplayId() {
        return displayId;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public String getDurationString() {
        return durationString;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public boolean isLive() {
        return isLive;
    }

    public boolean isWasLive() {
        return wasLive;
    }

    public Object getRequestedSubtitles() {
        return requestedSubtitles;
    }

    public Object getHasDrm() {
        return hasDrm;
    }

    public long getEpoch() {
        return epoch;
    }

    public List<RequestedFormat> getRequestedFormats() {
        return requestedFormats;
    }

    public String getFormat() {
        return format;
    }

    public String getFormatId() {
        return formatId;
    }

    public String getExt() {
        return ext;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getLanguage() {
        return language;
    }

    public String getFormatNote() {
        return formatNote;
    }

    public long getFilesizeApprox() {
        return filesizeApprox;
    }

    public double getTbr() {
        return tbr;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getResolution() {
        return resolution;
    }

    public double getFps() {
        return fps;
    }

    public String getDynamicRange() {
        return dynamicRange;
    }

    public String getVcodec() {
        return vcodec;
    }

    public double getVbr() {
        return vbr;
    }

    public Object getStretchedRatio() {
        return stretchedRatio;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public String getAcodec() {
        return acodec;
    }

    public double getAbr() {
        return abr;
    }

    public int getAsr() {
        return asr;
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileName() {
        return fileName;
    }


    public DownloaderVersion getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", formats=" + formats +
                ", thumbnails=" + thumbnails +
                ", thumbnail='" + thumbnail + '\'' +
                ", description='" + description + '\'' +
                ", channelId='" + channelId + '\'' +
                ", channelUrl='" + channelUrl + '\'' +
                ", duration=" + duration +
                ", viewCount=" + viewCount +
                ", averageRating='" + averageRating + '\'' +
                ", ageLimit=" + ageLimit +
                ", webpageUrl='" + webpageUrl + '\'' +
                ", categories=" + categories +
                ", tags=" + tags +
                ", playableInEmbed=" + playableInEmbed +
                ", liveStatus='" + liveStatus + '\'' +
                ", releaseTimestamp=" + releaseTimestamp +
                ", formatSortFields=" + formatSortFields +
                ", automaticCaptions=" + automaticCaptions +
                ", subtitles=" + subtitles +
                ", commentCount=" + commentCount +
                ", chapters=" + chapters +
                ", heatmap=" + heatmap +
                ", likeCount=" + likeCount +
                ", channel='" + channel + '\'' +
                ", channelFollowerCount=" + channelFollowerCount +
                ", channelIsVerified=" + channelIsVerified +
                ", uploader='" + uploader + '\'' +
                ", uploaderId='" + uploaderId + '\'' +
                ", uploaderUrl='" + uploaderUrl + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                ", timestamp=" + timestamp +
                ", availability='" + availability + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", webpageUrlBasename='" + webpageUrlBasename + '\'' +
                ", webpageUrlDomain='" + webpageUrlDomain + '\'' +
                ", extractor='" + extractor + '\'' +
                ", extractorKey='" + extractorKey + '\'' +
                ", playlist='" + playlist + '\'' +
                ", playlistUploader='" + playlistUploader + '\'' +
                ", playlistUploaderId='" + playlistUploaderId + '\'' +
                ", playlistChannel='" + playlistChannel + '\'' +
                ", playlistChannelId='" + playlistChannelId + '\'' +
                ", playlistAutonumber=" + playlistAutonumber +
                ", playlistIndex=" + playlistIndex +
                ", displayId='" + displayId + '\'' +
                ", fullTitle='" + fullTitle + '\'' +
                ", durationString='" + durationString + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", releaseYear=" + releaseYear +
                ", isLive=" + isLive +
                ", wasLive=" + wasLive +
                ", requestedSubtitles=" + requestedSubtitles +
                ", hasDrm=" + hasDrm +
                ", epoch=" + epoch +
                ", requestedFormats=" + requestedFormats +
                ", format='" + format + '\'' +
                ", formatId='" + formatId + '\'' +
                ", ext='" + ext + '\'' +
                ", protocol='" + protocol + '\'' +
                ", language='" + language + '\'' +
                ", formatNote='" + formatNote + '\'' +
                ", filesizeApprox=" + filesizeApprox +
                ", tbr=" + tbr +
                ", width=" + width +
                ", height=" + height +
                ", resolution='" + resolution + '\'' +
                ", fps=" + fps +
                ", dynamicRange='" + dynamicRange + '\'' +
                ", vcodec='" + vcodec + '\'' +
                ", vbr=" + vbr +
                ", stretchedRatio=" + stretchedRatio +
                ", aspectRatio=" + aspectRatio +
                ", acodec='" + acodec + '\'' +
                ", abr=" + abr +
                ", asr=" + asr +
                ", audioChannels=" + audioChannels +
                ", filename='" + filename + '\'' +
                ", fileName='" + fileName + '\'' +
                ", type='" + type + '\'' +
                ", version=" + version +
                '}';
    }
}

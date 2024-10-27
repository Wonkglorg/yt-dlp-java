package com.wonkglorg.ytdlp.mapper.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Shorter version of {@link VideoInfo} containing information retrieved from a flattened youtube playlist (Does not contain as many values but is alot faster to obtain)
 */
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoPreviewInfo extends BaseVideoInfo {
    @JsonProperty("ie_key")
    protected String ieKey;
    @JsonProperty("url")
    protected String url;
    @JsonProperty("__x_forwarded_for_ip")
    protected String xForwardedForIp;

    public String getIeKey() {
        return ieKey;
    }

    public String getUrl() {
        return url;
    }

    public String getxForwardedForIp() {
        return xForwardedForIp;
    }

    @Override
    public String toString() {
        return "VideoEntry{" +
                "type='" + type + '\'' +
                ", ieKey='" + ieKey + '\'' +
                ", id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", channelId='" + channelId + '\'' +
                ", channel='" + channel + '\'' +
                ", channelUrl='" + channelUrl + '\'' +
                ", uploader='" + uploader + '\'' +
                ", uploaderId='" + uploaderId + '\'' +
                ", uploaderUrl='" + uploaderUrl + '\'' +
                ", thumbnails=" + thumbnails +
                ", timestamp=" + timestamp +
                ", releaseTimestamp=" + releaseTimestamp +
                ", availability='" + availability + '\'' +
                ", viewCount=" + viewCount +
                ", liveStatus='" + liveStatus + '\'' +
                ", channelIsVerified=" + channelIsVerified +
                ", xForwardedForIp='" + xForwardedForIp + '\'' +
                '}';
    }
}

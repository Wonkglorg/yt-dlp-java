package com.wonkglorg.ytdlp.mapper.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Iterator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistPreviewInfo extends BasePlaylistInfo implements Iterator<VideoPreviewInfo> {

    @JsonProperty("entries")
    private List<VideoPreviewInfo> entries;

    public List<VideoPreviewInfo> getEntries() {
        return entries;
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
                ", entries=" + entries +
                '}';
    }

    @Override
    public boolean hasNext() {
        return entries.iterator().hasNext();
    }

    @Override
    public VideoPreviewInfo next() {
        return entries.iterator().next();
    }
}

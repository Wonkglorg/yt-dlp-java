package com.wonkglorg.ytdlp;

import com.wonkglorg.ytdlp.exception.YtDlpException;
import com.wonkglorg.ytdlp.mapper.json.VideoInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class YtDlpTest {

    private static final String DIRECTORY = System.getProperty("java.io.tmpdir");
    private static final String VIDEO_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    private static final String NONE_EXISTENT_VIDEO_URL =
            "https://www.youtube.com/watch?v=dQw4w9WgXcZ";

    private static final String PLAYLIST_URL = "https://www.youtube.com/playlist?list=OLAK5uy_nh1X4KzCgR37rgmHGTDx8u_R95S0vX4jA";
    private static final String PLAYLIST_SPECIFIC_VIDEO_URL = "https://www.youtube.com/watch?v=3NpjfRKOblg&list=OLAK5uy_nh1X4KzCgR37rgmHGTDx8u_R95S0vX4jA&index=1";

    /**
     * @Test public void testUsingOwnExecutablePath() throws YtDlpException {
     * YtDlp.setExecutablePath("/usr/bin/yt-dlp"); Assert.assertNotNull(YtDlp.getVersion()); }
     */
    @Test
    public void testGetVersion() throws YtDlpException {
        Assert.assertNotNull(YtDlp.getVersion());
    }

    @Test
    public void testElapsedTime() throws YtDlpException {

        long startTime = System.nanoTime();

        YtDlpRequest request = new YtDlpRequest();
        request.setOption("--version");
        YtDlpResponse response = YtDlp.execute(request);

        int elapsedTime = (int) (System.nanoTime() - startTime);

        Assert.assertTrue(elapsedTime > response.getElapsedTime());
    }

    @Test
    public void testSimulateDownload() throws YtDlpException {

        YtDlpRequest request = new YtDlpRequest();
        request.setUrl(VIDEO_URL);
        request.setOption("--simulate");

        YtDlpResponse response = YtDlp.execute(request);

        Assert.assertEquals("yt-dlp " + VIDEO_URL + " --simulate", response.getCommand());
    }

    @Test
    public void testDirectory() throws YtDlpException {

        YtDlpRequest request = new YtDlpRequest(VIDEO_URL, DIRECTORY);
        request.setOption("--simulate");

        YtDlpResponse response = YtDlp.execute(request);

        Assert.assertEquals(DIRECTORY, response.getDirectory());
    }

    @Test
    public void testGetVideoInfo() throws YtDlpException {
        Optional<VideoInfo> videoInfoOptional = YtDlp.getVideoInfo(VIDEO_URL);
        Assert.assertTrue(videoInfoOptional.isPresent());
    }

    @Test(expected = YtDlpException.class)
    public void testFailGetNonExistentVideoInfo() throws YtDlpException {
        YtDlp.getVideoInfo(NONE_EXISTENT_VIDEO_URL);
    }

    @Test
    public void testGetVideoPlaylistInfo() throws YtDlpException {
        Optional<VideoInfo> videoInfoOptional = YtDlp.getVideoInfo(PLAYLIST_URL);
        Assert.assertTrue(videoInfoOptional.isPresent());
    }

    @Test
    public void testGetVideoPlaylistInfoFromSpecificIndex() throws YtDlpException {
        Optional<VideoInfo> videoInfoOptional = YtDlp.getVideoInfo(PLAYLIST_SPECIFIC_VIDEO_URL);
        Assert.assertTrue(videoInfoOptional.isPresent());
    }

    @Test
    public void testGetPlaylistPreviewInfo() throws YtDlpException {
        Optional<VideoInfo> videoInfoOptional = YtDlp.getVideoInfo(PLAYLIST_URL);
        Assert.assertTrue(videoInfoOptional.isPresent());
    }

    @Test(expected = YtDlpException.class)
    public void testFailGetNonExistentPlaylistInfo() throws YtDlpException {
        YtDlp.getVideoInfo(NONE_EXISTENT_VIDEO_URL);
    }

    @Test(expected = YtDlpException.class)
    public void testFailGetNonExistentPlaylistPreviewInfo() throws YtDlpException {
        YtDlp.getVideoInfo(NONE_EXISTENT_VIDEO_URL);
    }

    @Test
    public void shouldBeValidPlaylist() throws YtDlpException {
        boolean isPlaylist = YtDlp.isPlaylist(PLAYLIST_URL);
        Assert.assertTrue(isPlaylist);
    }

    @Test
    public void shouldBeValidPlaylistFromSpecificIndex() throws YtDlpException {
        boolean isPlaylist = YtDlp.isPlaylist(PLAYLIST_SPECIFIC_VIDEO_URL);
        Assert.assertTrue(isPlaylist);
    }

    @Test
    public void shouldNotBeValidPlaylist() throws YtDlpException {
        boolean isPlaylist = YtDlp.isPlaylist(VIDEO_URL);
        Assert.assertFalse(isPlaylist);
    }

    @Test
    public void getYtdlpVersion() throws YtDlpException {
        String version = YtDlp.getVersion();
        Assert.assertNotNull(version);
    }

    @Test
    public void getFormats() throws YtDlpException {
        YtDlp.getFormats(VIDEO_URL);
    }


}

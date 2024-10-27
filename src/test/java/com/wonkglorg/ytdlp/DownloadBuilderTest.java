package com.wonkglorg.ytdlp;

import com.wonkglorg.ytdlp.builder.DownloadBuilder;
import com.wonkglorg.ytdlp.exception.YtDlpException;
import com.wonkglorg.ytdlp.utils.FormatOption;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class DownloadBuilderTest {

    private static final String DIRECTORY = Path.of(System.getProperty("java.io.tmpdir"), "ytdlp-java-test").toString();
    private static final String VIDEO_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    private static final String NONE_EXISTENT_VIDEO_URL =
            "https://www.youtube.com/watch?v=dQw4w9WgXcZ";

    private static final String PLAYLIST_URL = "https://www.youtube.com/playlist?list=OLAK5uy_nh1X4KzCgR37rgmHGTDx8u_R95S0vX4jA";
    private static final String PLAYLIST_SPECIFIC_VIDEO_URL = "https://www.youtube.com/watch?v=3NpjfRKOblg&list=OLAK5uy_nh1X4KzCgR37rgmHGTDx8u_R95S0vX4jA&index=1";

    @Before
    public void setup() {
        Path path = Path.of(DIRECTORY);
        File file = path.toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Test
    public void downloadPlaylistALlTypesParallel() {
        for (FormatOption formatOption : FormatOption.values()) {
            DownloadBuilder builder = new DownloadBuilder(PLAYLIST_SPECIFIC_VIDEO_URL, DIRECTORY);
            var output = builder.setFormatOption(formatOption).downloadPlaylistShortened(true);
            output.videoFileInfo().forEach(videoInfo -> {
                Assert.assertTrue(videoInfo.file().exists());
                try {
                    Files.delete(videoInfo.file().toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Test
    public void downloadPlaylistAllTypesSequential() {
        for (FormatOption formatOption : FormatOption.values()) {
            DownloadBuilder builder = new DownloadBuilder(PLAYLIST_SPECIFIC_VIDEO_URL, DIRECTORY);
            var output = builder.setFormatOption(formatOption).downloadPlaylistShortened(false);
            output.videoFileInfo().forEach(videoInfo -> {
                Assert.assertTrue(videoInfo.file().exists());
                try {
                    Files.delete(videoInfo.file().toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Test
    public void downloadPlaylistSubFolder() {
        DownloadBuilder builder = new DownloadBuilder(PLAYLIST_SPECIFIC_VIDEO_URL, DIRECTORY);
        var output = builder.setFormatOption(FormatOption.MP3).setSubDirectoryPlaylist(true).downloadPlaylistShortened(false);
        output.videoFileInfo().forEach(videoInfo -> {
            Assert.assertTrue(videoInfo.file().exists());
            try {
                Files.delete(videoInfo.file().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    @Test(expected = YtDlpException.class)
    public void downloadPlaylistShouldFailInvalidUrl() {
        DownloadBuilder builder = new DownloadBuilder(NONE_EXISTENT_VIDEO_URL, DIRECTORY);
        var output = builder.downloadPlaylistShortened(true);
    }

    @Test(expected = YtDlpException.class)
    public void downloadPlaylistShouldFailSingleVideo() {
        DownloadBuilder builder = new DownloadBuilder(VIDEO_URL, DIRECTORY);
        var output = builder.downloadPlaylistShortened(true);
    }


    @Test
    public void downloadSingleVideoAllFormats() throws IOException {
        for (FormatOption formatOption : FormatOption.values()) {
            System.out.println("\nDownloading " + formatOption.getExtension() + " to: " + DIRECTORY);
            DownloadBuilder builder = new DownloadBuilder(VIDEO_URL, DIRECTORY);
            var output = builder.setFormatOption(formatOption).download();
            Assert.assertTrue(output.file().exists());
            Files.delete(output.file().toPath());
        }

    }

    @Test
    public void downloadSingleVideoDefaultFormat() throws IOException {
        System.out.println("Downloading video to: " + DIRECTORY);
        DownloadBuilder builder = new DownloadBuilder(VIDEO_URL, DIRECTORY);
        var output = builder.download();
        Assert.assertTrue(output.file().exists());
        Files.delete(output.file().toPath());
    }

    @Test(expected = YtDlpException.class)
    public void downloadSingleVideoShouldFailInvalidUrl() {
        DownloadBuilder builder = new DownloadBuilder(NONE_EXISTENT_VIDEO_URL, DIRECTORY);
        var output = builder.download();
    }

    @Test
    public void downloadSingleVideoShouldDownloadIndex1FromPlaylist() {
        DownloadBuilder builder = new DownloadBuilder(PLAYLIST_URL, DIRECTORY);
        var output = builder.download();
    }
}

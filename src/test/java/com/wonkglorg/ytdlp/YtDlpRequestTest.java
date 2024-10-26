package com.wonkglorg.ytdlp;

import org.junit.Assert;
import org.junit.Test;

public class YtDlpRequestTest {

    @Test
    public void testBuildOptionStandalone() {

        YtDlpRequest request = new YtDlpRequest();
        request.addOption("--help");

        Assert.assertEquals("--help", request.buildOptions());
    }

    @Test
    public void testBuildOptionWithValue() {

        YtDlpRequest request = new YtDlpRequest();
        request.addOption("--password", "1234");

        Assert.assertEquals("--password 1234", request.buildOptions());
    }

    @Test
    public void testBuildChainOptionWithValue() {

        YtDlpRequest request = new YtDlpRequest();
        request.addOption("--password", "1234");
        request.addOption("--username", "1234");

        Assert.assertEquals("--password 1234 --username 1234", request.buildOptions());
    }
}

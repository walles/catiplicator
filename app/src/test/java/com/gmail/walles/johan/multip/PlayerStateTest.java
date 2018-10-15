package com.gmail.walles.johan.multip;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class PlayerStateTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testPersistence() throws Exception {
        File file = new File(testFolder.getRoot(), "playerstate");

        PlayerState playerState = PlayerState.fromFile(file);
        Assert.assertEquals(1, playerState.getLevel());

        playerState.increaseLevel();
        Assert.assertEquals(2, playerState.getLevel());

        // Create a new object with the same path and verify that it gets loaded correctly
        playerState = PlayerState.fromFile(file);
        Assert.assertEquals(2, playerState.getLevel());
    }
}

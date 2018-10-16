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

    @Test
    public void testRetries() throws Exception {
        File file = new File(testFolder.getRoot(), "playerstate");

        Challenge challenge = new Challenge();

        PlayerState playerState = PlayerState.fromFile(file);

        Assert.assertEquals(0, playerState.getRetries(challenge));

        // Success from no-failures-noted, should be a no-op
        playerState.noteSuccess(challenge);
        Assert.assertEquals(0, playerState.getRetries(challenge));

        // First failure, one retry
        playerState.noteFailure(challenge);
        Assert.assertEquals(1, playerState.getRetries(challenge));

        // One retry done, no more retries neede
        playerState.noteSuccess(challenge);
        Assert.assertEquals(0, playerState.getRetries(challenge));

        // Multiple failures, require three retries
        playerState.noteFailure(challenge);
        playerState.noteFailure(challenge);
        Assert.assertEquals(3, playerState.getRetries(challenge));

        // Verify that we can read retries from disk
        PlayerState recovered = PlayerState.fromFile(file);
        Assert.assertEquals(3, recovered.getRetries(challenge));
    }
}

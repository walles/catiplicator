package com.gmail.walles.johan.catiplicator;

import static org.hamcrest.CoreMatchers.is;

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
        File file = new File(testFolder.getRoot(), PlayerState.PLAYER_STATE_FILE_NAME);

        PlayerState playerState = com.gmail.walles.johan.catiplicator
                .PlayerState.fromFile(file);
        Assert.assertThat(playerState.getLevel(), is(1));

        playerState.increaseLevel();
        Assert.assertThat(playerState.getLevel(), is(2));

        // Create a new object with the same path and verify that it gets loaded correctly
        playerState = PlayerState.fromFile(file);
        Assert.assertThat(playerState.getLevel(), is(2));
    }

    @Test
    public void testRetries() throws Exception {
        File file = new File(testFolder.getRoot(), PlayerState.PLAYER_STATE_FILE_NAME);

        Challenge challenge = new Challenge(4, 7);

        PlayerState playerState = com.gmail.walles.johan.catiplicator
                .PlayerState.fromFile(file);

        Assert.assertThat(playerState.getRetryCount(challenge), is(0));

        // Success from no-failures-noted, should be a no-op
        playerState.noteSuccess(challenge);
        Assert.assertThat(playerState.getRetryCount(challenge), is(0));

        // First failure, one retry
        playerState.noteFailure(challenge);
        Assert.assertThat(playerState.getRetryCount(challenge), is(1));

        // One retry done, no more retries neede
        playerState.noteSuccess(challenge);
        Assert.assertThat(playerState.getRetryCount(challenge), is(0));

        // Multiple failures, require three retries
        playerState.noteFailure(challenge);
        playerState.noteFailure(challenge);
        Assert.assertThat(playerState.getRetryCount(challenge), is(3));

        // Verify that we can read retries from disk
        PlayerState recovered = com.gmail.walles.johan.catiplicator
                .PlayerState.fromFile(file);
        Assert.assertThat(recovered.getRetryCount(challenge), is(3));
    }
}

/*
 *
 *  * Copyright 2018, Johan Walles <johan.walles@gmail.com>
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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

/*
 * Copyright 2018, Johan Walles <johan.walles@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.gmail.walles.johan.catiplicator;

import android.content.Context;

import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

// Consider replacing Serializable with SQLite and Flyway to support database migrations
public class PlayerState implements Serializable {
    private static final long serialVersionUID = 3L;

    /**
     * Yes, it is true, this formula is correct :)
     */
    private static final double SKILL_LEVEL_RAISE_ON_SUCCESS =
            (100 - ChallengePicker.SUCCESS_PERCENTAGE) / (double)ChallengePicker.SUCCESS_PERCENTAGE;
    @NonNls
    static final String PLAYER_STATE_FILE_NAME = "player-state";

    private Map<Challenge, Integer> retriesNeeded = new HashMap<>();

    /**
     * The highest not-completed level.
     *
     * When the user starts a new level, this is the level they will end up on.
     */
    private int level = 1;

    private double skillLevel = 1.0;

    /**
     * This is our on-disk backing store.
     */
    private final File file;

    private PlayerState(File file) {
        this.file = file;
    }

    // This method has default protection for testing purposes
    static PlayerState fromFile(@NonNls File file) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (PlayerState)in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("PlayerState not found in " + file, e);
        } catch (FileNotFoundException e) {
            return new PlayerState(file);
        } catch (InvalidClassException e) {
            Timber.w(e, "Player state loading failed, starting over");
            return new PlayerState(file);
        }
    }

    public static PlayerState fromContext(Context context) throws IOException {
        return fromFile(new File(context.getFilesDir(), PLAYER_STATE_FILE_NAME));
    }

    /**
     * Atomically persist to disk via a tempfile
     */
    private void persist() throws IOException {
        File tempfile = new File(file.getPath());
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempfile))) {
            out.writeObject(this);
        }

        if (!tempfile.renameTo(file)) {
            @NonNls String message =
                    "Rename failed: " + tempfile.getAbsolutePath() + "->" + file.getAbsolutePath();
            throw new IOException(message);
        }
    }

    /**
     * This method is expected to be called from GameActivity when the level is completed
     */
    public void increaseLevel() throws IOException {
        level++;

        persist();
    }

    /**
     * What level number should be displayed to the player?
     *
     * @see #getSkillLevel()
     */
    public int getLevel() {
        return level;
    }

    /**
     * Decrease retries-count for this challenge when applicable.
     */
    public void noteSuccess(Challenge challenge) throws IOException {
        if (challenge.getDifficulty() >= skillLevel) {
            skillLevel += SKILL_LEVEL_RAISE_ON_SUCCESS;
        }

        if (!retriesNeeded.containsKey(challenge)) {
            // No retries required
            return;
        }

        int retries = retriesNeeded.get(challenge);
        retries--;

        if (retries <= 0) {
            // No more retries, woho!
            retriesNeeded.remove(challenge);
            persist();
            return;
        }

        retriesNeeded.put(challenge, retries);
        persist();
    }

    public void noteFailure(Challenge challenge) throws IOException {
        skillLevel -= 1.0;
        if (skillLevel <= 1.0) {
            skillLevel = 1.0;
        }

        int retries = 0;
        if (retriesNeeded.containsKey(challenge)) {
            retries = retriesNeeded.get(challenge);
        }

        if (retries == 0) {
            // First-time offender, request one more try
            retriesNeeded.put(challenge, 1);
        } else {
            // Multiple offenses, request three retries
            retriesNeeded.put(challenge, 3);
        }

        persist();
    }

    public int getRetryCount(Challenge challenge) {
        if (!retriesNeeded.containsKey(challenge)) {
            return 0;
        }

        return retriesNeeded.get(challenge);
    }

    /**
     * How skilled is this player?
     *
     * @see #getLevel()
     */
    public double getSkillLevel() {
        return skillLevel;
    }

    /**
     * Which challenges need to be retried?
     *
     * @see #getRetryCount(Challenge)
     */
    public Collection<Challenge> listRetries() {
        return retriesNeeded.keySet();
    }
}

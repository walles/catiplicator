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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ChallengePicker {
    private static final Random RANDOM = new Random();

    private interface DifficultyApprover {
        boolean approved(int difficulty);
    }

    public interface DifficultyEvaluator {
        int getBest(int a, int b);
    }

    /**
     * We should help the player succeed this much.
     */
    public static final int SUCCESS_PERCENTAGE = 80;

    /**
     * Maximum number of failures we want to see per round.
     */
    private static final int MAX_FAILURES = (10 * (100 - SUCCESS_PERCENTAGE)) / 100;

    private final static Set<Challenge> allChallenges = createChallengesSet();
    private static Set<Challenge> createChallengesSet() {
        Set<Challenge> returnMe = new HashSet<>();

        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                returnMe.add(new Challenge(i, j));
            }
        }

        return returnMe;
    }

    @Nullable
    private static Challenge pickChallengeToRepeat(LevelState levelState, PlayerState playerState) {
        List<Challenge> retryCandidates = new ArrayList<>();
        for (Challenge challenge: playerState.listRetries()) {
            if (levelState.usedChallenges.contains(challenge)) {
                // Already used, never mind
                continue;
            }

            if (challenge.getDifficulty() > playerState.getSkillLevel()) {
                // Too hard, ignore
                continue;
            }

            if (retryCandidates.isEmpty()) {
                // Add first candidate
                retryCandidates.add(challenge);
                continue;
            }

            int currentRetries = playerState.getRetryCount(retryCandidates.get(0));
            int candidateRetries = playerState.getRetryCount(challenge);
            if (candidateRetries > currentRetries) {
                // There are other candidates with fewer retries
                continue;
            }

            if (candidateRetries == currentRetries) {
                // This one is as good as the others, add it to the list
                retryCandidates.add(challenge);
                continue;
            }

            // This one requires fewer retries than the others, start the list over
            retryCandidates.clear();
            retryCandidates.add(challenge);
        }

        if (retryCandidates.isEmpty()) {
            return null;
        }

        return retryCandidates.get(RANDOM.nextInt(retryCandidates.size()));
    }

    @Nullable
    private static Challenge pickChallenge(LevelState levelState, DifficultyApprover filter, DifficultyEvaluator getBestDifficulty) {
        List<Challenge> candidates = new ArrayList<>();
        for (Challenge challenge: allChallenges) {
            if (levelState.usedChallenges.contains(challenge)) {
                // Already used, never mind
                continue;
            }

            if (!filter.approved(challenge.getDifficulty())) {
                // Not wanted, never mind
                continue;
            }

            candidates.add(challenge);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        List<Challenge> bestCandidates = new ArrayList<>();
        int difficulty = candidates.get(0).getDifficulty();
        for (Challenge challenge : candidates) {
            int bestDifficulty = getBestDifficulty.getBest(challenge.getDifficulty(), difficulty);
            if (bestDifficulty != difficulty) {
                // Old record beat
                bestCandidates.clear();
            }

            if (challenge.getDifficulty() == bestDifficulty) {
                bestCandidates.add(challenge);
                difficulty = challenge.getDifficulty();
            }
        }

        return bestCandidates.get(RANDOM.nextInt(bestCandidates.size()));
    }

    /**
     * Provide a not-yet-used challenge
     */
    @NonNull
    public static Challenge pickChallenge(LevelState levelState, final PlayerState playerState) {
        // From easy-enough to-repeat tasks, pick (the) one that's closest to done
        Challenge returnMe = pickChallengeToRepeat(levelState, playerState);
        if (returnMe != null) {
            return returnMe;
        }

        if (levelState.failureCount < MAX_FAILURES) {
            // From available current-level-and-above tasks, pick one from as low level as possible
            returnMe = pickChallenge(levelState,
                    difficulty -> difficulty >= playerState.getSkillLevel(),
                    Math::min);

            if (returnMe != null) {
                return returnMe;
            }

            // If our level is too high, just return random challenges
            return pickRandomChallenge(levelState);
        } else {
            // From available current-level-and-below tasks, pick one from as high level as possible
            returnMe = pickChallenge(levelState,
                    difficulty -> difficulty <= playerState.getSkillLevel(),
                    Math::max);
            assert returnMe != null;
            return returnMe;
        }
    }

    private static Challenge pickRandomChallenge(LevelState levelState) {
        List<Challenge> candidates = new ArrayList<>();
        for (Challenge challenge: allChallenges) {
            if (levelState.usedChallenges.contains(challenge)) {
                // Already used, never mind
                continue;
            }

            candidates.add(challenge);
        }

        return candidates.get(RANDOM.nextInt(candidates.size()));
    }
}

package com.gmail.walles.johan.multip;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ChallengePicker {
    private static final Random RANDOM = new Random();

    private interface ChallengeApprover {
        boolean approved(Challenge challenge);
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
    private static Challenge pickChallenge(LevelState levelState, ChallengeApprover filter) {
        List<Challenge> candidates = new ArrayList<>();
        for (Challenge challenge: allChallenges) {
            if (levelState.usedChallenges.contains(challenge)) {
                // Already used, never mind
                continue;
            }

            if (!filter.approved(challenge)) {
                // Not wanted, never mind
                continue;
            }

            candidates.add(challenge);
        }

        return candidates.get(RANDOM.nextInt(candidates.size()));
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

        // From available current-level tasks, pick one
        returnMe = pickChallenge(levelState,
                challenge -> challenge.getDifficulty() == (int)playerState.getSkillLevel());
        if (returnMe != null) {
            return returnMe;
        }

        if (levelState.failureCount < MAX_FAILURES) {
            // From available current-level-and-above tasks, pick one from as low level as possible
            returnMe = pickChallenge(levelState,
                    challenge -> challenge.getDifficulty() >= playerState.getSkillLevel());
            assert returnMe != null;
            return returnMe;
        } else {
            // From available current-level-and-below tasks, pick one
            returnMe = pickChallenge(levelState,
                    challenge -> challenge.getDifficulty() <= playerState.getSkillLevel());
            assert returnMe != null;
            return returnMe;
        }
    }
}

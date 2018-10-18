package com.gmail.walles.johan.multip;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ChallengePicker {
    private static final Random RANDOM = new Random();
    public static final double SUCCESS_PERCENTAGE = 80;

    private final static Map<Integer, Set<Challenge>> difficultyToChallenges = createChallengesMap();

    private static Map<Integer, Set<Challenge>> createChallengesMap() {
        Map<Integer, Set<Challenge>> returnMe = new HashMap<>();

        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                Challenge challenge = new Challenge(i, j);
                int difficulty = challenge.getDifficulty();

                Set<Challenge> challenges;
                if (returnMe.containsKey(difficulty)) {
                    challenges = returnMe.get(difficulty);
                } else {
                    challenges = new HashSet<>();
                    returnMe.put(difficulty, challenges);
                }
                challenges.add(challenge);
            }
        }

        return returnMe;
    }

    /**
     * Provide a not-yet-used challenge
     */
    public static Challenge pickChallenge(Set <Challenge> alreadyPicked, PlayerState playerState) {
        Challenge newChallenge;
        do {
            // FIXME: Throw exception after too many iterations
            int a = RANDOM.nextInt(10) + 1;
            int b = RANDOM.nextInt(10) + 1;
            newChallenge = new Challenge(a, b);
        } while (levelState.usedChallenges.contains(newChallenge));

        return newChallenge;
    }
}

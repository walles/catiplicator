package com.gmail.walles.johan.catiplicator;

import java.util.HashSet;
import java.util.Set;

class LevelState {
    public final Set<Challenge> usedChallenges = new HashSet<>();

    int failureCount = 0;
}

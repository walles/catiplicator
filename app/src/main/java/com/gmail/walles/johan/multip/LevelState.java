package com.gmail.walles.johan.multip;

import java.util.HashSet;
import java.util.Set;

class LevelState {
    public final Set<Challenge> usedChallenges = new HashSet<>();

    int failureCount = 0;
}

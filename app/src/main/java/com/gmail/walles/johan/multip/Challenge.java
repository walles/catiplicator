package com.gmail.walles.johan.multip;

import java.util.Random;

public class Challenge {
    private static final Random RANDOM = new Random();

    public final String question;
    public final String answer;

    public Challenge() {
        int a = RANDOM.nextInt(10) + 1;
        int b = RANDOM.nextInt(10) + 1;

        question = "" + a + "⋅" + b;
        answer = "" + b;
    }
}

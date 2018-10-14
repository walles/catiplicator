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
        answer = "" + a * b;
    }

    @Override
    public int hashCode() {
        return question.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Challenge)) {
            return false;
        }
        Challenge that = (Challenge)obj;

        return question.equals(that.question);
    }
}

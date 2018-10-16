package com.gmail.walles.johan.multip;

import java.io.Serializable;
import java.util.Random;

public class Challenge implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final transient Random RANDOM = new Random();

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

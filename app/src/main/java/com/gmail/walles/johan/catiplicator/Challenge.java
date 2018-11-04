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

import org.jetbrains.annotations.NonNls;

import java.io.Serializable;

public class Challenge implements Serializable {
    private static final long serialVersionUID = 2L;

    @NonNls
    public final String question;

    @NonNls
    public final String answer;

    private final int a;
    private final int b;

    public Challenge(int a, int b) {
        this.a = a;
        this.b = b;

        question = a + "⋅" + b;
        answer = String.valueOf(a * b);
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

    public int getDifficulty() {
        if (a == 1 || b == 1) {
            return 1;
        } else if (a == 10 || b == 10) {
            return 2;
        } else {
            return a + b;
        }
    }
}

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

import android.content.Context;
import android.media.SoundPool;
import android.support.annotation.RawRes;

import org.jetbrains.annotations.NonNls;

import timber.log.Timber;

class SoundEffect {
    private final SoundPool soundPool;
    private final int soundId;
    private final String name;
    private boolean playRequestedWhileLoading;

    /**
     * @param name Free text name of this sound, will be used in logging
     */
    public SoundEffect(Context context, @NonNls String name, @RawRes int resId) {
        this.soundPool = new SoundPool.Builder().setMaxStreams(2).build();
        this.name = name;
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status != 0) {
                Timber.w("Loading <%s> sound failed", name);
                return;
            }

            if (playRequestedWhileLoading) {
                Timber.i("Playing now-loaded sound <%s>", name);
                start();
            }
        });
        soundId = soundPool.load(context, resId, 1);
    }

    public void start() {
        int result = soundPool.play(soundId, 1, 1, 0, 0, 1);
        if (result == 0) {
            Timber.w("Playing <%s> sound failed", name);
            playRequestedWhileLoading = true;
        }
    }

    public void release() {
        soundPool.release();
    }
}

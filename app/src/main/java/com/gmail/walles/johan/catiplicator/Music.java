package com.gmail.walles.johan.catiplicator;

import android.content.Context;
import android.media.MediaPlayer;

public class Music {
    private final MediaPlayer music;

    public Music(Context context) {
        music = MediaPlayer.create(context, R.raw.bensound_sweet);
        setVolumePercent(music, 50);
        music.setLooping(true);
    }

    public void start() {
        music.start();
    }

    public void pause() {
        music.pause();
    }

    public void resume() {
        music.start();
    }

    /**
     * From: https://stackoverflow.com/a/12075910/473672
     * @param percent 0-100
     */
    private void setVolumePercent(MediaPlayer mediaPlayer, int percent) {
        final int maxVolume = 100;

        float log1=(float)(Math.log(maxVolume - percent)/Math.log(maxVolume));
        mediaPlayer.setVolume(1 - log1, 1 - log1);
    }

}

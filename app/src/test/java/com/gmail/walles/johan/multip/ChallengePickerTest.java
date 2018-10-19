package com.gmail.walles.johan.multip;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ChallengePickerTest {
    @Test
    public void testPickChallenge() {
        PlayerState playerState = Mockito.mock(PlayerState.class);
        Assert.assertNotNull(ChallengePicker.pickChallenge(new LevelState(), playerState));
    }
}

package com.gmail.walles.johan.multip;

import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ChallengePickerTest {
    @Test
    public void testPickChallenge() {
        PlayerState playerState = Mockito.mock(PlayerState.class);
        Assert.assertThat(
                ChallengePicker.pickChallenge(new LevelState(), playerState), notNullValue());
    }
}

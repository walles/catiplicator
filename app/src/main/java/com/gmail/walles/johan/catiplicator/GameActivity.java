package com.gmail.walles.johan.catiplicator;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class GameActivity extends AppCompatActivity {
    private Challenge challenge;
    private TextView question;
    private EditText answer;
    private final LevelState levelState = new LevelState();
    private PlayerState playerState;
    private MediaPlayer ding;

    private final Handler handler = new Handler(Looper.myLooper());
    private int correctCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ding = MediaPlayer.create(this, R.raw.ding);

        try {
            playerState = PlayerState.fromContext(this);
        } catch (IOException e) {
            throw new RuntimeException("Setting up player state failed", e);
        }

        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);

        answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This block intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (answer.getText().length() >= challenge.answer.length()) {
                    onTextEntryComplete(answer.getText());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This block intentionally left blank
            }
        });

        setNewChallenge();
    }

    private void onTextEntryComplete(CharSequence text) {
        try {
            handleAnswer(text);
        } catch (IOException e) {
            throw new RuntimeException("Updating player state failed", e);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showAnswerDialog(Runnable callAfter) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_redo_failed);

        TextView demoString = dialog.findViewById(R.id.demoString);
        demoString.setText(challenge.question + "=" + challenge.answer);

        TextView questionString = dialog.findViewById(R.id.questionString);
        questionString.setText(challenge.question + "=");

        EditText hintedAnswerBox = dialog.findViewById(R.id.hintedAnswerBox);
        hintedAnswerBox.setHint(challenge.answer);
        hintedAnswerBox.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(challenge.answer.length())});

        // Pop up the keyboard, inspired by https://stackoverflow.com/a/7435225/473672
        Window dialogWindow = dialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        hintedAnswerBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This block intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This block intentionally left blank
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != challenge.answer.length()) {
                    // User not done
                    return;
                }

                if (TextUtils.equals(s, challenge.answer)) {
                    // Freeze the UI and pause a bit before dropping the dialog box. We want
                    // the user to be able to see that they typed the right answer.
                    hintedAnswerBox.setTypeface(null, Typeface.BOLD);
                    hintedAnswerBox.setTextColor(0xff008000);
                    ding.start();

                    handler.postDelayed(() -> {
                        dialog.dismiss();
                        callAfter.run();
                    }, 2000);
                    return;
                }

                // Wrong answer, try again
                s.clear();
            }
        });

        dialog.show();
    }

    private void handleAnswer(CharSequence text) throws IOException {
        if (TextUtils.equals(text, challenge.answer)) {
            playerState.noteSuccess(challenge);
            correctCount++;

            ding.start();

            handler.postDelayed(this::handleChallengeDone, 1000);
        } else {
            playerState.noteFailure(challenge);
            levelState.failureCount++;
            showAnswerDialog(this::handleChallengeDone);
        }
    }

    private void handleChallengeDone() {
        if (levelState.usedChallenges.size() >= 10) {
            int finishedLevel = playerState.getLevel();
            try {
                playerState.increaseLevel();
            } catch (IOException e) {
                throw new RuntimeException("Persisting player state failed");
            }

            LevelFinishedActivity.start(this, finishedLevel, correctCount);
            finish();
        } else {
            setNewChallenge();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setNewChallenge() {
        challenge = ChallengePicker.pickChallenge(levelState, playerState);

        question.setText(challenge.question + "=");

        answer.setText("");
        answer.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(challenge.answer.length())});

        // FIXME: Somehow ask the system to pop up the soft keyboard if it isn't already visible.
        // This is a problem after having shown the wrong-answer-please-redo dialog.

        levelState.usedChallenges.add(challenge);
    }
}

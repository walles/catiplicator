package com.gmail.walles.johan.multip;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

    private interface RunnableWithIOException {
        void run() throws IOException;
    }

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
    private void showAnswerDialog(RunnableWithIOException callAfter) {
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
                if (TextUtils.equals(hintedAnswerBox.getText(), challenge.answer)) {
                    // Freeze the UI and pause a bit before dropping the dialog box. We want
                    // the user to be able to see that they typed the right answer.

                    // FIXME: Can we make the text stand more while still freezing the input?
                    hintedAnswerBox.setEnabled(false);

                    handler.postDelayed(() -> {
                        dialog.dismiss();
                        try {
                            callAfter.run();
                        } catch (IOException e) {
                            throw new RuntimeException("Finishing answer dialog failed", e);
                        }
                    }, 2500);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // If the user put in the correct answer that would have been caught in
                // onTextChanged(), and we would never have ended up here.
                if (s.length() == challenge.answer.length()) {
                    // Correct number of chars, but not the correct ones, start over
                    s.clear();
                }
            }
        });

        dialog.show();
    }

    private void handleAnswer(CharSequence text) throws IOException {
        if (TextUtils.equals(text, challenge.answer)) {
            playerState.noteSuccess(challenge);
            correctCount++;

            ding.start();

            handleChallengeDone();
        } else {
            playerState.noteFailure(challenge);
            levelState.failureCount++;
            showAnswerDialog(this::handleChallengeDone);
        }
    }

    private void handleChallengeDone() throws IOException {
        if (levelState.usedChallenges.size() >= 10) {
            int finishedLevel = playerState.getLevel();
            playerState.increaseLevel();

            LevelFinishedActivity.start(this, finishedLevel, correctCount);
            finish();
        } else {
            setNewChallenge();
        }
    }

    private void setNewChallenge() {
        challenge = ChallengePicker.pickChallenge(levelState, playerState);

        question.setText(challenge.question);
        answer.setText("");

        // FIXME: Somehow ask the system to pop up the soft keyboard if it isn't already visible.
        // This is a problem after having shown the wrong-answer-please-redo dialog.

        levelState.usedChallenges.add(challenge);
    }
}

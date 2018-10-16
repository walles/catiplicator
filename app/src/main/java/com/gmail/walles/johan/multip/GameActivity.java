package com.gmail.walles.johan.multip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GameActivity extends AppCompatActivity {
    private Challenge challenge;
    private TextView question;
    private EditText answer;
    private final Set<Challenge> usedChallenges = new HashSet<>();
    private PlayerState playerState;

    private int correctCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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

    private void handleAnswer(CharSequence text) throws IOException {
        if (TextUtils.equals(text, challenge.answer)) {
            Toast.makeText(this, "Correct, woho!", Toast.LENGTH_SHORT).show();
            playerState.noteSuccess(challenge);
            correctCount++;
        } else {
            Toast.makeText(this, "Wrong answer, sorry!", Toast.LENGTH_SHORT).show();
            playerState.noteFailure(challenge);
        }

        if (usedChallenges.size() >= 10) {
            int finishedLevel = playerState.getLevel();
            playerState.increaseLevel();

            LevelFinishedActivity.start(this, finishedLevel, correctCount);
            finish();
        } else {
            setNewChallenge();
        }
    }

    private void setNewChallenge() {
        // Create a new not-yet-used challenge
        Challenge newChallenge;
        do {
            // FIXME: Throw exception after too many iterations
            newChallenge = new Challenge();
        } while (usedChallenges.contains(newChallenge));
        challenge = newChallenge;

        question.setText(challenge.question);
        answer.setText("");

        usedChallenges.add(challenge);
    }
}

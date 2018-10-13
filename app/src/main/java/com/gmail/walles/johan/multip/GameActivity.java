package com.gmail.walles.johan.multip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {
    private Challenge challenge;
    private TextView question;
    private EditText answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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
        if (TextUtils.equals(text, challenge.answer)) {
            Toast.makeText(this, "Correct, woho!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong answer, sorry!", Toast.LENGTH_SHORT).show();
        }

        setNewChallenge();
    }

    private void setNewChallenge() {
        challenge = new Challenge();
        question.setText(challenge.question);
        answer.setText("");
    }
}

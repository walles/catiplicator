package com.gmail.walles.johan.multip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    private Challenge challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        challenge = new Challenge();
        ((TextView)findViewById(R.id.question)).setText(challenge.question);
    }
}

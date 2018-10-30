package com.gmail.walles.johan.multip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.io.IOException;

public class LevelLaunchingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_launching);

        int level;
        try {
            level = PlayerState.fromContext(this).getLevel();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find which level to start", e);
        }

        String level_n = getResources().getString(R.string.level_n);
        Button launchButton = findViewById(R.id.launch_level_button);
        launchButton.setText(String.format(level_n, level));
        launchButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent(LevelLaunchingActivity.this, GameActivity.class);
                    startActivity(intent);
                });
    }
}

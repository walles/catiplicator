package com.gmail.walles.johan.multip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

        Button launchButton = findViewById(R.id.launch_level_button);
        launchButton.setText("Level " + level);
        launchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LevelLaunchingActivity.this, GameActivity.class);
                        startActivity(intent);
                    }
                });
    }
}

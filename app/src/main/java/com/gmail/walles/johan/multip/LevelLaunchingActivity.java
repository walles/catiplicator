package com.gmail.walles.johan.multip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;

import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import timber.log.Timber;

public class LevelLaunchingActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        menu.findItem(R.id.about).setOnMenuItemClickListener(
                item -> {
                    showAboutDialog();
                    return true;
                });

        return true;
    }

    @SuppressWarnings({"resource", "IOResourceOpenedButNotSafelyClosed"})
    private void showAboutDialog() {
        String credits;
        try (InputStream inputStream = getResources().openRawResource(R.raw.credits)) {
            @NonNls final String BEGINNING_OF_INPUT = "\\A";
            credits = new Scanner(inputStream, StandardCharsets.UTF_8.name())
                    .useDelimiter(BEGINNING_OF_INPUT)
                    .next();
        } catch (IOException e) {
            Timber.e(e, "Unable to load credits resource");
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage(credits)
                .setCancelable(true)
                .setTitle(R.string.about)
                .setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

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

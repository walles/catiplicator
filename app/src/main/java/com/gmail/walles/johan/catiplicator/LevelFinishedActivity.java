package com.gmail.walles.johan.catiplicator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import org.jetbrains.annotations.NonNls;

public class LevelFinishedActivity extends MusicActivity {
    @NonNls
    private static final String EXTRA_LEVEL_NUMBER = "levelNumber";
    @NonNls
    private static final String EXTRA_CORRECT_COUNT = "correctCount";

    private SoundEffect wow;

    public static void start(Context context, int finishedLevel, int correctCount) {
        Intent intent = new Intent(context, LevelFinishedActivity.class);
        intent.putExtra(EXTRA_LEVEL_NUMBER, finishedLevel);
        intent.putExtra(EXTRA_CORRECT_COUNT, correctCount);

        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.quit_game_question)
                .setMessage(R.string.quit_game_question)
                .setPositiveButton(R.string.yes_quit, (dialog, which) -> finish())
                .setNegativeButton(R.string.no_keep_playing, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wow.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_finished);

        int levelNumber = getIntent().getIntExtra(EXTRA_LEVEL_NUMBER, -1);
        int correctCount = getIntent().getIntExtra(EXTRA_CORRECT_COUNT, -1);

        if (correctCount == 10) {
            wow = new SoundEffect(this, "happy kids", R.raw.happykids);
        } else {
            wow = new SoundEffect(this, "light applause", R.raw.lightapplause);
        }
        wow.start();

        TextView textView = findViewById(R.id.levelFinishedText);
        textView.setText(String.format(getResources().getString(R.string.level_n_finished_you_got_m_answers_right),
                levelNumber,
                correctCount));

        findViewById(R.id.levelFinishedContinue).setOnClickListener(
                v -> {
                    Intent intent = new Intent(LevelFinishedActivity.this, LevelLaunchingActivity.class);
                    startActivity(intent);
                });
    }
}

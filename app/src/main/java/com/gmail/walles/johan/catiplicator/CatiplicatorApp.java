package com.gmail.walles.johan.catiplicator;

import android.app.Application;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public class CatiplicatorApp extends Application {
    @Override public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static final class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, @Nullable String tag, @NotNull String message,
                @Nullable Throwable t) {
            // This method intentionally left blank for now
        }
    }
}

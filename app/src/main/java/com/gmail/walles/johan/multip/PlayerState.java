package com.gmail.walles.johan.multip;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Consider replacing Serializable with SQLite and Flyway to support database migrations
public class PlayerState implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The highest not-completed level.
     *
     * When the user starts a new level, this is the level they will end up on.
     */
    private int level = 1;

    /**
     * This is our on-disk backing store.
     */
    private final File file;

    private PlayerState(File file) {
        this.file = file;
    }

    // This method has default protection for testing purposes
    static PlayerState fromFile(File file) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (PlayerState) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("PlayerState not found in " + file, e);
        } catch (FileNotFoundException e) {
            return new PlayerState(file);
        }
    }

    public static PlayerState fromContext(Context context) throws IOException {
        return fromFile(new File(context.getFilesDir(), "player-state"));
    }

    /**
     * Atomically persist to disk via a tempfile
     */
    private void persist() throws IOException {
        File tempfile = new File(file.getPath());
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempfile))) {
            out.writeObject(this);
        }

        if (!tempfile.renameTo(file)) {
            throw new IOException("Rename failed: " + tempfile.getAbsolutePath() + "->" + file.getAbsolutePath());
        }
    }

    /*
     * This method is expected to be called from GameActivity when the level is completed
     */
    public void increaseLevel() throws IOException {
        level++;

        persist();
    }

    public int getLevel() {
        return level;
    }
}

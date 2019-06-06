package com.fang.spaceinvaders.game;

import android.content.Context;
import android.media.MediaPlayer;

import com.fang.spaceinvaders.R;

public class SFXController {

    private MediaPlayer explosionPlayer;
    private MediaPlayer invaderKilledPlayer;
    private MediaPlayer shootPlayer;
    private MediaPlayer ufoPlayer;

    public SFXController (Context context) {
        explosionPlayer = MediaPlayer.create(context, R.raw.explosion);
        invaderKilledPlayer = MediaPlayer.create(context, R.raw.invaderkilled);
        shootPlayer = MediaPlayer.create(context, R.raw.shoot);
        ufoPlayer = MediaPlayer.create(context, R.raw.ufo_lowpitch);
    }

    public void close() {
        explosionPlayer.release();
        invaderKilledPlayer.release();
        shootPlayer.release();
        ufoPlayer.release();
    }

    public void explosion() {
        explosionPlayer.start();
    }
    public void invaderKilled() {
        invaderKilledPlayer.start();
    }
    public void shoot() {
        shootPlayer.start();
    }
    public void ufo() {
        ufoPlayer.start();
    }
}

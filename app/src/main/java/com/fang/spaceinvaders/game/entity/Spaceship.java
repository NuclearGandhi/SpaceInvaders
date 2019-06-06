package com.fang.spaceinvaders.game.entity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Rect;

import com.fang.spaceinvaders.game.GameData;
import com.fang.spaceinvaders.game.util.Board;
import com.fang.spaceinvaders.game.util.Constants.EntityState;

import java.util.Random;

import static com.fang.spaceinvaders.game.util.Constants.STATE_ALIVE;
import static com.fang.spaceinvaders.game.util.Constants.STATE_DEAD;
import static com.fang.spaceinvaders.game.util.Constants.STATE_OFF_SCREEN;
import static com.fang.spaceinvaders.game.util.Constants.STATE_ON_SCREEN;

public class Spaceship extends Entity {

    public static final int MOVE_SPEED = 4;
    public static int sSpawnDelay = 360;
    public static int DEATH_SCORE = 300;
    public static final int DEATH_DELAY = 6;

    public static final Rect BOUNDS = new Rect(2, 4, 17, 10);
    public static final Rect BOUNDS_DEAD = new Rect(102, 3, 114, 10);

    private int deathDelay = DEATH_DELAY;
    private int spawnDelay = sSpawnDelay;

    private Bitmap aliveBitmap;
    private Bitmap deadBitmap;

    public Spaceship(int x, int y) {
        super(x, y, BOUNDS);

        setX(Board.WIDTH - getWidth());
        setY(GameData.sBoard.calcRowToY(0));
        aliveBitmap = getBitmap();
        deadBitmap = cutBitmapFromSprites(BOUNDS_DEAD, GameData.sDefaultBitmap);

        state = STATE_OFF_SCREEN | STATE_DEAD;
    }

    @Override
    public boolean update() {
        if (spawnDelay <= 0 && (state & STATE_ALIVE) != STATE_ALIVE) {
            revive();
        } else {
            spawnDelay--;
        }

        if ((state & STATE_ALIVE) == STATE_ALIVE) {
            move(-MOVE_SPEED, 0);
            if (isOutOfBounds()) {
                kill();
            }
        }

        if (state == (STATE_DEAD | STATE_ON_SCREEN))
            if (deathDelay != 0) {
                deathDelay--;
            } else {
                onDeath();
            }
        return !isOutOfBounds();
    }

    private void revive() {
        state = STATE_ALIVE | STATE_ON_SCREEN;
        spawnDelay = -1;
        setX(Board.WIDTH - getWidth());
        setBitmap(aliveBitmap);
    }

    public void kill() {
        if (isOutOfBounds()) state = STATE_DEAD | STATE_OFF_SCREEN;
        else state = STATE_DEAD | STATE_ON_SCREEN;
        deathDelay = DEATH_DELAY;
        sSpawnDelay = 360 + 40 - GameData.RANDOM.nextInt(80);
        spawnDelay = sSpawnDelay;
        setBitmap(deadBitmap);
    }

    public void onDeath() {
        deathDelay = DEATH_DELAY;
        state = STATE_OFF_SCREEN | STATE_DEAD;
        setX(-200);
    }
}

package com.fang.spaceinvaders.game.entity;

import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.core.content.ContextCompat;

import com.fang.spaceinvaders.fragment.GameFragment;
import com.fang.spaceinvaders.game.util.Board;
import com.fang.spaceinvaders.game.GameData;
import com.fang.spaceinvaders.game.util.Constants;

import org.greenrobot.eventbus.EventBus;

import static com.fang.spaceinvaders.game.util.Constants.*;

/**
 * The entity the player controls. It can shoot lasers to kill monsters, and it dies when hit by one. A player can only move horizontally.
 */
public class Player extends Entity implements IShooter {

    /**
     * How many pixels the player moves each frame
     */
    private static final int MOVE_SPEED = 10;

    /**
     * Defines the amount of frames the player can't shoot after shooting a laser
     */
    private static final int MAX_LASER_DELAY = 25;
    private int laserTimer = MAX_LASER_DELAY; //In how many frames the player can shoot again

    private static final int MAX_DEATH_DELAY = 6;
    private int deathTimer = MAX_DEATH_DELAY;

    public static final Rect BOUNDS = new Rect(36, 18, 48, 25);
    public static final Rect BOUNDS_DEAD = new Rect(102, 3, 114, 10);

    private boolean isMoving = false;
    private boolean moveLeft = false;

    private Bitmap deadBitmap;

    public Player(int x, int y) {
        super(x, y, BOUNDS);
        deadBitmap = cutBitmapFromSprites(BOUNDS_DEAD, GameData.sDefaultBitmap);
    }

    @Override
    public void move(int x, int y) {
        super.move(x, y);
        if (getX() < 0) setX(0);
        else if (getX() + getWidth() > Board.WIDTH) setX(Board.WIDTH - getWidth());
    }

    @Override
    public boolean update() {
        if (deathTimer == 0) onDeath();
        if (state == (STATE_DEAD | STATE_ON_SCREEN)) {
            deathTimer--;
        } else if ((state & STATE_ALIVE) == STATE_ALIVE) {
            if (isMoving) {
                if (moveLeft) move(-MOVE_SPEED, 0);
                else move(MOVE_SPEED, 0);
            }

            //Update the laser timer accordingly
            if (laserTimer > 0) {
                laserTimer--;
            }
        }
        return (state & STATE_ON_SCREEN) == STATE_ON_SCREEN;
    }

    public void startMoving(boolean left) {
        isMoving = true;
        moveLeft = left;
    }

    public void stopMoving() {
        isMoving = false;
    }

    @Override
    public boolean shoot() {
        if (laserTimer != 0) return false;
        Laser laser = new PLaser(this);
        GameData.sLasers.add(laser);
        laserTimer = MAX_LASER_DELAY;
        return true;
    }

    public void kill() {
        setBitmap(deadBitmap);
        state = STATE_DEAD | STATE_ON_SCREEN;
    }

    private void onDeath() {
        EventBus.getDefault().post(new GameFragment.GameOverEvent());
        state = STATE_DEAD | STATE_OFF_SCREEN;
    }
}

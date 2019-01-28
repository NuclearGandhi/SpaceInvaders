package com.fang.spaceinvaders.game.entity;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.fang.spaceinvaders.game.Board;

import java.util.List;

/**
 * The entity the player controls. It can shoot lasers to kill monsters, and it dies when hit by one. A player can only move horizontally.
 */
public class Player extends Entity {

    /**
     * How many pixels the player moves each frame
     */
    private static final int MOVE_SPEED = 10;

    /**
     * Defines the amount of frames the player can't shoot after shooting a laser
     */
    private static final int MAX_LASER_DELAY = 20;

    private static final Rect BOUNDS = new Rect(36, 18, 48, 25);

    private int laserTimer = MAX_LASER_DELAY; //In how many frames the player can shoot again
    private boolean isMoving = false;
    private boolean moveLeft = false;

    public Player(int x, int y, Bitmap spritesBitmap) {
        super(x, y, BOUNDS, spritesBitmap);
    }

    @Override
    public void move(int x, int y) {
        super.move(x, y);
        if (getX() < 0) setX(0);
        else if (getX() + getWidth() > Board.WIDTH) setX(Board.WIDTH - getWidth());
    }

    @Override
    public boolean update() {
        if (isMoving) {
            if (moveLeft) move(-MOVE_SPEED, 0);
            else move(MOVE_SPEED, 0);
        }

        //Update the laser timer accordingly
        if (laserTimer > 0) {
            laserTimer--;
        }
        return true;
    }

    public void startMoving(boolean left) {
        isMoving = true;
        moveLeft = left;
    }

    public void stopMoving() {
        isMoving = false;
    }

    /**
     * Create a laser in the player position. Doesn't shoot if the player's {@code laserTimer}
     * didn't reach 0.
     *
     * @param lasers        the lasers list to update
     * @param spritesBitmap the bitmap to get the laser bitmap from
     * @return whether the player shot the laser or not
     */
    public boolean shoot(List<Laser> lasers, Bitmap spritesBitmap) {
        if (laserTimer != 0) return false;
        Laser laser = new PLaser(this, spritesBitmap);
        lasers.add(laser);
        laserTimer = MAX_LASER_DELAY;
        return true;
    }

    public void kill() {}
}

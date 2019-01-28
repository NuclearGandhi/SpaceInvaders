package com.fang.spaceinvaders.game.entity;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.fang.spaceinvaders.game.Board;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * A game object that it's goal is to defeat the player
 */
public class Monster extends Entity {

    public static final int SIZE = 16 * PIXEL_BITMAP_SCALE;
    public static int MOVE_SPEED = 10;
    public static int DEATH_DELAY = 6;

    /**
     * Monsters can have different types, and each type has different states. A type affects the monster's overall look, and the state is its different frames of animation.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_DEAD, TYPE_1_STATE_1, TYPE_1_STATE_2, TYPE_2_STATE_1, TYPE_2_STATE_2, TYPE_3_STATE_1, TYPE_3_STATE_2})
    public @interface MonsterTypeState {
    }

    public static final int TYPE_DEAD = 0;
    public static final int TYPE_1_STATE_1 = 1;
    public static final int TYPE_1_STATE_2 = 2;
    public static final int TYPE_2_STATE_1 = 3;
    public static final int TYPE_2_STATE_2 = 4;
    public static final int TYPE_3_STATE_1 = 5;
    public static final int TYPE_3_STATE_2 = 6;

    public static final Rect BOUNDS_DEAD = new Rect(102, 3, 114, 10);
    public static final Rect BOUNDS_1_STATE_1 = new Rect(21, 3, 32, 10);
    public static final Rect BOUNDS_1_STATE_2 = new Rect(36, 3, 47, 10);
    public static final Rect BOUNDS_2_STATE_1 = new Rect(51, 3, 61, 10);
    public static final Rect BOUNDS_2_STATE_2 = new Rect(65, 3, 75, 10);
    public static final Rect BOUNDS_3_STATE_1 = new Rect(79, 3, 86, 10);
    public static final Rect BOUNDS_3_STATE_2 = new Rect(91, 3, 98, 10);

    private boolean isDead = false;
    private @MonsterTypeState int type;
    private Bitmap stateBitmap;

    private int deathDelay = DEATH_DELAY;
    private Bitmap deadBitmap;

    public Monster(int x, int y, @MonsterTypeState int type, Bitmap spritesBitmap) {
        super(x, y, SIZE, SIZE, null);

        Rect bounds = boundsFromType(type);
        setBitmap(cutBitmapFromSprites(bounds, spritesBitmap));

        this.type = type;

        Rect stateBounds = boundsFromType(type + 1);
        stateBitmap = cutBitmapFromSprites(stateBounds, spritesBitmap);

        Rect deadBounds = boundsFromType(TYPE_DEAD);
        deadBitmap = cutBitmapFromSprites(deadBounds, spritesBitmap);
    }

    @Override
    public void move(int x, int y) {
        if (!isDead) {
            super.move(x, y);
        }
    }

    @Override
    public boolean update() {
        if (isDead && deathDelay != 0) deathDelay --;
        return !(deathDelay == 0);
    }

    /**
     * Change the state. In other words, animate.
     */
    public void changeState() {
        if (!isDead) {
            Bitmap temp = getBitmap();
            setBitmap(stateBitmap);
            stateBitmap = temp;
        }
    }

    /**
     * Get the bounds matching the monster type and state
     *
     * @param type the type
     * @return the matching bounds
     */
    public static Rect boundsFromType(@MonsterTypeState int type) {
        switch (type) {
            case TYPE_DEAD:
                return BOUNDS_DEAD;
            case TYPE_1_STATE_1:
                return BOUNDS_1_STATE_1;
            case TYPE_1_STATE_2:
                return BOUNDS_1_STATE_2;
            case TYPE_2_STATE_1:
                return BOUNDS_2_STATE_1;
            case TYPE_2_STATE_2:
                return BOUNDS_2_STATE_2;
            case TYPE_3_STATE_1:
                return BOUNDS_3_STATE_1;
            case TYPE_3_STATE_2:
                return BOUNDS_3_STATE_2;
            default:
                throw new IllegalArgumentException(type + " is not a valid @MonsterTypeState int");
        }
    }

    public void kill() {
        isDead = true;
        setBitmap(deadBitmap);
        setType(TYPE_DEAD);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

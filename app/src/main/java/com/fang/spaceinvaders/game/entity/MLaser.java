package com.fang.spaceinvaders.game.entity;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * A laser made by monsters
 */
public class MLaser extends Laser{

    public static final int MOVE_SPEED = 8;

    public static final int STATE_1 = 1;
    public static final int STATE_2 = 2;

    public static final Rect BOUNDS_STATE_1 = new Rect(100, 19, 102, 25);
    public static final Rect BOUNDS_STATE_2 = new Rect(105, 19, 107, 25);

    private int type = 1;
    private Bitmap stateBitmap;

    public MLaser(Monster monster, Bitmap spritesBitmap) {
        super(
                monster.getX() + monster.getWidth() / 2,
                monster.getY() + monster.getHeight(),
                BOUNDS_STATE_1,
                spritesBitmap);

        stateBitmap = cutBitmapFromSprites(BOUNDS_STATE_2, spritesBitmap);
    }

    @Override
    public boolean update() {
        move(0, MOVE_SPEED);
        changeState();
        return super.update();
    }

    public void changeState() {
        Bitmap temp = getBitmap();
        setBitmap(stateBitmap);
        stateBitmap = temp;
    }
}

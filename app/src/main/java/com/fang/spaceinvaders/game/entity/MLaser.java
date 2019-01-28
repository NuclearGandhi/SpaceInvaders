package com.fang.spaceinvaders.game.entity;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * A laser made by monsters
 */
public class MLaser extends Laser{

    public static final int MOVE_SPEED = 8;

    public static final Rect BOUNDS = new Rect(31, 21, 31, 24);

    public MLaser(Player player, Bitmap spritesBitmap) {
        super(
                player.getX() + player.getWidth() / 2,
                player.getY() - BOUNDS.height(),
                BOUNDS,
                spritesBitmap);
    }

    @Override
    public boolean update() {
        move(0, MOVE_SPEED);
        return super.update();
    }

}

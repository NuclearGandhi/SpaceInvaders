package com.fang.spaceinvaders.game.entity;

import android.graphics.Bitmap;
import android.graphics.Rect;

import androidx.annotation.CallSuper;

/**
 * A game object that is created by other entities inorder to destroy other entities. A laser can only move vertically.
 */
public abstract class Laser extends Entity {

    public Laser(int x, int y, Rect bounds) {
        super(x, y, bounds);
    }

    @Override
    @CallSuper
    public boolean update() {
        return !isOutOfBounds();
    }
}

package com.fang.spaceinvaders.game.entity;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.fang.spaceinvaders.game.util.Board;

/**
 * A basic game object
 */
public abstract class Entity {

    public static final int PIXEL_BITMAP_SCALE = 3;

    private int x;
    private int y;

    private int width;
    private int height;

    Bitmap bitmap;

    public Entity(int x, int y, int width, int height, Bitmap bitmap) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bitmap = bitmap;
    }

    public Entity(int x, int y, Rect bounds, Bitmap spritesBitmap) {
        this.x = x;
        this.y = y;
        this.width = (bounds.width() + 1) * PIXEL_BITMAP_SCALE;
        this.height = (bounds.height() + 1) * PIXEL_BITMAP_SCALE;
        this.bitmap = Bitmap.createBitmap(spritesBitmap, bounds.left, bounds.top, bounds.width() + 1, bounds.height() + 1);
    }

    /**
     * Move the entity {@code x} (board) pixels to the right and {@code y} pixels down.
     *
     * @param x the number of pixels to move to the right
     * @param y the number o pixels to move down
     */
    public void move(int x, int y) {
        this.x += x;
        this.y += y;
    }

    /**
     * @return whether the entity is out of the board bounds
     */
    public boolean isOutOfBounds() {
        return getY() < 0 || getY() - getHeight() > Board.HEIGHT ||
                getX() < 0 || getX() - getWidth() > Board.WIDTH;
    }

    /**
     * Update the entity, should be called once for each frame
     *
     * @return whether the entity still exists after the update operation
     */
    public abstract boolean update();

    public static Bitmap cutBitmapFromSprites(Rect bounds, Bitmap spritesBitmap) {
        return Bitmap.createBitmap(spritesBitmap,
                bounds.left,
                bounds.top,
                bounds.width() + 1,
                bounds.height() + 1);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

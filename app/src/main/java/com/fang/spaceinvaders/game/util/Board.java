package com.fang.spaceinvaders.game.util;

import android.graphics.Rect;

import com.fang.spaceinvaders.game.entity.Entity;

import androidx.annotation.Nullable;

/**
 * An object to position the different entities on a board that doesn't necessarily has the same aspect ratio and resolution as the device's screen.
 */
public class Board {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    public static final int HEADER_HEIGHT = 32;
    public static final int FOOTER_HEIGHT = 32;

    public static final int ROW_COUNT = 13;
    public static final int ROW_HEIGHT = 32;
    public static final int PLAYER_ROW = 12;
    public static final int WALLS_ROW = 11;

    private final int mXOffset;
    private final int mYOffset;
    private int mScale;

    /**
     * Creates a board. the board is defined by a pixel grid that sit on top of a higher resolution pixel grid (the screen).
     * It does so by creating a {@code mScale} defined by the screen height divided by the board height, and then calculates the board offset in the x and y axis.
     *
     * @param screenWidth  the screen width
     * @param screenHeight the screen height
     */
    public Board(int screenWidth, int screenHeight) {
        mScale = screenHeight / (HEIGHT);
        mXOffset = (screenWidth - mScale * WIDTH) / 2;
        mYOffset = (screenHeight - mScale * (HEIGHT)) / 2;
    }

    /**
     * Calculates a x screen position from an x board position
     *
     * @param x a x board position
     * @return a x screen position
     */
    public int pixelX(int x) {
        return x * mScale + mXOffset;
    }

    /**
     * Calculates a y screen position from an y board position
     *
     * @param y a y board position
     * @return a y screen position
     */
    public int pixelY(int y) {
        return y * mScale + mYOffset;
    }

    /**
     * Calculate the y position of a given row
     *
     * @param row a row index
     * @return the y position of that row (in board pixels)
     */
    public int calcRowToY(int row) {
        if (row >= ROW_COUNT)
            throw new IllegalArgumentException("Row index must be smaller than Board.ROW_COUNT");
        return mYOffset + HEADER_HEIGHT + row * ROW_HEIGHT;
    }

    public Rect rectFromEntity(Entity object, @Nullable Rect bounds) {
        int x = object.getX();
        int y = object.getY();

        int x2 = object.getX() + object.getWidth();
        int y2 = object.getY() + object.getHeight();

        if (bounds != null) {
            int xOffset = (object.getWidth() - bounds.width() * Entity.PIXEL_BITMAP_SCALE) / 2;
            int yOffset = (object.getHeight() - bounds.height() * Entity.PIXEL_BITMAP_SCALE) / 2;

            x += xOffset;
            x2 -= xOffset;
            y += yOffset;
            y2 -= yOffset;
        }

        return new Rect(pixelX(x), pixelY(y), pixelX(x2), pixelY(y2));
    }
}

package com.fang.spaceinvaders.game.util;

import android.graphics.Bitmap;

import com.fang.spaceinvaders.game.GameData;
import com.fang.spaceinvaders.game.entity.Monster;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntDef;

import static com.fang.spaceinvaders.game.entity.Monster.MOVE_SPEED;


/**
 * An {@link ArrayList} of {@link Monster}s. A {@link MonsterRow} is a row of monsters, and its behavior with other rows.
 * Behaviors:
 * 1.  A row only moves when {@code moveDelay} reaches 0, meaning it only moves every {@code sMaxMoveDelay} frames.
 * 2.  There is a delay between the movement of each row, that delay is {@code DELAY_DIFFERENCE}.
 * 3.  When a monster in any of the rows is updated to be out of bounds, all of the rows {@code changeDirection()} and {@code descend()}.
 */
public class MonsterRow extends ArrayList<Monster> {

    /**
     * The direction change of the monster's movement
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DIRECTION_CHANGE_UNCHANGED, DIRECTION_CHANGE_LEFT, DIRECTION_CHANGE_RIGHT})
    public @interface DirectionChange {
    }

    static final int DIRECTION_CHANGE_UNCHANGED = 0;
    static final int DIRECTION_CHANGE_LEFT = 1;
    static final int DIRECTION_CHANGE_RIGHT = 2;

    public static final int MONSTERS_IN_A_ROW = 10;
    public static final int ROW_COUNT = 5;

    public static final int DELAY_DIFFERENCE = 4;
    public static final int sMaxMoveDelay = 30;

    private static final int CHANCE_TO_SHOOT = 300;

    private int moveDelay;
    private boolean isMovingLeft = false;

    private int lastX;
    private int firstX;
    @Monster.MonsterTypeState private int type;
    @Monster.MonsterTypeState private int state;

    public MonsterRow(@Monster.MonsterTypeState int type, int moveDelay, int y, Bitmap spritesBitmap) {
        this.type = type;
        this.moveDelay = moveDelay == sMaxMoveDelay ? moveDelay + 1 : moveDelay;

        for (int i = 0; i < MONSTERS_IN_A_ROW; i++) {
            add(new Monster((i + 1) * Monster.SIZE,
                    y,
                    type,
                    spritesBitmap));
        }

        state = type;
        updateX();
    }

    /**
     * Move each monster in the row. In other words, move the whole row. {@see Monster#move()}
     *
     * @param x the number of pixels to move to the right.
     * @param y the number of pixels to move to the left.
     */
    public void move(int x, int y) {
        for (Monster monster : this) {
            monster.move(x, y);
        }
    }

    /**
     * Move the row according to it's direction
     */
    private void moveForward() {
        if (isMovingLeft) move(-MOVE_SPEED, 0);
        else move(MOVE_SPEED, 0);
    }

    /**
     * Update the row according to its direction. Also updates the state and the {@code moveDelay}.
     */
    public void update() {
        if (state == type) state += 1;
        else state -= 1;

        if (moveDelay == 0) { //Only if the row should move
            moveForward(); //Move according to the direction
            updateX(); //Update firstX and lastX
            changeState(); //Update the state - animate

            moveDelay = sMaxMoveDelay; //Reset the delay
        }
        if (moveDelay > 0) {
            moveDelay--;
        }

        boolean shouldShoot = GameData.RANDOM.nextInt(CHANCE_TO_SHOOT) == 1; // 1 in 200 chance
        if (shouldShoot) {
            synchronized (GameData.sLasers) {
                if (!isEmpty()) {
                    int index = GameData.RANDOM.nextInt(size()); // The monster that shoots
                    int timeout = 5; //The number of monsters to try until giving up
                    while (timeout != -1 && !get(index).shoot()) {
                        index++;
                        if (index >= size() - 1) index = 0; // return to beginning
                        timeout--;
                    }
                }
            }
        }

    }

    /**
     * Update the {@code firstX} and {@code lastX}, since it changes every time the row moves
     */
    private void updateX() {
        if (size() == 0) return;
        firstX = get(0).getX();
        lastX = get(size() - 1).getX() + get(size() - 1).getWidth();
    }

    /**
     * Change the state of the row - animate
     */
    public void changeState() {
        for (Monster monster : this) {
            monster.changeState();
        }
    }

    /**
     * Check if any of the monsters in the given rows is out of the {@link Board} bounds, and return the appropriate {@link DirectionChange}.
     *
     * @param rows all the {@link MonsterRow}s
     * @return a {@link DirectionChange}
     */
    public static int shouldChangeDirection(List<MonsterRow> rows) {
        for (MonsterRow row : rows) {
            if (row.firstX < 0) return DIRECTION_CHANGE_RIGHT;
            if (row.lastX > Board.WIDTH) return DIRECTION_CHANGE_LEFT;
        }
        return DIRECTION_CHANGE_UNCHANGED;
    }

    /**
     * Change the direction of the row's movement. If the direction has changed, descend.
     *
     * @param directionState change
     */
    public void changeDirection(@DirectionChange int directionState) {
        boolean oldMove = isMovingLeft;
        switch (directionState) {
            case DIRECTION_CHANGE_LEFT:
                isMovingLeft = true;
                break;
            case DIRECTION_CHANGE_RIGHT:
                isMovingLeft = false;
                break;
            case DIRECTION_CHANGE_UNCHANGED:
                return;
        }

        if (oldMove != isMovingLeft) { //If the direction has changed
            if (hasJustUpdated()) { //If the row is responsible for the change
                moveForward();
                moveForward();
            }
            descend();
        }
    }

    /**
     * Move the row down
     */
    private void descend() {
        move(0, MOVE_SPEED);
    }

    /**
     * Check whether this row has just been updated
     * @return whether the row was just updated
     */
    public boolean hasJustUpdated() {
        return moveDelay == sMaxMoveDelay - 1;
    }
}

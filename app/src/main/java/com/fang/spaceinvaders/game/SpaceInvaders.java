package com.fang.spaceinvaders.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.fang.spaceinvaders.R;
import com.fang.spaceinvaders.game.entity.Entity;
import com.fang.spaceinvaders.game.entity.Laser;
import com.fang.spaceinvaders.game.entity.MLaser;
import com.fang.spaceinvaders.game.entity.Monster;
import com.fang.spaceinvaders.game.entity.PLaser;
import com.fang.spaceinvaders.game.entity.Player;
import com.orhanobut.logger.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntDef;

public class SpaceInvaders {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TOUCH_INVALID, TOUCH_TOP, TOUCH_LEFT, TOUCH_RIGHT})
    @interface TouchLocation {
    }

    public static final int TOUCH_INVALID = -1;
    public static final int TOUCH_TOP = 0;
    public static final int TOUCH_LEFT = 1;
    public static final int TOUCH_RIGHT = 2;

    private Board mBoard;
    private Player mPlayer;
    private List<Laser> mLasers;
    private List<MonsterRow> mMonsters;

    private Bitmap mDefaultBitmap;

    public SpaceInvaders(Board mBoard, Context context) {
        this.mBoard = mBoard;

        mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.space_invaders_sprites);
        mPlayer = new Player(0, 0, mDefaultBitmap);
        mPlayer.setX(Board.WIDTH / 2 - (mPlayer.getWidth() / 2));
        mPlayer.setY(mBoard.calcRowToY(Board.PLAYER_ROW) - mPlayer.getHeight() / 2);

        mLasers = new ArrayList<>();

        mMonsters = new ArrayList<>();
        for (int i = 0; i < MonsterRow.ROW_COUNT; i++) {
            int type = Monster.TYPE_3_STATE_1;
            if (i == 4 || i == 3) type = Monster.TYPE_1_STATE_1;
            if (i == 2 || i == 1) type = Monster.TYPE_2_STATE_1;
            mMonsters.add(new MonsterRow(
                    type,
                    (i + 1) * MonsterRow.DELAY_DIFFERENCE,
                    mBoard.calcRowToY(i),
                    mDefaultBitmap));
        }
    }

    public void nextFrame() {
        updateLocation();
        checkCollision();
    }

    private void updateLocation() {
        mPlayer.update();
        for (int i = 0; i < mLasers.size(); i++) {
            Laser laser = mLasers.get(i);
            if (!laser.update()) {
                remove(laser);
                i--;
            }
        }

        for (MonsterRow row : mMonsters) {
            row.update();
            for (int i = 0; i < row.size(); i++) {
                Monster monster = row.get(i);
                if (!monster.update()) remove(monster);
            }
        }
        int directionState = MonsterRow.shouldChangeDirection(mMonsters);
        for (MonsterRow row : mMonsters) {
            row.changeDirection(directionState);
        }
    }

    private void checkCollision() {
        for (int j = 0; j < mLasers.size(); j++) {
            Laser laser = mLasers.get(j);
            if (laser instanceof PLaser) {
                for (MonsterRow row : mMonsters) {
                    for (int i = 0; i < row.size(); i++) {
                        Monster monster = row.get(i);
                        if (isColliding(monster, laser)) {
                            monster.kill();
                            remove(laser);
                            j--;
                        }
                    }
                }
            } else if (laser instanceof MLaser) {
                if (isColliding(mPlayer, laser)) {
                    mPlayer.kill();
                }
            }
        }
    }

    private boolean isColliding(Entity e1, Entity e2) {
        return mBoard.rectFromEntity(e1, null).intersect(
                mBoard.rectFromEntity(e2, null));
    }

    public void touch(@TouchLocation int location) {
        switch (location) {
            case TOUCH_LEFT:
                mPlayer.startMoving(true);
                break;
            case TOUCH_RIGHT:
                mPlayer.startMoving(false);
                break;
            case TOUCH_TOP:
                synchronized (mLasers) {
                    mPlayer.shoot(mLasers, mDefaultBitmap);
                }
                break;
        }
    }

    public void release() {
        mPlayer.stopMoving();
    }

    public void drawObjects(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mPlayer.getBitmap(),
                null,
                mBoard.rectFromEntity(mPlayer, null),
                paint);

        synchronized (mLasers) {
            for (Laser laser : mLasers) {
                canvas.drawBitmap(laser.getBitmap(),
                        null,
                        mBoard.rectFromEntity(laser, null),
                        paint);
            }
        }

        synchronized (mMonsters) {
            for (MonsterRow row : mMonsters) {
                for (Monster monster : row) {
                    canvas.drawBitmap(monster.getBitmap(),
                            null,
                            mBoard.rectFromEntity(monster, Monster.boundsFromType(monster.getType())),
                            paint);
                }
            }
        }
    }

    public void remove(Entity entity) {
        if (!removeEntity(entity))
            Logger.w("Oh boy, entity of type %s not found, therefore it can't be deleted",
                    entity.getClass().getSimpleName());
    }

    private boolean removeEntity(Entity entity) {
        if (entity instanceof Player) {
            if (mPlayer.equals(entity)) {
                mPlayer = null;
                return true;
            }
        } else if (entity instanceof Laser) {
            if (mLasers.contains(entity)) {
                mLasers.remove(entity);
                return true;
            }
        } else if (entity instanceof Monster) {
            for (MonsterRow row : mMonsters) {
                if (row.contains(entity)) {
                    row.remove(entity);
                    return true;
                }
            }
        }
        return false;
    }
}

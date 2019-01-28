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
import java.util.Random;

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

    public static Board sBoard;
    public static Player sPlayer;
    public static List<Laser> sLasers;
    public static List<MonsterRow> sMonsters;
    public static final Random RANDOM = new Random(System.currentTimeMillis());

    public static Bitmap sDefaultBitmap;

    public SpaceInvaders(Board mBoard, Context context) {
        this.sBoard = mBoard;

        sDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.space_invaders_sprites);
        sPlayer = new Player(0, 0, sDefaultBitmap);
        sPlayer.setX(Board.WIDTH / 2 - (sPlayer.getWidth() / 2));
        sPlayer.setY(mBoard.calcRowToY(Board.PLAYER_ROW) - sPlayer.getHeight() / 2);

        sLasers = new ArrayList<>();

        sMonsters = new ArrayList<>();
        for (int i = 0; i < MonsterRow.ROW_COUNT; i++) {
            int type = Monster.TYPE_3_STATE_1;
            if (i == 4 || i == 3) type = Monster.TYPE_1_STATE_1;
            if (i == 2 || i == 1) type = Monster.TYPE_2_STATE_1;
            sMonsters.add(new MonsterRow(
                    type,
                    (i + 1) * MonsterRow.DELAY_DIFFERENCE,
                    mBoard.calcRowToY(i),
                    sDefaultBitmap));
        }
    }

    public void nextFrame() {
        updateLocation();
        checkCollision();
    }

    private void updateLocation() {
        sPlayer.update();
        synchronized (SpaceInvaders.sLasers) {
            for (int i = 0; i < sLasers.size(); i++) {
                Laser laser = sLasers.get(i);
                if (!laser.update()) {
                    remove(laser);
                    i--;
                }
            }
        }

        for (MonsterRow row : sMonsters) {
            row.update();
            synchronized (row) {
                for (int i = 0; i < row.size(); i++) {
                    Monster monster = row.get(i);
                    if (!monster.update()) remove(monster);
                }
            }
        }
        int directionState = MonsterRow.shouldChangeDirection(sMonsters);
        for (MonsterRow row : sMonsters) {
            row.changeDirection(directionState);
        }
    }

    private void checkCollision() {
        synchronized (SpaceInvaders.sLasers) {
            for (int j = 0; j < sLasers.size(); j++) {
                Laser laser = sLasers.get(j);
                if (laser instanceof PLaser) {
                    for (MonsterRow row : sMonsters) {
                        boolean didJob = false;
                        for (int i = 0; i < row.size(); i++) {
                            Monster monster = row.get(i);
                            if (isColliding(monster, laser)) {
                                monster.kill();
                                remove(laser);
                                j--;

                                didJob = true; //Exit out of the outer loop
                                break; //Exit out of this loop
                            }
                        }
                        if (didJob) break;
                    }
                } else if (laser instanceof MLaser) {
                    if (isColliding(sPlayer, laser)) {
                        sPlayer.kill();
                    }
                }
            }
        }
    }

    private boolean isColliding(Entity e1, Entity e2) {
        return sBoard.rectFromEntity(e1, null).intersect(
                sBoard.rectFromEntity(e2, null));
    }

    public void touch(@TouchLocation int location) {
        switch (location) {
            case TOUCH_LEFT:
                sPlayer.startMoving(true);
                break;
            case TOUCH_RIGHT:
                sPlayer.startMoving(false);
                break;
            case TOUCH_TOP:
                synchronized (sLasers) {
                    sPlayer.shoot(sDefaultBitmap);
                }
                break;
        }
    }

    public void release() {
        sPlayer.stopMoving();
    }

    public void drawObjects(Canvas canvas, Paint paint) {
        canvas.drawBitmap(sPlayer.getBitmap(),
                null,
                sBoard.rectFromEntity(sPlayer, null),
                paint);

        synchronized (sLasers) {
            for (Laser laser : sLasers) {
                canvas.drawBitmap(laser.getBitmap(),
                        null,
                        sBoard.rectFromEntity(laser, null),
                        paint);
            }
        }

        synchronized (sMonsters) {
            for (MonsterRow row : sMonsters) {
                for (Monster monster : row) {
                    canvas.drawBitmap(monster.getBitmap(),
                            null,
                            sBoard.rectFromEntity(monster, Monster.boundsFromType(monster.getType())),
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
            if (sPlayer.equals(entity)) {
                sPlayer = null;
                return true;
            }
        } else if (entity instanceof Laser) {
            if (sLasers.contains(entity)) {
                sLasers.remove(entity);
                return true;
            }
        } else if (entity instanceof Monster) {
            for (MonsterRow row : sMonsters) {
                if (row.contains(entity)) {
                    row.remove(entity);
                    return true;
                }
            }
        }
        return false;
    }
}

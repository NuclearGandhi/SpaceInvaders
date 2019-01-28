package com.fang.spaceinvaders.game;

import android.content.Context;
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
import com.fang.spaceinvaders.game.util.Board;
import com.fang.spaceinvaders.game.util.MonsterRow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import androidx.annotation.IntDef;

@SuppressWarnings("SynchronizeOnNonFinalField")
public class SpaceInvaders {

    @IntDef({TOUCH_INVALID, TOUCH_TOP, TOUCH_LEFT, TOUCH_RIGHT})
    @interface TouchLocation {
    }

    public static final int TOUCH_INVALID = -1;
    public static final int TOUCH_TOP = 0;
    public static final int TOUCH_LEFT = 1;
    public static final int TOUCH_RIGHT = 2;

    public SpaceInvaders(Board mBoard, Context context) {
        GameData.sBoard = mBoard;

        GameData.sDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.space_invaders_sprites);
        GameData.sPlayer = new Player(0, 0, GameData.sDefaultBitmap);
        GameData.sPlayer.setX(Board.WIDTH / 2 - (GameData.sPlayer.getWidth() / 2));
        GameData.sPlayer.setY(mBoard.calcRowToY(Board.PLAYER_ROW) - GameData.sPlayer.getHeight() / 2);

        GameData.sLasers = new ArrayList<>();

        GameData.sMonsterRows = new ArrayList<>();
        for (int i = 0; i < MonsterRow.ROW_COUNT; i++) {
            int type = Monster.TYPE_3_STATE_1;
            if (i == 4 || i == 3) type = Monster.TYPE_1_STATE_1;
            if (i == 2 || i == 1) type = Monster.TYPE_2_STATE_1;
            GameData.sMonsterRows.add(new MonsterRow(
                    type,
                    (i + 1) * MonsterRow.DELAY_DIFFERENCE,
                    mBoard.calcRowToY(i),
                    GameData.sDefaultBitmap));
        }
    }

    public void nextFrame() {
        updateLocation();
        checkCollision();
    }

    private void updateLocation() {
        GameData.sPlayer.update();
        synchronized (GameData.sLasers) {
            for (int i = 0; i < GameData.sLasers.size(); i++) {
                Laser laser = GameData.sLasers.get(i);
                if (!laser.update()) {
                    GameData.remove(laser);
                    i--;
                }
            }
        }

        for (MonsterRow row : GameData.sMonsterRows) {
            row.update();
            synchronized (row) {
                for (int i = 0; i < row.size(); i++) {
                    Monster monster = row.get(i);
                    if (!monster.update()) GameData.remove(monster);
                }
            }
        }
        int directionState = MonsterRow.shouldChangeDirection(GameData.sMonsterRows);
        for (MonsterRow row : GameData.sMonsterRows) {
            row.changeDirection(directionState);
        }
    }

    private void checkCollision() {
        synchronized (GameData.sLasers) {
            for (int j = 0; j < GameData.sLasers.size(); j++) {
                Laser laser = GameData.sLasers.get(j);
                if (laser instanceof PLaser) {
                    for (Monster monster : GameData.getAllMonsters()) {
                            if (isColliding(monster, laser)) {
                                monster.kill();
                                GameData.remove(laser);
                                j--;
                                break; //Stop, the laser is useless
                        }
                    }
                } else if (laser instanceof MLaser) {
                    if (isColliding(GameData.sPlayer, laser)) {
                        GameData.sPlayer.kill();
                    }
                }
            }
        }
    }

    private boolean isColliding(Entity e1, Entity e2) {
        return GameData.sBoard.rectFromEntity(e1, null).intersect(
                GameData.sBoard.rectFromEntity(e2, null));
    }

    void touch(@TouchLocation int location) {
        switch (location) {
            case TOUCH_LEFT:
                GameData.sPlayer.startMoving(true);
                break;
            case TOUCH_RIGHT:
                GameData.sPlayer.startMoving(false);
                break;
            case TOUCH_TOP:
                synchronized (GameData.sLasers) {
                    GameData.sPlayer.shoot(GameData.sDefaultBitmap);
                }
                break;
            case TOUCH_INVALID:
                break;
        }
    }

    void release() {
        GameData.sPlayer.stopMoving();
    }

    void drawObjects(Canvas canvas, Paint paint) {
        synchronized (GameData.sPlayer) {
            canvas.drawBitmap(GameData.sPlayer.getBitmap(),
                    null,
                    GameData.sBoard.rectFromEntity(GameData.sPlayer, null),
                    paint);
        }

        synchronized (GameData.sLasers) {
            for (Laser laser : GameData.sLasers) {
                canvas.drawBitmap(laser.getBitmap(),
                        null,
                        GameData.sBoard.rectFromEntity(laser, null),
                        paint);
            }
        }

        synchronized (GameData.sMonsterRows) {
                for (Monster monster : GameData.getAllMonsters()) {
                    canvas.drawBitmap(monster.getBitmap(),
                            null,
                            GameData.sBoard.rectFromEntity(monster, Monster.boundsFromType(monster.getType())),
                            paint);
                }
        }
    }
}

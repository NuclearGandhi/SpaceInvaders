package com.fang.spaceinvaders.game;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import com.fang.spaceinvaders.R;
import com.fang.spaceinvaders.game.entity.Entity;
import com.fang.spaceinvaders.game.entity.Laser;
import com.fang.spaceinvaders.game.entity.MLaser;
import com.fang.spaceinvaders.game.entity.Monster;
import com.fang.spaceinvaders.game.entity.PLaser;
import com.fang.spaceinvaders.game.entity.Player;
import com.fang.spaceinvaders.game.entity.Spaceship;
import com.fang.spaceinvaders.game.util.Board;
import com.fang.spaceinvaders.game.util.Constants;
import com.fang.spaceinvaders.game.util.MonsterRow;

import java.util.ArrayList;

import androidx.annotation.IntDef;

import static com.fang.spaceinvaders.game.GameData.getAllMonsters;
import static com.fang.spaceinvaders.game.GameData.remove;
import static com.fang.spaceinvaders.game.GameData.sBoard;
import static com.fang.spaceinvaders.game.GameData.sDefaultBitmap;
import static com.fang.spaceinvaders.game.GameData.sLasers;
import static com.fang.spaceinvaders.game.GameData.sMonsterRows;
import static com.fang.spaceinvaders.game.GameData.sPlayer;
import static com.fang.spaceinvaders.game.GameData.sSpaceship;
import static com.fang.spaceinvaders.game.util.Constants.*;

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
        sBoard = mBoard;

        sDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.space_invaders_sprites);
        sPlayer = new Player(0, 0);
        sPlayer.setX(Board.WIDTH / 2 - (sPlayer.getWidth() / 2));
        sPlayer.setY(mBoard.calcRowToY(Board.PLAYER_ROW) - sPlayer.getHeight() / 2);

        sLasers = new ArrayList<>();

        sMonsterRows = new ArrayList<>();
        for (int i = 0; i < MonsterRow.ROW_COUNT; i++) {
            int type = Monster.TYPE_3_STATE_1;
            if (i == 4 || i == 3) type = Monster.TYPE_1_STATE_1;
            if (i == 2 || i == 1) type = Monster.TYPE_2_STATE_1;
            sMonsterRows.add(new MonsterRow(
                    type,
                    (i + 1) * MonsterRow.DELAY_DIFFERENCE,
                    mBoard.calcRowToY(i),
                    sDefaultBitmap));
        }

        sSpaceship = new Spaceship(0, 0);
        sSpaceship.setX(Board.WIDTH + sSpaceship.getWidth());
    }

    public void nextFrame() {
        updateLocation();
        checkCollision();
    }

    private void updateLocation() {
        sPlayer.update();
        synchronized (sLasers) {
            for (int i = 0; i < sLasers.size(); i++) {
                Laser laser = sLasers.get(i);
                if (!laser.update()) {
                    remove(laser);
                    i--;
                }
            }
        }

        for (MonsterRow row : sMonsterRows) {
            row.update();
            synchronized (row) {
                for (int i = 0; i < row.size(); i++) {
                    Monster monster = row.get(i);
                    if (!monster.update()) remove(monster);
                }
            }
        }
        int directionState = MonsterRow.shouldChangeDirection(sMonsterRows);
        for (MonsterRow row : sMonsterRows) {
            row.changeDirection(directionState);
        }

        sSpaceship.update();
    }

    private void checkCollision() {
        synchronized (sLasers) {
            for (int j = 0; j < sLasers.size(); j++) {
                Laser laser = sLasers.get(j);
                if (laser instanceof PLaser) {
                    if (isColliding(sSpaceship, laser)) {
                        sSpaceship.kill();
                        remove(laser);
                        j--;
                    } else {
                        for (Monster monster : getAllMonsters()) {
                            if (isColliding(monster, laser)) {
                                monster.kill();
                                remove(laser);
                                j--;
                                break; //Stop, the laser is useless
                            }
                        }
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

    void touch(@TouchLocation int location) {
        switch (location) {
            case TOUCH_LEFT:
                sPlayer.startMoving(true);
                break;
            case TOUCH_RIGHT:
                sPlayer.startMoving(false);
                break;
            case TOUCH_TOP:
                synchronized (sLasers) {
                    sPlayer.shoot();
                }
                break;
            case TOUCH_INVALID:
                break;
        }
    }

    void release() {
        sPlayer.stopMoving();
    }

    void drawObjects(Canvas canvas, Paint paint) {
        synchronized (sPlayer) {
            canvas.drawBitmap(sPlayer.getBitmap(),
                    null,
                    sBoard.rectFromEntity(sPlayer, null),
                    paint);
        }

        synchronized (sLasers) {
            for (Laser laser : sLasers) {
                canvas.drawBitmap(laser.getBitmap(),
                        null,
                        sBoard.rectFromEntity(laser, null),
                        paint);
            }
        }

        synchronized (sMonsterRows) {
            for (Monster monster : getAllMonsters()) {
                canvas.drawBitmap(monster.getBitmap(),
                        null,
                        sBoard.rectFromEntity(monster, Monster.boundsFromType(monster.getType())),
                        paint);
            }
        }

        if ((sSpaceship.getState() & STATE_ON_SCREEN) == STATE_ON_SCREEN) {
            synchronized (sSpaceship) {
                Paint redPaint = new Paint();
                ColorFilter filter = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.DARKEN);
                redPaint.setColorFilter(filter);

                canvas.drawBitmap(sSpaceship.getBitmap(),
                        null,
                        sBoard.rectFromEntity(sSpaceship, null),
                        redPaint);
            }
        }
    }
}

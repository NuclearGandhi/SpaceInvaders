package com.fang.spaceinvaders.game;

import android.graphics.Bitmap;

import com.fang.spaceinvaders.game.entity.Entity;
import com.fang.spaceinvaders.game.entity.Laser;
import com.fang.spaceinvaders.game.entity.Monster;
import com.fang.spaceinvaders.game.entity.Player;
import com.fang.spaceinvaders.game.entity.Spaceship;
import com.fang.spaceinvaders.game.util.Board;
import com.fang.spaceinvaders.game.util.MonsterRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class GameData {

    public static final Random RANDOM = new Random(System.currentTimeMillis());
    public static int sScore = 0;
    public static Board sBoard;
    public static Player sPlayer;
    public static List<Laser> sLasers;
    public static List<MonsterRow> sMonsterRows;
    public static Spaceship sSpaceship;
    public static Bitmap sDefaultBitmap;

    static List<Monster> getAllMonsters() {
        List<Monster> monsters = new ArrayList<>();
        for (MonsterRow row : sMonsterRows) {
            monsters.addAll(row);
        }
        return monsters;
    }

    static void remove(Entity entity) {
        if (!removeEntity(entity))
            Timber.w("Oh boy, entity of type %s not found, therefore it can't be deleted",
                    entity.getClass().getSimpleName());
    }

    private static boolean removeEntity(Entity entity) {
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
            for (MonsterRow row : sMonsterRows) {
                if (row.contains(entity)) {
                    row.remove(entity);
                    return true;
                }
            }
        }
        return false;
    }
}

package com.fang.spaceinvaders.game.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public class Constants {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_ALIVE, STATE_DEAD, STATE_ON_SCREEN, STATE_OFF_SCREEN})
    public @interface EntityState {}

    public static final int STATE_ALIVE = 1;
    public static final int STATE_DEAD = 2;
    public static final int STATE_ON_SCREEN = 4;
    public static final int STATE_OFF_SCREEN = 8;
}

package com.fang.spaceinvaders.game.entity;

public interface IShooter {

    /**
     * Create a laser in the player position. Doesn't shoot if the player's {@code laserTimer}
     * didn't reach 0.
     *
     * @return whether the player shot the laser or not
     */
    boolean shoot();
}

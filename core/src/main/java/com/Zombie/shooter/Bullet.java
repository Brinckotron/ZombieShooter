package com.Zombie.shooter;

import com.badlogic.gdx.math.Vector2;

public class Bullet {
    public float positionX;
    public float positionY;
    public Vector2 direction;

    public Bullet(float posX, float posY, Vector2 dir)
    {
        this.positionX = posX;
        this.positionY = posY;
        this.direction = dir;
    }
}

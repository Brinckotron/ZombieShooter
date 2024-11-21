package com.Zombie.shooter;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    public float positionX;
    public float positionY;
    public Vector2 direction;
    public Sprite sprite;
    public Rectangle rectangle;

    public Bullet(float posX, float posY, Vector2 dir, Sprite spr)
    {
        this.positionX = posX;
        this.positionY = posY;
        this.direction = dir;
        this.sprite = spr;
        this.sprite.setCenter(posX, posY);
        this.rectangle = new Rectangle();
        this.rectangle.set(positionX, positionY, sprite.getWidth(), sprite.getHeight());
    }
}

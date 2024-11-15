package com.Zombie.shooter;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Hero {
    public float positionX;
    public float positionY;
    public Sprite sprite;
    public int health;
    public float speed;
    public enum gunType {
        pistol,
        machinegun,
        shotgun;
    }
    public gunType weapon;

    public Hero(float posX, float posY, Sprite spr, int hp, float spd, gunType gun)
    {
        this.positionX = posX;
        this.positionY = posY;
        this.sprite = spr;
        this.sprite.setSize(25, 25);
        this.sprite.setCenter(posX, posY);
        this.speed = spd;
        this.health = hp;
        this.weapon = gun;
    }
}

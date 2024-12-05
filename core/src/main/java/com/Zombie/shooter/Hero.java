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
    public int ammoCount;
    public gunType weapon;
    public float invTimer;
    public float speedTimer;

    public Hero(float posX, float posY, Sprite spr, int hp, float spd, gunType gun)
    {
        this.positionX = posX;
        this.positionY = posY;
        this.sprite = spr;
        this.sprite.setSize(32, 32);
        this.sprite.setCenter(posX, posY);
        this.speed = spd;
        this.health = hp;
        this.weapon = gun;
        this.ammoCount = 0;
        this.invTimer = 0f;
        this.speedTimer = 0f;
    }
}

package com.Zombie.shooter;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import javax.lang.model.util.Elements;

public class Zombie {

    public float positionX;
    public float positionY;
    public Sprite sprite;
    float knockBackTimer;
    float knockBackTime;
    Vector2 kbOrigin;
    Vector2 kbEndPoint;

    public Zombie(float posX, float posY, Sprite spr){
        this.knockBackTime = 0.2f;
        this.kbOrigin = null;
        this.kbEndPoint = null;
        this.knockBackTimer = 0f;
        this.positionX = posX;
        this.positionY = posY;
        this.sprite = spr;
        this.sprite.setSize(25, 25);
        this.sprite.setCenter(this.positionX, this.positionY);
    }

    public void knockBackTick(float delta){
        if (this.knockBackTimer > 0) {
            float progress = (knockBackTime - knockBackTimer) / knockBackTime;
            this.positionX = MathUtils.lerp(kbOrigin.x, kbEndPoint.x, progress);
            this.positionY = MathUtils.lerp(kbOrigin.y, kbEndPoint.y, progress);
            this.sprite.setCenter(positionX, positionY);
            this.knockBackTimer -= delta;
        }
    }

    public void Knockback(Vector2 direction, int distanceMultiplier){
        this.knockBackTimer = knockBackTime;
        kbOrigin = new Vector2(this.positionX, this.positionY);
        kbEndPoint = new Vector2( kbOrigin.x + (direction.x * distanceMultiplier),kbOrigin.y + (direction.y * distanceMultiplier));
    }
}

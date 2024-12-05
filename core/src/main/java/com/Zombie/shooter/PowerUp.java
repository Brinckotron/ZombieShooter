package com.Zombie.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class PowerUp {
    enum powerUpEnum {health, shotgun, machinegun, bomb, speed}
    powerUpEnum powerUpType;
    Sprite sprite;
    float currentLifeSpan;
    float positionX;
    float positionY;
    float scale;

    public PowerUp(Vector2 position){
        int random = new Random().nextInt(1,5);
        switch (random) {
            case 1: {
                powerUpType = powerUpEnum.health;
                sprite = new Sprite(new Texture("Heart.png"));
                sprite.setScale(0.4f);
                break;
            }
            case 2: {
                powerUpType = powerUpEnum.shotgun;
                sprite = new Sprite(new Texture("PowerUpShotgun.png"));
                sprite.setScale(1f);
                break;
            }
            case 3: {
                powerUpType = powerUpEnum.machinegun;
                sprite = new Sprite(new Texture("PowerUpMachineGun.png"));
                sprite.setScale(1f);
                break;
            }
            case 4: {
                powerUpType = powerUpEnum.speed;
                sprite = new Sprite(new Texture("SpeedBoost.png"));
                sprite.setScale(1.5f);
                break;

            }
            case 5: {
                powerUpType = powerUpEnum.bomb;
                sprite = new Sprite(new Texture("PowerUpTNT.png"));
                sprite.setScale(1f);
                break;
            }
            default:{
                break;
            }
        }
        this.currentLifeSpan = 10f;
        this.positionX = position.x;
        this.positionY = position.y;
        this.scale = sprite.getScaleX();
        this.sprite.setCenter(positionX, positionY);
    }

    public void PowerUpTick(float delta){
        this.currentLifeSpan -= delta;
        if (((int)currentLifeSpan % 2) == 0)
        {
            this.sprite.setScale(scale * 1.2f);
        }
        else {
            this.sprite.setScale(scale);
        }
    }

}

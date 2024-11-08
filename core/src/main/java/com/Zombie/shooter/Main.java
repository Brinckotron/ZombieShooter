package com.Zombie.shooter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    Texture backgroundTexture;
    Texture heroTexture;
    Texture zombieTexture;
    Texture tower;
    Texture bullets;
    Sound gunSound;
    Sound zombieSound;
    Sound zombieDeathSound;
    Sound powerUp;
    Sound lose;
    Music music;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Sprite heroSprite;

    Vector2 touchPos;

    Array<Sprite> zombieSprites;
    Array<Sprite> bulletSprites;

    float zombieSpawnTimer;

    Rectangle heroRectangle;
    Array<Rectangle> zombieRectangles;

    @Override
    public void create() {
        backgroundTexture = new Texture("Background.png");


        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(896, 512);


        touchPos = new Vector2();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // Draw your application here.
        input();
        logic();
        draw();
    }


    public void input() {

    }

    public void logic(){

    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        spriteBatch.end();
    }


    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}

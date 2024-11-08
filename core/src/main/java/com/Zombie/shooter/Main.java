package com.Zombie.shooter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
    Texture bulletTexture;
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
        heroTexture = new Texture("Hero.png");
        zombieTexture = new Texture("Zombie.png");
        bulletTexture = new Texture("Bullet.png");

        //zombieSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        //music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(896, 512);

        heroSprite = new Sprite(heroTexture);
        heroSprite.setSize(25, 25);

        touchPos = new Vector2();

        zombieSprites = new Array<>();
        bulletSprites = new Array<>();

        heroRectangle = new Rectangle();
        zombieRectangles = new Array<>();

        spawnZombie();

       /* music.setLooping(true);
        music.setVolume(.5f);
        music.play();*/
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
        heroSprite.draw(spriteBatch);

        for (Sprite zombieSprite : zombieSprites) {
            zombieSprite.draw(spriteBatch);
        }

        for (Sprite bulletSprite : bulletSprites) {
            bulletSprite.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void spawnZombie() {

        float zombieWidth = 25;
        float zombieHeight = 25;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        Sprite zombieSprite = new Sprite(zombieTexture);
        zombieSprite.setSize(zombieWidth, zombieHeight);
        zombieSprite.setX(MathUtils.random(0, 1) == 0? 0f: worldWidth - zombieWidth);
        zombieSprite.setY(MathUtils.random(0f, worldHeight - zombieHeight));
        zombieSprites.add(zombieSprite);
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

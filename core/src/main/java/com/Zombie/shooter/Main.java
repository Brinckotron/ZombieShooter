package com.Zombie.shooter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    Texture aimTexture;
    Texture tower;
    Texture bullet;
    Sound gunSound;
    Sound zombieSound;
    Sound zombieDeathSound;
    Sound powerUp;
    Sound lose;
    Music music;
    Array<Bullet> bullets;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Sprite heroSprite;

    Vector2 touchPos;

    Array<Sprite> zombieSprites;
    Array<Sprite> bulletSprites;

    float zombieSpawnTimer;

    Rectangle heroRectangle;
    Rectangle zombieRectangle;
    Rectangle bulletRectangle;
    Rectangle gameWorld;

    @Override
    public void create() {
        backgroundTexture = new Texture("Background.png");
        heroTexture = new Texture("Hero.png");
        zombieTexture = new Texture("Zombie.png");
        bulletTexture = new Texture("Bullet.png");
        aimTexture = new Texture("Reticle.png");

        //zombieSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        //music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(896, 512);

        heroSprite = new Sprite(heroTexture);
        heroSprite.setSize(25, 25);

        touchPos = new Vector2();

        zombieSprites = new Array<>();
        bulletSprites = new Array<>();
        bullets = new Array<>();

        heroRectangle = new Rectangle();
        //zombieRectangles = new Array<>();
        gameWorld = new Rectangle();
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
        input();
        logic();
        draw();
    }


    public void input() {
        float speed = 30f;
        float delta = Gdx.graphics.getDeltaTime();
        float verticalVelocity = 0;
        float horizontalVelocity = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            horizontalVelocity += (speed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            horizontalVelocity += (-speed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            verticalVelocity += (speed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            verticalVelocity += (-speed * delta);
        }

        heroSprite.translateX(horizontalVelocity);
        heroSprite.translateY(verticalVelocity);

        if (Gdx.input.isTouched())
        {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            spawnBullet();
        }
    }

    public void logic(){
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float playerWidth = heroSprite.getWidth();
        float playerHeight = heroSprite.getHeight();
        gameWorld.set(0f, 0f,896f, 512f);

        heroSprite.setX(MathUtils.clamp(heroSprite.getX(), 0, worldWidth - playerWidth));
        heroSprite.setY(MathUtils.clamp(heroSprite.getY(), 0, worldHeight - playerHeight));

        float delta = Gdx.graphics.getDeltaTime();

        heroRectangle.set(heroSprite.getX(), heroSprite.getY(), playerWidth, playerHeight);

        for (int i = bulletSprites.size - 1; i >= 0; i--) {
            Sprite bulletSprite = bulletSprites.get(i);
            float bulletWidth = bulletSprite.getWidth();
            float bulletHeight = bulletSprite.getHeight();

          //  bulletSprite.translateY(-2f * delta);
            bulletRectangle.set(bulletSprite.getX(), bulletSprite.getY(), bulletWidth, bulletHeight);


//            if (bulletSprite.getY() > gameWorld.getHeight() / 2) bulletSprites.removeIndex(i);
//            else if (bulletSprite.getX() > gameWorld.getWidth() / 2) bulletSprites.removeIndex(i);
            if(!bulletRectangle.overlaps(gameWorld)) bulletSprites.removeIndex(i);
            else if (bulletRectangle.overlaps(zombieRectangle)) {
                bulletSprites.removeIndex(i);
            }
        }
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

    private void spawnBullet()
    {
        Vector2 direction = new Vector2(touchPos.x - heroSprite.getX(), touchPos.y - heroSprite.getY());
        bullets.add(new Bullet(heroSprite.getX(), heroSprite.getY(), direction));
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

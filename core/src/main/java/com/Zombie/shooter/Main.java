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
    Texture gunTexture;
    Texture towerTexture;
    Sound gunSound;
    Sound zombieSound;
    Sound zombieDeathSound;
    Sound powerUp;
    Sound lose;
    Music music;
    Array<Bullet> bullets;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Hero hero;

    Vector2 mousePos;

    Sprite gunSprite;
    Array<Sprite> zombieSprites;

    float zombieSpawnTimer;
    float bulletSpeed = 100f;
    float heroSpeed = 50f;
    int heroHealth = 100;
    float pistolReloadTime = 0.75f;
    float machineGunReloadTime = 0.25f;
    float shotgunReloadTime = 1f;
    float timerReload;
    float shotgunSpread = 10f;

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
        gunTexture = new Texture("Gun.png");

        //zombieSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        //music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(896, 512);

        hero = new Hero(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, new Sprite(heroTexture), heroHealth, heroSpeed, Hero.gunType.pistol );
        hero.sprite.setSize(25, 25);
        gunSprite = new Sprite(gunTexture);


        timerReload = 0f;

        mousePos = new Vector2();

        zombieSprites = new Array<>();
        bullets = new Array<>();

        heroRectangle = new Rectangle();
        bulletRectangle = new Rectangle();
        zombieRectangle = new Rectangle();
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
        float delta = Gdx.graphics.getDeltaTime();
        float verticalVelocity = 0;
        float horizontalVelocity = 0;
        mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        if (Gdx.input.isKeyPressed(Input.Keys.D))
        {
            horizontalVelocity += (heroSpeed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontalVelocity += (-heroSpeed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            verticalVelocity += (heroSpeed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            verticalVelocity += (-heroSpeed * delta);
        }

        hero.positionX += horizontalVelocity;
        hero.positionY += verticalVelocity;
        hero.sprite.translateX(horizontalVelocity);
        hero.sprite.translateY(verticalVelocity);

        if (Gdx.input.isTouched() && timerReload <= 0f)
        {
            viewport.unproject(mousePos);

            switch(hero.weapon){
                case pistol -> {
                    timerReload = pistolReloadTime;
                    spawnBullet();}
                case shotgun -> {
                    timerReload = shotgunReloadTime;
                    spawnShotgunBullets();}
                case machinegun -> {
                    timerReload = machineGunReloadTime;
                    spawnBullet();}
            }
        }
    }

    public void logic(){
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float playerWidth = hero.sprite.getWidth();
        float playerHeight = hero.sprite.getHeight();
        gameWorld.set(0f, 0f,896f, 512f);

        hero.sprite.setX(MathUtils.clamp(hero.sprite.getX(), 0, worldWidth - playerWidth));
        hero.sprite.setY(MathUtils.clamp(hero.sprite.getY(), 0, worldHeight - playerHeight));
        Vector2 direction = new Vector2(mousePos.x - hero.sprite.getX(), mousePos.y - hero.sprite.getY());
        direction.nor();
        gunSprite.setX(hero.sprite.getX() + direction.x * 20);
        gunSprite.setY(hero.sprite.getY() + direction.y * 20);

        float delta = Gdx.graphics.getDeltaTime();

        heroRectangle.set(hero.sprite.getX(), hero.sprite.getY(), playerWidth, playerHeight);


        //reload time logic
        if (timerReload > 0f)
        {
            timerReload -= delta;
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            float bulletWidth = bullet.sprite.getWidth();
            float bulletHeight = bullet.sprite.getHeight();

            bullet.sprite.translate(bulletSpeed * bullet.direction.x * delta, bulletSpeed * bullet.direction.y * delta);
            bullet.positionX = bullet.sprite.getX();
            bullet.positionY = bullet.sprite.getY();
            bulletRectangle.set(bullet.positionX, bullet.positionY, bulletWidth, bulletHeight);


//            if (bulletSprite.getY() > gameWorld.getHeight() / 2) bulletSprites.removeIndex(i);
//            else if (bulletSprite.getX() > gameWorld.getWidth() / 2) bulletSprites.removeIndex(i);
            if(!bulletRectangle.overlaps(gameWorld)) bullets.removeIndex(i);
            else if (bulletRectangle.overlaps(zombieRectangle)) {
                bullets.removeIndex(i);
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
        hero.sprite.draw(spriteBatch);
        gunSprite.draw(spriteBatch);

        for (Sprite zombieSprite : zombieSprites) {
            zombieSprite.draw(spriteBatch);
        }

        for (Bullet bullet : bullets) {
            bullet.sprite.draw(spriteBatch);
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
        Vector2 direction = new Vector2(mousePos.x - hero.sprite.getX(), mousePos.y - hero.sprite.getY());
        direction.nor();
        bullets.add(new Bullet(hero.sprite.getX(), hero.sprite.getY(), direction, new Sprite(bulletTexture)));
    }

    private void spawnShotgunBullets()
    {
        Vector2 direction = new Vector2(mousePos.x - hero.sprite.getX(), mousePos.y - hero.sprite.getY());
        Vector2 direction2 = new Vector2(mousePos.x - hero.sprite.getX(), mousePos.y - hero.sprite.getY());
        direction2.rotateDeg(shotgunSpread);
        direction2.nor();
        Vector2 direction3 = new Vector2(mousePos.x - hero.sprite.getX(), mousePos.y - hero.sprite.getY());
        direction3.rotateDeg(-shotgunSpread);
        direction3.nor();

        direction.nor();
        bullets.add(new Bullet(hero.sprite.getX(), hero.sprite.getY(), direction, new Sprite(bulletTexture)));
        bullets.add(new Bullet(hero.sprite.getX(), hero.sprite.getY(), direction2, new Sprite(bulletTexture)));
        bullets.add(new Bullet(hero.sprite.getX(), hero.sprite.getY(), direction3, new Sprite(bulletTexture)));
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

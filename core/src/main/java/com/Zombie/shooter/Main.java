package com.Zombie.shooter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.logging.XMLFormatter;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main implements ApplicationListener {

    Texture backgroundTexture;
    Texture heroTexture;
    Texture zombieTexture;
    Texture bulletTexture;
    Texture pistolTexture;
    Texture shotgunTexture;
    Texture machineGunTexture;
    Texture towerTexture;
    Texture gamePausedTexture;
    Array<Texture> numbers;
    Sound gunSound;
    Sound zombieSound;
    Sound zombieDeathSound;
    Sound powerUp;
    Sound lose;
    Music music;
    Array<Bullet> bullets;
    Sprite scoreUnits;
    Sprite scoreDiz;
    Sprite scoreCent;

    boolean isPaused = false;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Hero hero;

    Vector2 mousePos;

    Sprite pauseSprite;

    Sprite gunSprite;
    Array<Sprite> zombieSprites;

    float zombieSpawnTimer = 0f;
    float bulletSpeed = 150f;
    float heroSpeed = 80f;
    int heroHealth = 100;
    float pistolReloadTime = 0.75f;
    float machineGunReloadTime = 0.25f;
    float shotgunReloadTime = 1f;
    float timerReload;
    float shotgunSpread = 10f;
    int score = 0;
    float zombieSpeed = 40f;
    float pauseTimer = 0f;

    Rectangle heroRectangle;
    Rectangle zombieRectangle;
    Rectangle gameWorld;

    @Override
    public void create() {
        Pixmap aim = new Pixmap(Gdx.files.internal("Reticle.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(aim, 8, 8));
        aim.dispose();
        backgroundTexture = new Texture("Background.png");
        heroTexture = new Texture("Hero.png");
        zombieTexture = new Texture("Zombie.png");
        bulletTexture = new Texture("Bullet.png");
        pistolTexture = new Texture("Gun.png");
        shotgunTexture = new Texture("Shotgun.png");
        machineGunTexture = new Texture("MachineGun.png");
        gamePausedTexture = new Texture("GamePaused.png");
        numbers = new Array<Texture>();
        for (int i = 0; i < 10; i++) {
            numbers.add(new Texture(i + ".png"));
        }
        scoreUnits = new Sprite();
        scoreDiz = new Sprite();
        scoreCent = new Sprite();


        //zombieSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("GoodDayToDie.mp3"));

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(896, 512);

        hero = new Hero(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, new Sprite(heroTexture), heroHealth, heroSpeed, Hero.gunType.shotgun);
        hero.sprite.setSize(25, 25);
        gunSprite = new Sprite(pistolTexture);
        pauseSprite = new Sprite(gamePausedTexture);
        pauseSprite.setScale(5f, 2f);
        pauseSprite.setCenter(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);


        timerReload = 0f;

        mousePos = new Vector2();

        zombieSprites = new Array<>();
        bullets = new Array<>();

        heroRectangle = new Rectangle();
        zombieRectangle = new Rectangle();
        gameWorld = new Rectangle(0f, 0f, 896f, 512f);

        music.setLooping(true);
        music.setVolume(.1f);
        music.play();
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
        viewport.unproject(mousePos);

        PauseLogic(delta);

        //movement
        if (Gdx.input.isKeyPressed(Input.Keys.D) && !isPaused) {
            horizontalVelocity += (heroSpeed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) && !isPaused) {
            horizontalVelocity += (-heroSpeed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && !isPaused) {
            verticalVelocity += (heroSpeed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S) && !isPaused) {
            verticalVelocity += (-heroSpeed * delta);
        }

        hero.positionX += horizontalVelocity;
        hero.positionY += verticalVelocity;
        hero.sprite.translateX(horizontalVelocity);
        hero.sprite.translateY(verticalVelocity);

        // shooting gun
        if (Gdx.input.isTouched() && timerReload <= 0f && !isPaused) {

            switch (hero.weapon) {
                case pistol -> {
                    timerReload = pistolReloadTime;
                    spawnBullet();
                }
                case shotgun -> {
                    timerReload = shotgunReloadTime;
                    spawnShotgunBullets();
                }
                case machinegun -> {
                    timerReload = machineGunReloadTime;
                    spawnBullet();
                }
            }
        }
    }

    private void PauseLogic(float delta) {
        //pause logic
        if (pauseTimer > 0) pauseTimer -= delta;
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && pauseTimer <= 0) {
            isPaused = !isPaused;
            pauseTimer = 0.5f;
            if (isPaused) {
                music.pause();
            } else {
                music.play();
            }
        }
    }

    public void logic() {
        float delta = isPaused ? 0 : Gdx.graphics.getDeltaTime();
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float playerWidth = hero.sprite.getWidth();
        float playerHeight = hero.sprite.getHeight();
        Vector2 playerCenter = new Vector2(hero.positionX + playerWidth / 2, hero.positionY + playerHeight / 2);

        //reload time logic
        if (timerReload > 0f && !isPaused) {
            timerReload -= delta;
        }

        ClampHero(worldWidth, playerWidth, worldHeight, playerHeight);

        //hero hitbox
        heroRectangle.set(playerCenter.x, playerCenter.y, playerWidth, playerHeight);

        SetSprite(playerCenter);

        BulletLogic(delta);

        ZombieSpawnLogic(delta);

        ZombieMoveLogic(playerCenter, delta);

        ScoreLogic(worldWidth, worldHeight);

    }

    private void ScoreLogic(float worldWidth, float worldHeight) {
        //score logic
        String scoreString = String.valueOf(score);
        if (score < 100) {
            scoreString = "0" + scoreString;
            if (score < 10) {
                scoreString = "0" + scoreString;
            }
        }

        scoreUnits = new Sprite(numbers.get(Integer.parseInt(String.valueOf(scoreString.charAt(2)))));
        scoreDiz = new Sprite(numbers.get(Integer.parseInt(String.valueOf(scoreString.charAt(1)))));
        scoreCent = new Sprite(numbers.get(Integer.parseInt(String.valueOf(scoreString.charAt(0)))));
        scoreUnits.setCenter(worldWidth - 10f, worldHeight - 10f);
        scoreDiz.setCenter(worldWidth - 30f, worldHeight - 10f);
        scoreCent.setCenter(worldWidth - 50f, worldHeight - 10f);
    }

    private void ZombieMoveLogic(Vector2 playerCenter, float delta) {
        for (Sprite zombieSprite : zombieSprites) {
            Vector2 zombieCenter = new Vector2(zombieSprite.getX() + zombieSprite.getWidth() / 2, zombieSprite.getY() + zombieSprite.getHeight() / 2);
            Vector2 direction = new Vector2(playerCenter.x - zombieCenter.x, playerCenter.y - zombieCenter.y);
            direction.nor();

            zombieSprite.translate(zombieSpeed * direction.x * delta, zombieSpeed * direction.y * delta);
            zombieSprite.setScale(direction.x < 0 ? -1 : 1, 1);
        }
    }

    private void ZombieSpawnLogic(float delta) {
        zombieSpawnTimer -= delta;
        if (zombieSprites.size > 50) {return;}
        if (zombieSpawnTimer <= 0f) {
            if (score < 20) {
                spawnZombie();
                zombieSpawnTimer = 1f;
            } else if (score < 50) {
                zombieSpawnTimer = 0.8f;
                spawnZombie();
            } else if (score < 100) {
                zombieSpawnTimer = 0.6f;
                spawnZombie();
            } else if (score < 200) {
                zombieSpawnTimer = 0.5f;
                spawnZombie();
                spawnZombie();
            } else {
                zombieSpawnTimer = 0.5f;
                spawnZombie();
                spawnZombie();
                spawnZombie();
            }
        }
    }

    private void BulletLogic(float delta) {
        //bullet logic
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            float bulletWidth = bullet.sprite.getWidth();
            float bulletHeight = bullet.sprite.getHeight();

            bullet.sprite.translate(bulletSpeed * bullet.direction.x * delta, bulletSpeed * bullet.direction.y * delta);
            bullet.positionX = bullet.sprite.getX();
            bullet.positionY = bullet.sprite.getY();
            bullet.rectangle.set(bullet.positionX, bullet.positionY, bulletWidth, bulletHeight);

            if (!bullet.rectangle.overlaps(gameWorld)) bullets.removeIndex(i);
            for (int j = zombieSprites.size - 1; j >= 0; j--) {
                Sprite zombieSprite = zombieSprites.get(j);
                Vector2 zombieCenter = new Vector2(zombieSprite.getX(), zombieSprite.getY());
                zombieRectangle.set(zombieCenter.x-5, zombieCenter.y, zombieSprite.getWidth(), zombieSprite.getHeight());
                if (bullet.rectangle.overlaps(zombieRectangle)) {
                    bullets.removeIndex(i);
                    zombieSprites.removeIndex(j);
                    score++;
                    if (score == 1000) {
                        score = 0;
                    }
                    return;
                }
            }

        }
    }

    private void SetSprite(Vector2 playerCenter) {
        //gunsprite logic
        Vector2 direction = new Vector2(mousePos.x - playerCenter.x, mousePos.y - playerCenter.y);
        direction.nor();
        gunSprite.setCenter((playerCenter.x-4) + direction.x * 13, playerCenter.y + direction.y * 13);
        switch (hero.weapon) {
            case pistol -> gunSprite.setTexture(pistolTexture);
            case shotgun -> gunSprite.setTexture(shotgunTexture);
            case machinegun -> gunSprite.setTexture(machineGunTexture);
        }
        gunSprite.setScale(direction.x < 0 ? -1 : 1, 1);
        hero.sprite.setScale(direction.x < 0 ? -1 : 1, 1);
    }

    private void ClampHero(float worldWidth, float playerWidth, float worldHeight, float playerHeight) {
        //clamp hero position
        hero.sprite.setX(MathUtils.clamp(hero.sprite.getX(), 0, worldWidth - playerWidth));
        hero.sprite.setY(MathUtils.clamp(hero.sprite.getY(), 0, worldHeight - playerHeight));
        hero.positionX = MathUtils.clamp(hero.sprite.getX(), 0, worldWidth - playerWidth);
        hero.positionY = MathUtils.clamp(hero.sprite.getY(), 0, worldHeight - playerHeight);
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
        scoreUnits.draw(spriteBatch);
        scoreDiz.draw(spriteBatch);
        scoreCent.draw(spriteBatch);

        if (isPaused) pauseSprite.draw(spriteBatch);

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
        zombieSprite.setCenter(MathUtils.random(0, 1) == 0 ? 0f : worldWidth - zombieWidth, MathUtils.random(0f, worldHeight - zombieHeight));
        zombieSprites.add(zombieSprite);
    }

    private void spawnBullet() {
        float playerWidth = hero.sprite.getWidth();
        float playerHeight = hero.sprite.getHeight();
        Vector2 playerCenter = new Vector2(hero.positionX + playerWidth / 2, hero.positionY + playerHeight / 2);
        Vector2 direction = new Vector2(mousePos.x - playerCenter.x, mousePos.y - playerCenter.y);
        direction.nor();
        bullets.add(new Bullet(playerCenter.x + direction.x * 20, playerCenter.y + direction.y * 20, direction, new Sprite(bulletTexture)));
    }

    private void spawnShotgunBullets() {
        float playerWidth = hero.sprite.getWidth();
        float playerHeight = hero.sprite.getHeight();
        Vector2 playerCenter = new Vector2(hero.positionX + playerWidth / 2, hero.positionY + playerHeight / 2);
        Vector2 direction = new Vector2(mousePos.x - hero.positionX, mousePos.y - hero.positionY);
        Vector2 direction2 = new Vector2(mousePos.x - hero.positionX, mousePos.y - hero.positionY);
        direction2.rotateDeg(shotgunSpread);
        direction2.nor();
        Vector2 direction3 = new Vector2(mousePos.x - hero.positionX, mousePos.y - hero.positionY);
        direction3.rotateDeg(-shotgunSpread);
        direction3.nor();

        direction.nor();
        bullets.add(new Bullet(playerCenter.x + direction.x * 20, playerCenter.y + direction.y * 20, direction, new Sprite(bulletTexture)));
        bullets.add(new Bullet(playerCenter.x + direction.x * 20, playerCenter.y + direction.y * 20, direction2, new Sprite(bulletTexture)));
        bullets.add(new Bullet(playerCenter.x + direction.x * 20, playerCenter.y + direction.y * 20, direction3, new Sprite(bulletTexture)));
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

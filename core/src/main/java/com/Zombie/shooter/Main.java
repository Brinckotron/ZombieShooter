package com.Zombie.shooter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.List;
import java.util.Random;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main implements ApplicationListener {

    boolean gameOver = false;
    boolean showScores = false;

    String username = "";

    Stage stage;
    Skin skin;

    TextField usernameInput;
    TextButton ok;
    TextButton restart;
    Label[] scores;
    Label[] usernames;
    Label HealthLabel;

    Texture backgroundTexture;
    Texture heroTexture;
    Texture zombieTexture;
    Texture bulletTexture;
    Texture pistolTexture;
    Texture shotgunTexture;
    Texture machineGunTexture;
    Texture gamePausedTexture;
    Texture heartTexture;
    Array<Texture> numbers;
    Music music;
    Array<Bullet> bullets;
    Sprite scoreUnits;
    Sprite scoreDiz;
    Sprite scoreCent;
    Sprite heartSprite;

    boolean isPaused = false;

    SpriteBatch spriteBatch;
    FitViewport viewport;

    Hero hero;

    Vector2 mousePos;

    Sprite pauseSprite;

    Sprite gunSprite;
    Array<Zombie> zombies;
    Array<PowerUp> powerUps;
    Rectangle powerUpRectangle;

    float zombieSpawnTimer = 0f;
    float bulletSpeed = 150f;
    float heroSpeed = 80f;
    int heroHealth = 10;
    float pistolReloadTime = 0.75f;
    float machineGunReloadTime = 0.25f;
    float shotgunReloadTime = 1f;
    float timerReload;
    float shotgunSpread = 10f;
    int score = 0;
    float zombieSpeed = 40f;
    float pauseTimer = 0f;
    int zombiePushBack = 30;
    float speedBoostLength = 10;
    float invTime = 1f;

    Rectangle heroRectangle;
    Rectangle zombieRectangle;
    Rectangle gameWorld;

    @Override
    public void create() {

        viewport = new FitViewport(896, 512);
        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Pixmap aim = new Pixmap(Gdx.files.internal("Reticle.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(aim, 8, 8));
        aim.dispose();
        float middleWidth = viewport.getWorldWidth() / 2;
        float middleHeight= viewport.getWorldHeight() / 2;
        backgroundTexture = new Texture("Background.png");
        heroTexture = new Texture("Hero.png");
        zombieTexture = new Texture("Zombie.png");
        bulletTexture = new Texture("Bullet.png");
        pistolTexture = new Texture("Gun.png");
        shotgunTexture = new Texture("Shotgun.png");
        machineGunTexture = new Texture("MachineGun.png");
        gamePausedTexture = new Texture("GamePaused.png");
        heartTexture = new Texture("Heart.png");
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


        hero = new Hero(middleWidth, middleHeight, new Sprite(heroTexture), heroHealth, heroSpeed, Hero.gunType.pistol);
        hero.sprite.setSize(25, 25);
        gunSprite = new Sprite(pistolTexture);
        pauseSprite = new Sprite(gamePausedTexture);
        pauseSprite.setScale(5f, 2f);
        pauseSprite.setCenter(middleWidth, middleHeight);
        heartSprite = new Sprite(heartTexture);
        heartSprite.setScale(0.7f);
        heartSprite.setCenter((viewport.getWorldWidth()/2) , 20);


        timerReload = 0f;

        mousePos = new Vector2();

        zombies = new Array<>();
        bullets = new Array<>();
        powerUps = new Array<>();

        heroRectangle = new Rectangle();
        zombieRectangle = new Rectangle();
        gameWorld = new Rectangle(0f, 0f, 896f, 512f);
        powerUpRectangle = new Rectangle();

        music.setLooping(true);
        music.setVolume(.1f);
        music.play();

        ok = new TextButton("OK", skin);
        ok.setSize(100, 50);
        ok.setPosition(middleWidth - (ok.getWidth()/2), middleHeight - 25);
        ok.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                ShowScores();
            }
        });
        restart = new TextButton("RESTART GAME", skin);
        restart.setSize(100, 50);
        restart.setPosition(middleWidth - (restart.getWidth()/2), middleHeight - 125);
        restart.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                RestartGame();
            }
        });

        usernameInput = new TextField("Username", skin);
        usernameInput.setPosition(middleWidth - (usernameInput.getWidth()/2), middleHeight + 75);
        usernames = new Label[] {new Label("no user", skin), new Label("no user", skin), new Label("no user", skin)};
        scores = new Label[] {new Label("0", skin), new Label("0", skin), new Label("0", skin)};
        for (int i = 0; i < 3; i++) {
            usernames[i].setSize(150, 50);
            scores[i].setSize(50, 50);
            usernames[i].setPosition((middleWidth - (usernames[i].getWidth()/2)) - 75, (middleHeight+100) - (i * 50));
            scores[i].setPosition(middleWidth - (scores[i].getWidth()/2) + 125, (middleHeight+100) - (i * 50));
        }
        HealthLabel = new Label("0", skin);
        HealthLabel.setSize(50,50);
        HealthLabel.setPosition(heartSprite.getX()+5, heartSprite.getY() + 10);
        HealthLabel.setAlignment(Align.center);
        stage.addActor(usernameInput);
        stage.addActor(ok);

    }

    public void RestartGame(){
        score = 0;
        username = "";
        gameOver = false;
        showScores = false;
        music.dispose();
        create();
    }

    public void GameOver() {
        gameOver = true;
    }

    public void ShowScores () {
        showScores = true;
        username = usernameInput.getText();

        UserManager.AddUserScore(username, score);
        List<String[]> dataScores = UserManager.DisplayUserScore();

        stage.clear();
        stage.addActor(restart);
        for (int i = 0; i < 3; i++){
            usernames[i].setText(dataScores.get(i)[0]);
            scores[i].setText(dataScores.get(i)[1]);
            stage.addActor(usernames[i]);
            stage.addActor(scores[i]);

            i = (i + 1) % 3;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        if (!gameOver) {logic();}
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
            horizontalVelocity += ((hero.speedTimer > 0? heroSpeed * 2: heroSpeed) * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) && !isPaused) {
            horizontalVelocity += (-(hero.speedTimer > 0? heroSpeed * 2: heroSpeed) * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && !isPaused) {
            verticalVelocity += ((hero.speedTimer > 0? heroSpeed * 2: heroSpeed) * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S) && !isPaused) {
            verticalVelocity += (-(hero.speedTimer > 0? heroSpeed * 2: heroSpeed) * delta);
        }

        if (!gameOver){
            hero.positionX += horizontalVelocity;
            hero.positionY += verticalVelocity;
            hero.sprite.translateX(horizontalVelocity);
            hero.sprite.translateY(verticalVelocity);
        }


        // shooting gun
        if (Gdx.input.isTouched() && timerReload <= 0f && !isPaused && !gameOver) {

            switch (hero.weapon) {
                case pistol -> {
                    timerReload = pistolReloadTime;
                    spawnBullet();
                }
                case shotgun -> {
                    timerReload = shotgunReloadTime;
                    spawnShotgunBullets();
                    hero.ammoCount--;
                    if (hero.ammoCount <= 0) hero.weapon = Hero.gunType.pistol;
                }
                case machinegun -> {
                    timerReload = machineGunReloadTime;
                    spawnBullet();
                    hero.ammoCount--;
                    if (hero.ammoCount <= 0) hero.weapon = Hero.gunType.pistol;
                }
            }
        }
    }

    private void PauseLogic(float delta) {
        //pause logic
        if (pauseTimer > 0) pauseTimer -= delta;
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE) && pauseTimer <= 0 && !gameOver) {
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


        // show health logic
        HealthLabel.setText(hero.health);

        //reload time logic
        if (timerReload > 0f && !isPaused) {
            timerReload -= delta;
        }

        //invTime Logic
        if (hero.invTimer > 0 && !isPaused) {
            hero.invTimer -= delta;
            hero.sprite.setColor(Color.RED);
        }
        else{
            hero.sprite.setColor(Color.WHITE);
        }

        //speedBoost Timer Logic
        if (hero.speedTimer > 0 && !isPaused) {
            hero.speedTimer -= delta;
        }

        ClampHero(worldWidth, playerWidth, worldHeight, playerHeight);

        //hero hitbox
        heroRectangle.set(playerCenter.x, playerCenter.y, playerWidth, playerHeight);
        heroRectangle.setCenter(playerCenter.x, playerCenter.y);

        SetSprite(playerCenter);

        HeroHitLogic(delta);

        BulletLogic(delta);

        ZombieSpawnLogic(delta);

        ZombieMoveLogic(playerCenter, delta);

        ScoreLogic(worldWidth, worldHeight);

        PowerUpsLogic(delta);


    }

    private void PowerUpsLogic(float delta) {
        //PowerUps Logic
        for (int i = 0; i < powerUps.size; i++) {
            PowerUp powerUp = powerUps.get(i);
            powerUp.PowerUpTick(delta);
            if (powerUp.currentLifeSpan <=0) {
                powerUps.removeIndex(i);
                continue;
            }

            powerUpRectangle.set(powerUp.positionX, powerUp.positionY, powerUp.sprite.getWidth(), powerUp.sprite.getHeight());
            powerUpRectangle.setCenter(powerUp.positionX, powerUp.positionY);
            if (heroRectangle.overlaps(powerUpRectangle)){
                switch (powerUp.powerUpType) {
                    case health: {
                        hero.health += 2;
                        MathUtils.clamp(hero.health, 0, 10);
                        break;
                    }
                    case shotgun:{
                        hero.weapon = Hero.gunType.shotgun;
                        hero.ammoCount = 20;
                        break;
                    }
                    case machinegun:{
                        hero.weapon = Hero.gunType.machinegun;
                        hero.ammoCount = 30;
                        break;
                    }
                    case bomb:{
                        break;
                    }
                    case speed:{
                        hero.speedTimer = speedBoostLength;
                        break;
                    }
                    default:{
                        break;
                    }
                }
                powerUps.removeIndex(i);
                return;
            }
        }
    }

    private void HeroHitLogic(float delta) {
        //hero hit logic
        for (Zombie zombie : zombies) {
            zombie.knockBackTick(delta);
            zombieRectangle.setCenter(zombie.positionX, zombie.positionY);
            if (heroRectangle.overlaps(zombieRectangle) && zombie.knockBackTimer <= 0 && hero.invTimer <= 0){
                Vector2 direction = new Vector2(zombie.positionX - hero.positionX, zombie.positionY - hero.positionY);
                direction.nor();
                zombie.Knockback(direction, zombiePushBack);
                hero.health -= 1;
                if (hero.health ==0){
                    GameOver();
                }
                hero.invTimer = invTime;
            }

        }
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
        for (Zombie zombie : zombies) {
            if (zombie.knockBackTimer <= 0) {

                Vector2 direction = new Vector2(playerCenter.x - zombie.positionX, playerCenter.y - zombie.positionY);
                direction.nor();
                zombie.sprite.translate(zombieSpeed * direction.x * delta, zombieSpeed * direction.y * delta);
                zombie.positionX = (zombie.sprite.getX() + (zombie.sprite.getWidth())/2) -5 ;
                zombie.positionY = (zombie.sprite.getY() + (zombie.sprite.getHeight())/2) -5 ;
                zombie.sprite.setScale(direction.x < 0 ? -1 : 1, 1);
            }
        }
    }

    private void ZombieSpawnLogic(float delta) {
        zombieSpawnTimer -= delta;
        if (zombies.size > 50) {return;}
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
            for (int j = zombies.size - 1; j >= 0; j--) {
                Zombie zombie = zombies.get(j);
                zombieRectangle.set(zombie.positionX, zombie.positionY, zombie.sprite.getWidth(), zombie.sprite.getHeight());
                zombieRectangle.setCenter(zombie.positionX, zombie.positionY);
                if (bullet.rectangle.overlaps(zombieRectangle)) {
                    int random = new Random().nextInt(1,16);
                    if (random == 1)
                    {
                        powerUps.add(new PowerUp(new Vector2(zombie.positionX, zombie.positionY)));
                    }
                    bullets.removeIndex(i);
                    zombies.removeIndex(j);

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
        heartSprite.draw(spriteBatch);
        HealthLabel.draw(spriteBatch, 1f);

        if (gameOver){ stage.draw();}

        if (isPaused) pauseSprite.draw(spriteBatch);

        for (Zombie zombie : zombies) {
            zombie.sprite.draw(spriteBatch);
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.sprite.draw(spriteBatch);
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

        Zombie zombie = new Zombie(MathUtils.random(0, 1) == 0 ? 0f : worldWidth - zombieWidth, MathUtils.random(0f, worldHeight - zombieHeight), new Sprite(zombieTexture));
        zombies.add(zombie);
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

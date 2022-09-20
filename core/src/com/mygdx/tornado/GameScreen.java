package com.mygdx.tornado;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    final Tornado game;

    Random random = new Random();

    Texture stormImage;
    Texture skillImage;
    Texture cactusImage;
    Texture activeImage;
    Texture coconutImage;
    Texture bubbleImage;
    Texture dimaImage;
    TextureRegion backgroundTexture;

    Sound dropSound;
    Music rainMusic;

    OrthographicCamera camera;
    Rectangle dima;
    Array<TornadoDrop> tornadoes;
    long lastDropTime;
    int dropsGathered;

    public GameScreen(final Tornado game) {
        this.game = game;

        skillImage = new Texture(Gdx.files.internal("skill.png"));
        stormImage = new Texture(Gdx.files.internal("storm.png"));
        cactusImage = new Texture(Gdx.files.internal("cactus.png"));
        activeImage = new Texture(Gdx.files.internal("active.png"));
        coconutImage = new Texture(Gdx.files.internal("coconut.png"));
        bubbleImage = new Texture(Gdx.files.internal("bubble.png"));
        dimaImage = new Texture(Gdx.files.internal("dima.png"));
        backgroundTexture = new TextureRegion(new Texture("background.jpg"), 0, 0, 1280, 720);

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        dima = new Rectangle();
        dima.x = 1280 / 2 - 64 / 2;
        dima.y = 20;
        dima.width = 64;
        dima.height = 64;

        tornadoes = new Array<>();

        spawnRaindrop();

    }

    private void spawnRaindrop() {
        Texture texture;
        Rectangle tornadoDrop = new Rectangle();
        int coin;

        tornadoDrop.x = MathUtils.random(0, 1280 - 32);
        tornadoDrop.y = 720;
        tornadoDrop.width = 32;
        tornadoDrop.height = 64;

        switch(random.nextInt(6)){

            case 1:
                texture = activeImage;
                tornadoDrop.height = 64;
                coin = 1;
                break;
            case 2:
                texture = stormImage;
                tornadoDrop.height = 64;
                coin = 1;
                break;
            case 3:
                texture = skillImage;
                tornadoDrop.height = 64;
                coin = 1;
                break;
            case 4:
                texture = cactusImage;
                tornadoDrop.height = 64;
                coin = 1;
                break;
            case 5:
                texture = coconutImage;
                tornadoDrop.height = 64;
                coin = 1;
                break;
            default:
                texture = bubbleImage;
                tornadoDrop.height = 96;
                coin = 2;
        }

        tornadoes.add(new TornadoDrop(tornadoDrop, texture, coin));
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0);
        game.font.draw(game.batch, "Liters Collected: " + dropsGathered, 0, 720);
        game.batch.draw(dimaImage, dima.x, dima.y);
        for (TornadoDrop tornadoDrop : tornadoes) {
            game.batch.draw(tornadoDrop.texture, tornadoDrop.rectangle.x, tornadoDrop.rectangle.y);
        }
        game.batch.end();
        //1
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            dima.x = (int) (touchPos.x - 64 / 2);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            dima.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            dima.x += 200 * Gdx.graphics.getDeltaTime();

        if (dima.x < 0)
            dima.x = 0;
        if (dima.x > 1280 - 64)
            dima.x = 1280 - 64;

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnRaindrop();

        Iterator<TornadoDrop> iter = tornadoes.iterator();
        while (iter.hasNext()) {
            TornadoDrop tornadoDrop = iter.next();
            tornadoDrop.rectangle.y -= 400 * Gdx.graphics.getDeltaTime();
            if (tornadoDrop.rectangle.y + 32 < 0) {
                iter.remove();
            }
            if (tornadoDrop.rectangle.overlaps(dima)) {
                dropsGathered+=tornadoDrop.coin;
                dropSound.play();
                iter.remove();
            }
            if (dropsGathered >= 5){
                game.setScreen(new GoodEndingScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stormImage.dispose();
        skillImage.dispose();
        bubbleImage.dispose();
        coconutImage.dispose();
        cactusImage.dispose();
        activeImage.dispose();

        dimaImage.dispose();

        dropSound.dispose();
        rainMusic.dispose();
    }
}

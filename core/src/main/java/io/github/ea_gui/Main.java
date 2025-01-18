package io.github.ea_gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class Main extends Game {

    public SpriteBatch batch;
    public BitmapFont font;
    public ScreenViewport viewport;
    public OrthographicCamera camera;


    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); //default font
//        viewport = new FitViewport(8, 5);
        camera = new OrthographicCamera();

        viewport = new ScreenViewport(camera);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        font.setUseIntegerPositions(false);
        font.getData().setScale(Gdx.graphics.getWidth() / Gdx.graphics.getHeight());

        this.setScreen(new IntroScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }

}

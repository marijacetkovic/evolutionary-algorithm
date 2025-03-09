package io.github.ea_gui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class IntroScreen implements Screen {
    private final Main game;

    public IntroScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.camera.combined);

        String title = "Welcome to the Evolution Simulation!";
        String subtitle = "Tap anywhere to begin!";

        game.batch.begin();

        game.font.setColor(Color.RED);

        float titleWidth = game.font.getRegion().getRegionWidth();
        float titleHeight = game.font.getRegion().getRegionHeight();
        float titleX = (Gdx.graphics.getWidth() - titleWidth) / 2;
        float titleY = Gdx.graphics.getHeight() / 2 + titleHeight / 2;
        game.font.draw(game.batch, title, titleX, titleY);

        game.font.setColor(Color.RED);

        float subtitleWidth = game.font.getRegion().getRegionWidth();
        float subtitleHeight = game.font.getRegion().getRegionHeight();
        float subtitleX = (Gdx.graphics.getWidth() - subtitleWidth) / 2;
        float subtitleY = titleY - titleHeight+40;
        game.font.draw(game.batch, subtitle, subtitleX, subtitleY);

        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new SimulationScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

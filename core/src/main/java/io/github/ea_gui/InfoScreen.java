package io.github.ea_gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

public class InfoScreen implements Screen {
    private final Main game;
    private String[] text;
    private boolean start;

    public InfoScreen(Main game, String[] text, boolean start) {
        this.game = game;
        this.text = text;
        this.start = start;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.font.getData().setScale(1f);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        game.font.setColor(Color.RED);

        float titleWidth = game.font.getRegion().getRegionWidth();
        float titleHeight = game.font.getRegion().getRegionHeight();
        float titleX = (Gdx.graphics.getWidth() - titleWidth) / 2;
        float titleY = Gdx.graphics.getHeight() / 2 + titleHeight / 2;
        game.font.draw(game.batch, text[0], titleX, titleY);

        game.font.setColor(Color.RED);

        float subtitleWidth = game.font.getRegion().getRegionWidth();
        float subtitleHeight = game.font.getRegion().getRegionHeight();
        float subtitleX = (Gdx.graphics.getWidth() - subtitleWidth) / 2;
        float subtitleY = titleY - titleHeight+40;
        game.font.draw(game.batch, text[1], subtitleX, subtitleY);

        game.batch.end();

        if (Gdx.input.isTouched()) {
            if(start==true) {
                game.setScreen(new SimulationScreen(game));
            }
            else{
                Gdx.app.exit();
            }
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

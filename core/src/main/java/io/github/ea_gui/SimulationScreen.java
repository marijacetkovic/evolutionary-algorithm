package io.github.ea_gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.evolutionary_algorithm.World;

public class SimulationScreen implements Screen {
    private final Main game;
    private World world;
    private WorldRenderer worldRenderer;
    public SimulationScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        world = new World(10);
        worldRenderer = new WorldRenderer(world,game);
    }
    float time;
    @Override
    public void render(float delta) {
        //world.behave();
        //float deltax = Gdx.graphics.getDeltaTime();
        time+=delta;
        if(time>2f){
            time = 0;
            world.behave();
        }
        else{worldRenderer.render(delta);}
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

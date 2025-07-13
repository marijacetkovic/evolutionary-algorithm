package io.github.ea_gui;

import com.badlogic.gdx.Screen;
import io.github.evolutionary_algorithm.EvolutionManager;
import io.github.evolutionary_algorithm.World;

public class SimulationScreen implements Screen {
    private final Main game;
    private World world;
    private WorldRenderer worldRenderer;
    String[] text;
    private float accumulator;
    private static final float FIXED_TIMESTEP = 1 / 30f;
    private EvolutionManager evolutionManager;

    public SimulationScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        evolutionManager = EvolutionManager.getInstance();
        world = evolutionManager.getWorld();
        worldRenderer = new WorldRenderer(world,game,evolutionManager);
        text = new String[]{
            "Simulation finished.",
            "Tap anywhere to close!"
        };
    }
    float time;
    @Override
    public void render(float delta) {
        float frameTime = Math.min(delta, 0.1f);
        accumulator += frameTime;

        while (accumulator >= FIXED_TIMESTEP) {
            boolean populationAlive = world.behave();
            evolutionManager.monitor();
            if(!populationAlive) {
                boolean simulationComplete = evolutionManager.update();

                if (simulationComplete) {
                    worldRenderer.dispose();
                    game.setScreen(new InfoScreen(game, text, false));
                    return;
                }
            }
            accumulator -= FIXED_TIMESTEP;
        }

        float alpha = accumulator / FIXED_TIMESTEP;
        worldRenderer.render(alpha);
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

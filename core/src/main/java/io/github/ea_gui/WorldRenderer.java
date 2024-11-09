package io.github.ea_gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.evolutionary_algorithm.World;

public class WorldRenderer {
    private World world;
    private Texture circleTexture;
    private Main game;
    float circleRadius = 0.2f;
    float padding = 0.02f;
    public WorldRenderer(World world, Main game) {
        this.world = world;
        this.game = game;
        circleTexture = new Texture(Gdx.files.internal("C:/EvolutionaryAlgorithm/assets/tile.png"));
    }

    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();


        // Render the grid of circles
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                float x = calcX(j);
                float y = calcY(i);

                game.batch.draw(circleTexture, x, y, circleRadius * 2, circleRadius * 2);
            }
        }

        game.batch.end();
    }

    private float calcX(int j){
        return j * (circleRadius * 2 + padding) + game.viewport.getWorldWidth() / 4;
    }
    private float calcY(int i){
        return i * (circleRadius * 2 + padding) + game.viewport.getWorldHeight() / 10;
    }
}


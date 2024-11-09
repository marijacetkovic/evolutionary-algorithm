package io.github.ea_gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.evolutionary_algorithm.Creature;
import io.github.evolutionary_algorithm.World;

public class WorldRenderer {
    private World world;
    private Texture tileTexture;
    private final Texture creatureTexture;

    private Main game;
    float tileSize;
    float padding;
    //float worldSize = (10-1)*(tileSize+padding);
    int n;
    public WorldRenderer(World world, Main game) {
        this.world = world;
        this.game = game;
        this.n = world.getSize();
        this.tileSize = 4f/n;
        this.padding = 0.2f/n;
        tileTexture = new Texture(Gdx.files.internal("C:/EvolutionaryAlgorithm/assets/tile.png"));
        creatureTexture = new Texture(Gdx.files.internal("C:/EvolutionaryAlgorithm/assets/monster.png"));
    }

    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();


        // Render the grid of circles
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                float x = calcX(j);
                float y = calcY(i);

                game.batch.draw(tileTexture, x, y, tileSize, tileSize);
            }
        }
        for (Creature c : world.getPopulation()) {
            //must invert Y coordinate
            game.batch.draw(creatureTexture, calcX(c.getJ()), calcY(n-1-c.getI()), tileSize, tileSize);
        }
        //game.batch.draw(creatureTexture, 0, 0, tileSize, tileSize);

        game.batch.end();
    }

    private float calcX(int j){
        return j * (tileSize + padding) + game.viewport.getWorldWidth() / 4;
    }
    private float calcY(int i){
        return i * (tileSize + padding) + game.viewport.getWorldHeight() / 10;
    }
}


package io.github.ea_gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.evolutionary_algorithm.Creature;
import io.github.evolutionary_algorithm.Food;
import io.github.evolutionary_algorithm.World;

public class WorldRenderer {
    private World world;
    private final Texture tileTexture;
    private final Texture creatureTexture;
    private final Texture foodTexture;

    private Main game;
    float tileSize;
    float padding;
    //float worldSize = (10-1)*(tileSize+padding);
    int n;
    private TiledMap map;
    private final TiledMapRenderer renderer;
    int tiled = 1;

    public WorldRenderer(World world, Main game) {
        this.world = world;
        this.game = game;
        this.n = world.getSize();
        this.tileSize = 4f/n;
        this.padding = 0.2f/n;
        tileTexture = new Texture(Gdx.files.internal("C:/EvolutionaryAlgorithm/assets/tile.png"));
        creatureTexture = new Texture(Gdx.files.internal("C:/EvolutionaryAlgorithm/assets/monster.png"));
        foodTexture = new Texture(Gdx.files.internal("C:/EvolutionaryAlgorithm/assets/food1.png"));
        map = new TmxMapLoader().load("novaMapa.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1f / 64f);
    }

    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();
        renderGrid();
        renderFood();
        renderCreatures();
        game.batch.end();
    }

    private void renderGrid() {
        if(tiled==1){
            renderer.setView((OrthographicCamera) game.viewport.getCamera());
            renderer.render();
        }
        else {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    float x = calcX(j);
                    float y = calcY(i);
                    game.batch.draw(tileTexture, x, y, tileSize, tileSize);
                }
            }
        }
    }


    private void renderCreatures() {
        for (Creature c : world.getPopulation()) {
            float x = calcX(c.getJ());
            float y = calcY(n - 1 - c.getI());
            game.batch.draw(creatureTexture, x, y, tileSize, tileSize);
            game.font.setColor(Color.GREEN);
            game.font.draw(game.batch, c.getHealth() + "", x + tileSize/5, y + tileSize /5);
            game.font.setColor(Color.BLACK);
            game.font.draw(game.batch, c.getId() + "", x + tileSize / 2, y + tileSize / 2);
        }
    }

    private void renderFood(){
        for (Food f : world.getFood()) {
            float x = calcX(f.getJ());
            float y = calcY(n - 1 - f.getI());
            game.batch.draw(foodTexture, x, y, 0.75f*tileSize, 0.75f*tileSize);
        }
    }

    private float calcX(int j){
        return j * (tileSize + padding) + game.viewport.getWorldWidth() / 4;
    }
    private float calcY(int i){
        return i * (tileSize + padding) + game.viewport.getWorldHeight() / 10;
    }
}


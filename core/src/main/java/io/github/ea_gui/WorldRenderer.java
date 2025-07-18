package io.github.ea_gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.evolutionary_algorithm.*;

import static io.github.ea_gui.Config.*;
import static io.github.evolutionary_algorithm.Config.FOOD_CODE_MEAT;
import static io.github.evolutionary_algorithm.Config.FOOD_CODE_PLANT;

public class WorldRenderer {
    private final EvolutionManager evolutionManager;
    private World world;
    private final Texture tileTexture;
    private final Texture creatureTexture;
    private final Texture foodTexture0;
    private final Texture foodTexture1;

    private Main game;
    float tileSize;
    float padding;
    //float worldSize = (10-1)*(tileSize+padding);
    int n;
    private TiledMap map;
    private final TiledMapRenderer renderer;
    int tiled = 1;

    public WorldRenderer(World world, Main game, EvolutionManager evolutionManager) {
        this.world = world;
        this.game = game;
        this.n = world.getSize();
        this.tileSize = 32f;
        this.padding = 1/100f;
        this.evolutionManager = evolutionManager;
        tileTexture = new Texture(Gdx.files.internal(TILE_TEXTURE));
        creatureTexture = new Texture(Gdx.files.internal(CREATURE_TEXTURE));
        foodTexture0 = new Texture(Gdx.files.internal(FOOD_TEXTURE_0));
        foodTexture1 = new Texture(Gdx.files.internal(FOOD_TEXTURE_1));
        String mapFilePath = getMapFilePath(this.n);
        map = new TmxMapLoader().load(mapFilePath);
        renderer = new OrthogonalTiledMapRenderer(map, 1f);
    }

    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();
        renderGrid();
        renderTiles();
        renderUI();
        game.batch.end();
    }

    private void renderGrid() {
        if(tiled==1){
            OrthographicCamera camera = (OrthographicCamera) game.viewport.getCamera();

            float worldWidth = n * tileSize;
            float worldHeight = n * tileSize;

            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();
            float aspectRatio = screenWidth / screenHeight;

            if (aspectRatio > 1) {
                camera.setToOrtho(false, worldHeight * aspectRatio, worldHeight);
            } else {
                camera.setToOrtho(false, worldWidth, worldWidth / aspectRatio);
            }

            camera.position.set(worldWidth / 2f, worldHeight / 2f, 0);
            camera.update();

            renderer.setView(camera);
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
    private void renderUI() {
        game.font.setColor(Color.BLACK);
        game.font.getData().setScale(4f);

        int generation = evolutionManager.getCurrentGeneration();
        int species = evolutionManager.getNeatManager().getSpeciesManager().getSpeciesList().size();
        float textMargin = 10;
        OrthographicCamera camera = (OrthographicCamera) game.viewport.getCamera();
        float x = camera.position.x - camera.viewportWidth / 2f + textMargin;
        float y = camera.position.y + camera.viewportHeight / 2f - textMargin;
        game.font.draw(game.batch, "Generation: " + generation, x, y);
        game.font.draw(game.batch, "Species: " + species, x, y - game.font.getLineHeight() - textMargin / 2);

        game.font.setColor(Color.WHITE); // Reset font color
    }

    private void renderTiles() {
        game.font.getData().setScale(1f);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Tile tile = world.world[i][j];
                float x = calcX(j);
                float y = calcY(n - 1 - i);


                for (Integer creatureId : tile.getCreatures()) {
                    AbstractCreature c = world.findCreatureById(creatureId);
                    if (c != null) {
                        game.batch.draw(creatureTexture, x, y, tileSize, tileSize);
                        game.font.setColor(Color.GREEN);
                        game.font.draw(game.batch, c.getHealth() + "", x + tileSize / 5, y + tileSize / 5);
                        game.font.setColor(Color.BLACK);
                        game.font.draw(game.batch, c.getId() + "", x + tileSize / 2, y + tileSize / 2);
                    }
                }

                for (Food f : tile.getFoodItems()) {
                    Texture foodTexture = null;

                    if (f.getCode() == FOOD_CODE_PLANT) {
                        foodTexture = foodTexture0;
                    } else if (f.getCode() == FOOD_CODE_MEAT) {
                        foodTexture = foodTexture1;
                    }

                    if (foodTexture != null) {
                        game.batch.draw(foodTexture, x, y, 0.75f * tileSize, 0.75f * tileSize);
                    }
                }
            }
        }
    }

    private float calcX(int j){
        return j * (tileSize + padding);
    }
    private float calcY(int i){
        return i * (tileSize + padding);
    }
    private String getMapFilePath(int worldSize) {
        String path = "maps/map_" + worldSize + ".tmx";
        if (!Gdx.files.internal(path).exists()) {
            throw new IllegalArgumentException("Map file not found for world size " + worldSize);
        }
        return path;
    }
    public void dispose() {
        tileTexture.dispose();
        creatureTexture.dispose();
        foodTexture0.dispose();
        foodTexture1.dispose();
        map.dispose();
    }
}


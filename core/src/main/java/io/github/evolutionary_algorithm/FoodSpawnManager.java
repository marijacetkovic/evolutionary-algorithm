package io.github.evolutionary_algorithm;

import java.util.Random;
import static io.github.evolutionary_algorithm.Config.*;

public class FoodSpawnManager {
    private World world;
    private Random r;
    private int currentGeneration;

    public FoodSpawnManager(World world) {
        this.world = world;
        this.r = new Random();
        this.currentGeneration = 0;
    }

    public void incrementGeneration() {
        this.currentGeneration++;
    }

    public void spawnFood(int n) {
        if (currentGeneration < FOOD_PHASE_END1) {
            spawnFood(n,2);
        } else if (currentGeneration < FOOD_PHASE_END2) {
            spawnFood(n,0);
        } else if (currentGeneration < FOOD_PHASE_END3) {
            spawnFood(n,1);
        }
        else{
            int s = r.nextInt(FOOD_LOCATION_BOUND);
            spawnFood(n,s);
        }
    }

    public void checkFoodQuantity() {
        if (world.getFood().size() < NUM_FOOD / MIN_FOOD_LVL) {
            int f = NUM_FOOD - world.getFood().size();
            spawnFood(f);
            System.out.println("Food quantity increased.");
        }
    }

    //food across the whole world
    private void spawnFood(int n, int spawnZoneType) {
        //int spawnZoneType = r.nextInt(FOOD_LOCATION_BOUND);
        int quadrantX = r.nextInt(FOOD_QUADRANT_BOUND);
        int quadrantY = r.nextInt(FOOD_QUADRANT_BOUND);
        int size = world.getSize();
        for (int k = 0; k < n; k++) {
            int i, j;
            switch (spawnZoneType) {
                case 0:
                    i = r.nextInt(size / 2) + (quadrantX * size / 2);
                    j = r.nextInt(size / 2) + (quadrantY * size / 2);
                    break;

                case 1:
                    i = r.nextInt(size / 4) + (quadrantX * size / 2);
                    j = r.nextInt(size / 4) + (quadrantX * size / 2);
                    break;
                default:
                    i = r.nextInt(size);
                    j = r.nextInt(size);
                    break;
            }
            // } while (world[i][j].getFoodItems().contains(-2));

            world.addFood(i,j);
        }
    }


}

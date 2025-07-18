package io.github.evolutionary_algorithm;

import java.util.Random;
import static io.github.evolutionary_algorithm.Config.*;

public class FoodSpawnManager {
    private World world;
    private Random r;

    public FoodSpawnManager(World world) {
        this.world = world;
        this.r = new Random();
    }


    public void spawnFood(int n, int currentGeneration) {
        if (currentGeneration < FOOD_PHASE_END1) {
            spawnFoodPattern(n,0);
        } else if (currentGeneration < FOOD_PHASE_END2) {
            spawnFoodPattern(n,1);
        } else if (currentGeneration < FOOD_PHASE_END3) {
            spawnFoodPattern(n,2);
        }
        else{
            int s = r.nextInt(FOOD_LOCATION_BOUND);
            spawnFoodPattern(n,s);
        }
    }

    public void checkFoodQuantity(int gen) {
        if (world.getFood().size() < NUM_FOOD / MIN_FOOD_LVL) {
            int f = NUM_FOOD - world.getFood().size();
            spawnFood(f, gen);
            System.out.println("Food quantity increased.");
        }
    }

    //food across the whole world
    private void spawnFoodPattern(int n, int spawnZoneType) {
        //int spawnZoneType = r.nextInt(FOOD_LOCATION_BOUND);
        int quadrantX = r.nextInt(FOOD_QUADRANT_BOUND);
        int quadrantY = r.nextInt(FOOD_QUADRANT_BOUND);
        int size = world.getSize();
        for (int k = 0; k < n; k++) {
            int i, j;
            switch (spawnZoneType) {
                case 1:
                    i = r.nextInt(size / 2) + (quadrantX * size / 2);
                    j = r.nextInt(size / 2) + (quadrantY * size / 2);
                    break;

                case 2:
                    i = r.nextInt(size / 4) + (quadrantX * size / 2);
                    j = r.nextInt(size / 4) + (quadrantY * size / 2);
                    break;

                case 0:
                    i = r.nextInt(size / 3) + r.nextInt(size / 5);
                    j = r.nextInt(size);
                    break;
                default:
                    i = r.nextInt(size);
                    j = r.nextInt(size);
                    break;
            }
            Food f = createRandomFood(i,j);
            world.addFood(i,j,f);
        }
    }

    private Food createRandomFood(int i, int j) {
        double prob = r.nextDouble();

        //control spawn by prob
        if (prob < Config.PLANT_SPAWN_PROBABILITY) {
            return new Food(i, j, Config.PLANT_FOOD_NUTRITION, Config.FOOD_CODE_PLANT);
        } else {
            return new Food(i, j, Config.MEAT_FOOD_NUTRITION, Config.FOOD_CODE_MEAT);
        }
    }

}

package io.github.evolutionary_algorithm;

import io.github.neat.Genome;
import java.util.List;
import java.util.Random;
import static io.github.evolutionary_algorithm.Config.*;
import static io.github.neat.Config.numInputs;

public class Creature extends AbstractCreature {

    public Creature(int id, int i, int j, int foodType, Genome genome){
        super(id,i,j,foodType,genome);
        this.dietType = assignDiet();
    }

    double[] getEnvironmentInput(World world) {
        double[] inputs = new double[numInputs];
        int idx = 0;
        inputs[idx++] = world.world[i][j].hasFood() ? 1.0 : -1.0;

        //here need to add two new inputs for food
        //inputs for other creature health

        //neighbors excluding current cell
        int[][] directions = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};

        for (int[] dir : directions) {
            int x = i + dir[0];
            int y = j + dir[1];

            //maybe can remove this
            // wall detection
            inputs[idx++] = world.isWall(x, y) ? 1 : -1;

            // food presence
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasFood()) ? 1 : 0;

            // creature presence
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasCreature(id)) ? 1 : 0;
        }

        // normalized health
        inputs[idx++] = health / (double)INITIAL_HEALTH;

        // vector pointing to closest food source to guide agent
        double[] foodVec = getClosestFoodVector(world);
        double dist = Math.sqrt(foodVec[0]*foodVec[0] + foodVec[1]*foodVec[1]);

        //continuous vector
        if (dist > 0) {
            inputs[idx++] = foodVec[0] / dist;
            inputs[idx++] = foodVec[1] / dist;
        } else {
            //agent is on food
            inputs[idx++] = 0.0;
            inputs[idx++] = 0.0;
        }

        // normalized food dist
        inputs[idx++] = Math.min(1, dist / (world.getSize()/2.0));

        return inputs;
    }

    void checkEatingAction(EventManager eventManager, World world){
        List<Food> food = world.world[i][j].getFoodItems();

        if (food.size() > 0) {
            for (Food f : food) {
                int foodCode = f.getCode();

                boolean canEat = false;
                if (foodCode == Config.FOOD_CODE_PLANT) {
                    if (this.dietType == DietType.HERBIVORE || this.dietType == DietType.OMNIVORE) {
                        canEat = true;
                    }
                } else if (foodCode == Config.FOOD_CODE_MEAT) {
                    if (this.dietType == DietType.CARNIVORE || this.dietType == DietType.OMNIVORE) {
                        canEat = true;
                    }
                }

                if (canEat) {
                    eventManager.publish(new EatingEvent(this, i, j, world, f), true);
                    hasEaten = true;
                    return;
                }
            }
        }
    }
    void checkBreedingAction(EventManager eventManager, World world) {
        // wantToMate = true;
        potentialMates = world.checkMateTile(this);
        if(mateWithMe()){
            System.out.println("Creature " + id + " found mate " + mate.getId() + " at " + i + " " + j);
            eventManager.publish(new BreedingEvent(this, mate, world),false);
            mate.resetMates();
            this.resetMates();
        }
    }

     boolean mateWithMe() {
        //System.out.println("Potential mates for "+id);
        for (AbstractCreature c : potentialMates) {
            //System.out.println("Mate "+c.getId());
            if (c!=null && c.hasMate(id)) {
                this.mate = c;
                //this.wantToMate = false;
                return true;
            }
        }
        return false;
    }

    //<--- CHECK
    public void consume(Food f){
        health += f.getNutrition();
    }

    //need to change fitness function
    public void evaluateAction(World w) {
        double[] foodVector = getClosestFoodVector(w);
        double currentDistance = Math.sqrt(foodVector[0] * foodVector[0] + foodVector[1] * foodVector[1]);

        if (currentDistance < prevFoodDistance) {
            fitness += 0.5;
        } else {
            fitness -= 0;
        }
        prevFoodDistance = currentDistance;

        if (hasEaten) {
            fitness += 50.0;
            timeSinceEaten = 0;
            hasEaten = false;
        } else {
            timeSinceEaten++;
        }

        /*boolean hitWall = w.isWall(i,j);
        if (hitWall) {
            fitness -= 100;
        }

        if (timeSinceEaten > 10) {
            fitness -= 10;
        }*/

        //transfer fitness to genome
        this.genome.setFitness(fitness);
    }
    private double getPreviousFoodDistance() {
        return prevFoodDistance;
    }
    private DietType assignDiet() {
        double r = new Random().nextDouble();
        if (r < Config.HERBIVORE_PROB) {
            return DietType.HERBIVORE;
        } else if (r < Config.HERBIVORE_PROB + Config.CARNIVORE_PROB) {
            return DietType.CARNIVORE;
        } else {
            return DietType.OMNIVORE;
        }
    }

}

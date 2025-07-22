package io.github.evolutionary_algorithm;

import io.github.neat.Genome;
import java.util.List;
import java.util.Random;

import static io.github.evolutionary_algorithm.AbstractCreature.DietType.*;
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
        double[] dietInputs = getDietInput();

        inputs[idx++] = dietInputs[0];
        inputs[idx++] = dietInputs[1];


        //neighbors including current cell
        int[][] directions =
            {{-1,-1},{-1,0},{-1,1},
            {0,-1},{0,0},{0,1},
            {1,-1},{1,0},{1,1}};

        for (int[] dir : directions) {
            int x = i + dir[0];
            int y = j + dir[1];

            //maybe can remove this
            // wall detection
            inputs[idx++] = world.isWall(x, y) ? 1 : 0;

            // food presence
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasPlantFood()) ? 1 : 0;
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasMeatFood()) ? 1 : 0;

            // creature presence (excl self)
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasCreature(id)) ? 1 : 0;
        }

        // normalized hunger
        inputs[idx++] = 1 - health / (double)INITIAL_HEALTH;

        // vector pointing to closest food source to guide agent
        double[] foodVec = getClosestFoodVector(world);
        //Manhattan
        double dist = Math.abs(foodVec[0]) + Math.abs(foodVec[1]);

        //continuous vector
        if (dist > 0) {
            inputs[idx++] = foodVec[0] / dist;
            inputs[idx++] = foodVec[1] / dist;
        } else {
            //agent is on food
            inputs[idx++] = 0.0;
            inputs[idx++] = 0.0;
        }

        // normalized food dist - <--- Maybe not needed
        inputs[idx++] = Math.min(1, dist / (world.getSize()/2.0));

        return inputs;
    }

    void checkEatingAction(EventManager eventManager, World world){
        List<Food> food = world.world[i][j].getFoodItems();

        if (food.size() > 0) {
            for (Food f : food) {
                if (canEat(f)) {
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
        if (health<MAX_HEALTH){
            health += f.getNutrition();
        }
    }

    //need to change fitness function
    public void evaluateAction(World w) {
        fitness++;
//        double[] foodVector = getClosestFoodVector(w);
//        double currentDistance = Math.sqrt(foodVector[0] * foodVector[0] + foodVector[1] * foodVector[1]);
//
//        if (currentDistance < prevFoodDistance) {
//            fitness += 0.5;
//        } else {
//            fitness -= 0;
//        }
//        prevFoodDistance = currentDistance;
//
//        if (hasEaten) {
//            fitness += 50.0;
//            timeSinceEaten = 0;
//            hasEaten = false;
//        } else {
//            timeSinceEaten++;
//        }

        //transfer fitness to genome
        this.genome.setFitness(fitness);
    }
    private double getPreviousFoodDistance() {
        return prevFoodDistance;
    }
    private DietType assignDiet() {
        double r = new Random().nextDouble();
        if (r < HERBIVORE_PROB) {
            return HERBIVORE;
        } else if (r < HERBIVORE_PROB + CARNIVORE_PROB) {
            return CARNIVORE;
        } else {
            return OMNIVORE;
        }
    }

}

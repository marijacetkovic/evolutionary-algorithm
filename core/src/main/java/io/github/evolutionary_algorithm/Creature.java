package io.github.evolutionary_algorithm;

import io.github.evolutionary_algorithm.events.*;
import io.github.neat.Genome;
import java.util.List;
import java.util.Random;

import static io.github.evolutionary_algorithm.AbstractCreature.DietType.*;
import static io.github.evolutionary_algorithm.Config.*;
import static io.github.neat.Config.numInputs;

public class Creature extends AbstractCreature {

    private int intendedAction;
    private Creature intendedTarget;

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
            inputs[idx++] = world.isWall(x, y) ? 0 : 1;

            // food presence
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasPlantFood()) ? 1 : 0;
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasMeatFood()) ? 1 : 0;

            // creature presence (excl self)
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasHerbivore(id)) ? 1 : 0;
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasCarnivore(id)) ? 1 : 0;

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
                    eventManager.publish(new EatingEvent(this,world, f), true);
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

    //checks if there is a chosen target and if its reachable
    void checkAttackAction(EventManager eventManager, World w, Creature targetCreature) {
        //change to adapt the range
        if (targetCreature != null &&
            intendedTarget.getI() == this.i && intendedTarget.getJ() == this.j) {
            eventManager.publish(new AttackEvent(this, targetCreature, w), false);
        }
    }

    public void chooseAction(EventManager eventManager, World world) {
        //inject genome here
        double[] input = this.getEnvironmentInput(world);
        //double[] input = getRndInput();
        this.intendedAction = genome.calcPropagation(input);
        this.intendedTarget = null;
        //System.out.println("Individuals decision to move: "+actions[decision]);

        if (intendedAction == Config.ACTION_ATTACK) {
            findPotentialTarget(world);
        }

    }

    public void checkHealth(EventManager eventManager, World world){
        if (isDead()) {
            return;
        }

        this.health -= HEALTH_PENALTY;

        if (this.health <= 0) {
            this.health = 0;
            if (!this.isDead()) {
                eventManager.publish(new DeathEvent(this, world), true);
            }
        }
    }
    public void performAction(EventManager eventManager, World world) {
        if (isDead()) return;

        switch (intendedAction) {
            case Config.ACTION_EAT -> checkEatingAction(eventManager, world);
            case Config.ACTION_ATTACK -> checkAttackAction(eventManager, world, intendedTarget);
            //case Config.ACTION_BREED -> checkBreedingAction(eventManager, world, intendedTarget);
            default -> checkMovingAction(world, intendedAction);
        }
    }
    public void checkMovingAction(World world, int decision){
        int i = this.i+actionOffset[decision][0];
        int j = this.j+actionOffset[decision][1];
        world.moveCreature(this,i,j);
    }

    private void findPotentialTarget(World world) {
        // <---- Is this needed?
        if (this.health <= INITIAL_HEALTH / 3) return;


        for (Integer id : world.world[i][j].getOtherCreatures(this.id)) {
            AbstractCreature c = world.findCreatureById(id);
            if (c != null //&& !c.isDead()
             ) {
                intendedTarget = (Creature) c;
                break;
            }
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
        health = (int) Math.max(MAX_HEALTH, health+f.getNutrition());
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

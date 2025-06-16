package io.github.evolutionary_algorithm;

import io.github.neat.Genome;

import java.util.List;
import java.util.Random;

import static io.github.evolutionary_algorithm.Config.*;
import static io.github.evolutionary_algorithm.Config.Phase.AUTO;
import static io.github.neat.Config.numInputs;
import static java.lang.Math.abs;

public class Creature {
    private int id;
    //position within the world
    private int i;
    private int j;
    private static Random r = new Random();
    private boolean wantToMate;
    private boolean eatsFood;
    public List<Creature> potentialMates;
    private int calories;
    private Creature mate;

    private final int foodType;
    private int health;
    private Genome genome;
    String[] actions = { "up", "left", "down", "right", "none", "eat"};
    int[][] actionOffset = { {-1,0}, {0,-1}, {1,0}, {0,1}, {0,0}};
    private boolean hasEaten;
    private int fitness;
    private double closestFoodDistance;
    private double prevFoodDistance;
    private int wallPenaltyCnt;
    private int timeSinceEaten;

    public Creature(int id, int i, int j, int foodType, Genome genome){
        this.id = id;
        this.i = i;
        this.j = j;
        this.foodType = foodType;
        this.mate = null;
        this.genome = genome;
        this.hasEaten = false;
        this.health = Config.INITIAL_HEALTH;
        this.fitness = 0;
        wallPenaltyCnt = 0;
        timeSinceEaten = 0;
        System.out.println("Spawned a creature at positions " +i+","+j);
    }




    public int getFoodType() {
        return foodType;
    }

    public int getHealth() {
        return health;
    }

    public void takeAction(EventManager eventManager, World world) {
        //inject genome here
        double[] input = this.getEnvironmentInput(world);
        //double[] input = getRndInput();
        int decision = genome.calcPropagation(input);
        System.out.println("Individuals decision to move: "+actions[decision]);

        performInWorld(eventManager,world,decision);
    }

    private void performInWorld(EventManager eventManager, World world, int decision) {
        if (decision == ACTION_EAT){
            checkEatingAction(eventManager, world);
        }
        else{
            int i = this.i+actionOffset[decision][0];
            int j = this.j+actionOffset[decision][1];

            world.moveCreature(this,i,j);
        }
        //add one more output node for this
        if (currentPhase==AUTO && fitness>breedingThreshold){
            checkBreedingAction(eventManager, world);
        }

    }

    private double[] getEnvironmentInput(World world) {
        double[] inputs = new double[numInputs]; // Fixed size for performance
        int idx = 0;
        inputs[idx++] = world.world[i][j].hasFood() ? 1.0 : -1.0;

        //neighbors excluding current cell
        int[][] directions = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};

        for (int[] dir : directions) {
            int x = i + dir[0];
            int y = j + dir[1];

            // wall detection
            inputs[idx++] = world.isWall(x, y) ? 1 : -1;

            // food presence
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasFood()) ? 1 : 0;

            // creature presence
            inputs[idx++] = (world.isWithinBounds(x, y) && world.world[x][y].hasCreature(id)) ? 1 : 0;
        }

        // normalized health
        inputs[idx++] = health / (double)INITIAL_HEALTH;

        // vector pointing to closest food source
        double[] foodVec = getClosestFoodVector(world);
        double dist = Math.sqrt(foodVec[0]*foodVec[0] + foodVec[1]*foodVec[1]);

        //x direction -1 up 1 down 0 nothing
        inputs[idx++] = (abs(foodVec[0]) > abs(foodVec[1])) ? (foodVec[0] > 0 ? 1 : -1) : 0;

        // y direction -1 left 1 right 0 nothing
        inputs[idx++] = (abs(foodVec[1]) > abs(foodVec[0])) ? (foodVec[1] > 0 ? 1 : -1) : 0;

        // normalized food dist
        inputs[idx++] = Math.min(1, dist / (world.getSize()/2.0));

        return inputs;
    }

    private double[] getClosestFoodVector(World world) {
        double[] bestFoodVector = new double[]{0.0, 0.0};
        double minDistanceSquared = Double.POSITIVE_INFINITY;

        for (int x = 0; x < world.getSize(); x++) {
            for (int y = 0; y < world.getSize(); y++) {
                Tile tile = world.world[x][y];
                if (tile.hasFood()) {
                    double dx = x - this.i;
                    double dy = y - this.j;
                    double currentDistanceSquared = dx * dx + dy * dy;

                    if (currentDistanceSquared < minDistanceSquared) {
                        minDistanceSquared = currentDistanceSquared;
                        bestFoodVector[0] = dx;
                        bestFoodVector[1] = dy;
                    }
                }
            }
        }
        return bestFoodVector;
    }
    private boolean shouldEat() {
        return r.nextDouble() < Config.eatProbability;
    }
    private boolean shouldBreed() {
        return r.nextDouble() < Config.breedProbability;
    }


    private void checkEatingAction(EventManager eventManager, World world){
        if(world.world[i][j].getFoodItems().size()>0){
            //process eating food immediately
            eventManager.publish(new EatingEvent(this, i,j, world),true);
            hasEaten = true;
        }
    }
    private void checkBreedingAction(EventManager eventManager, World world) {
        // wantToMate = true;
        potentialMates = world.checkMateTile(this);
        if(mateWithMe()){
            System.out.println("Creature " + id + " found mate " + mate.getId() + " at " + i + " " + j);
            eventManager.publish(new BreedingEvent(this, mate, world),false);
            mate.resetMates();
            this.resetMates();
        }
    }

    private boolean mateWithMe() {
        //System.out.println("Potential mates for "+id);
        for (Creature c : potentialMates) {
            //System.out.println("Mate "+c.getId());
            if (c!=null && c.hasMate(id)) {
                this.mate = c;
                //this.wantToMate = false;
                return true;
            }
        }
        return false;
    }
    public void resetMates(){
        potentialMates = null;
        mate = null;
    }
    public boolean hasMate(Integer id){
        if (potentialMates == null) return false;
        for (Creature c: potentialMates) {
            if(c.getId()==id) return true;
        }
        return false;
    }

    public boolean wantsToMate(){
        return wantToMate;
    }

    public int getId() {
        return id;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public Genome getGenome(){
        return genome;
    }

    public void updatePosition(int i, int j){
        this.i = i;
        this.j = j;
    }



    public boolean checkHealth(World world) {
        health-=HEALTH_PENALTY;
        if (health < 0) {
            //System.out.println("Creature " + id + " died.");
            world.remove(i,j,id);
            return true;
        }
        else return false;
    }
    public void consume(){
        health++;
    }



    public void evaluateAction(World w) {
        double[] foodVector = getClosestFoodVector(w);
        double currentDistance = Math.sqrt(foodVector[0] * foodVector[0] + foodVector[1] * foodVector[1]);

        if (currentDistance < prevFoodDistance) {
            fitness += 2.0;
        } else {
            fitness -= 2.5;
        }
        prevFoodDistance = currentDistance;

        if (hasEaten) {
            fitness += 25.0;
            timeSinceEaten = 0;
            hasEaten = false;
        } else {
            timeSinceEaten++;
        }

        boolean hitWall = (i == 0 || j == 0 || i == Config.WORLD_SIZE-1 || j == Config.WORLD_SIZE-1);
        if (hitWall) {
            fitness -= 10.0;
        }

        fitness += 0.1;

        if (timeSinceEaten > 20) {
            fitness -= 1;
        }
    }


    private double getPreviousFoodDistance() {
        return prevFoodDistance;
    }


    public int getFitness() {
        return fitness;
    }
}

package io.github.evolutionary_algorithm;

import io.github.neat.Genome;

import java.util.List;
import java.util.Random;

import static io.github.evolutionary_algorithm.Config.*;
import static io.github.evolutionary_algorithm.Config.Phase.AUTO;

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
    String[] actions = { "left", "up", "down", "right"};
    int[][] actionLocation = { {0,-1}, {-1,0}, {1,0}, {0,1}};
    private boolean hasEaten;
    private int fitness;
    private double closestFoodDistance;
    private double prevFoodDistance;

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
        //System.out.println("Individuals decision to move: "+actions[decision]);
        int i = this.i+actionLocation[decision][0];
        int j = this.j+actionLocation[decision][1];

        world.moveCreature(this,i,j);

        //process possible event
        checkEatingAction(eventManager, world);
        if (currentPhase==AUTO && fitness>breedingThreshold){
            checkBreedingAction(eventManager, world);
        }
    }

    private double[] getEnvironmentInput(World world) {
        double[] inputData = new double[inputFeatures];
        int index = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int x = this.i + i;
                int y = this.j + j;

                if (world.isWithinBounds(x,y)) {
                    Tile tile = world.world[x][y];
                    inputData[index++] = tile.hasFood() ? 1 : -1;
                    inputData[index++] = tile.hasCreature(id) ? -0.5 : 0.5;
                } else {
                    // default
                    inputData[index++] = 0;
                    inputData[index++] = 0;
                }
            }
        }

        inputData[index++] =((double)health/(double)INITIAL_HEALTH);
        closestFoodDistance = getClosestFoodDistance(world.getSize(),world);
        inputData[index] = closestFoodDistance/WORLD_SIZE;
        return inputData;
    }

    private double getClosestFoodDistance(int range, World world) {
        prevFoodDistance = closestFoodDistance;
        closestFoodDistance = Double.MAX_VALUE;

        int searchRadius = range;

        for (int x = Math.max(0, this.i - searchRadius); x < Math.min(world.getSize(), this.i + searchRadius + 1); x++) {
            for (int y = Math.max(0, this.j - searchRadius); y < Math.min(world.getSize(), this.j + searchRadius + 1); y++) {
                Tile tile = world.world[x][y];

                if (tile.hasFood()) {
                    double distance = Math.abs(this.i - x) + Math.abs(this.j - y);
                    closestFoodDistance = Math.min(closestFoodDistance, distance);
                }
            }
        }

        return (closestFoodDistance == Double.MAX_VALUE) ? -1 : closestFoodDistance;
    }


//    private double[] getEnvironmentInput(World world){
//        double foodDistance = world.getFoodDistance();
//        double creatureDistance = world.getCreatureDistance();
//        double energy = getHealth();
//
//        return new double[]{foodDistance,creatureDistance,energy};
//    }

    public double[] getRndInput() {
        Random random = new Random();
        double[] input = new double[inputFeatures];
        for (int i = 0; i < inputFeatures; i++) {
            input[i] = random.nextDouble();
        }
        return input;
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
            System.out.println("Creature " + id + " died.");
            world.remove(i,j,id);
            return true;
        }
        else return false;
    }
    public void consume(){
        health++;
    }

    public void evaluateAction(World w) {

        if (i == 0 || j == 0 || i == Config.WORLD_SIZE - 1 || j == Config.WORLD_SIZE - 1) {
            this.fitness -= 15;
        }


        if (hasEaten) {
            this.fitness += 10;
        }
        double currentDistance = getClosestFoodDistance(w.getSize(),w);
        if (currentDistance < prevFoodDistance) {
            fitness += 0.5;
        } else if (currentDistance > prevFoodDistance) {
            fitness -= 0.3;
        }
        hasEaten = false;
    }

    private double getPreviousFoodDistance() {
        return prevFoodDistance;
    }


    public int getFitness() {
        return fitness;
    }
}

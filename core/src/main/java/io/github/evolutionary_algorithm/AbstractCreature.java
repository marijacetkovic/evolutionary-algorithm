package io.github.evolutionary_algorithm;

import io.github.neat.Genome;

import java.util.List;
import java.util.Random;

import static io.github.evolutionary_algorithm.Config.*;
import static io.github.evolutionary_algorithm.Config.Phase.AUTO;
import static io.github.neat.Config.numInputs;

public abstract class AbstractCreature implements ICreature {
    protected int id;
    //position within the world
    protected int i;
    protected int j;
    protected static Random r = new Random();
    protected boolean wantToMate;
    protected boolean eatsFood;
    public List<AbstractCreature> potentialMates;
    protected int calories;
    protected AbstractCreature mate;

    protected final int foodType;
    protected int health;
    protected Genome genome;
    protected String[] actions = { "up", "left", "down", "right", "none", "eat"};
    protected int[][] actionOffset = { {-1,0}, {0,-1}, {1,0}, {0,1}, {0,0}};
    protected boolean hasEaten;
    protected double fitness;
    protected double closestFoodDistance;
    protected double prevFoodDistance;
    protected int wallPenaltyCnt;
    protected int timeSinceEaten;
    public enum DietType {
        HERBIVORE,
        CARNIVORE,
        OMNIVORE
    }

    protected DietType dietType;

    public AbstractCreature(int id, int i, int j, int foodType, Genome genome){
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
        //System.out.println("Spawned a creature at positions " +i+","+j);
    }

    @Override
    public int getFoodType() {
        return foodType;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public Genome getGenome(){
        return genome;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getI() {
        return i;
    }

    @Override
    public int getJ() {
        return j;
    }

    @Override
    public double getFitness() {
        return fitness;
    }


    public List<AbstractCreature> getPotentialMates() {
        return potentialMates;
    }


    public boolean wantsToMate(){
        return wantToMate;
    }


    public void resetMates(){
        potentialMates = null;
        mate = null;
    }

    public boolean hasMate(Integer id){
        if (potentialMates == null) return false;
        for (AbstractCreature c: potentialMates) {
            if(c.getId()==id) return true;
        }
        return false;
    }

    @Override
    public void updatePosition(int i, int j){
        this.i = i;
        this.j = j;
    }

    @Override
    public boolean checkHealth(World world) {
        health-=HEALTH_PENALTY;
        if (health < 0) {
            //System.out.println("Creature " + id + " died.");
            world.remove(i,j,id);
            return true;
        }
        else return false;
    }

    @Override
    public void takeAction(EventManager eventManager, World world) {
        //inject genome here
        double[] input = this.getEnvironmentInput(world);
        //double[] input = getRndInput();
        int decision = genome.calcPropagation(input);
        //System.out.println("Individuals decision to move: "+actions[decision]);

        performInWorld(eventManager,world,decision);
    }

    //dk if euclidean or manhattan applies better here?
    protected double[] getClosestFoodVector(World world) {
        double[] vector = new double[]{0.0, 0.0};
        double closestDistanceSq = Double.POSITIVE_INFINITY;

        for (int x = 0; x < world.getSize(); x++) {
            for (int y = 0; y < world.getSize(); y++) {
                Tile tile = world.world[x][y];
                if (!tile.hasFood()) continue;

                double dx = x - this.i;
                double dy = y - this.j;
                double distanceSq = dx * dx + dy * dy;
                //update if better found
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    vector[0] = dx;
                    vector[1] = dy;
                }
            }
        }

        /*// normalize for far away food inputs
        double magnitude = Math.sqrt(closestDistanceSq);
        if (magnitude != 0.0) {
            vector[0] /= magnitude;
            vector[1] /= magnitude;
        } else {
            vector[0] = 0.0;
            vector[1] = 0.0; // already on food
        }*/

        return vector;
    }

    protected void performInWorld(EventManager eventManager, World world, int decision) {
        if (decision == ACTION_EAT){
            checkEatingAction(eventManager, world); // This will be abstract
        }
        else{
            int i = this.i+actionOffset[decision][0];
            int j = this.j+actionOffset[decision][1];

            world.moveCreature(this,i,j);
        }
        //add one more output node for this next
        if (currentPhase==AUTO && fitness>breedingThreshold){
            checkBreedingAction(eventManager, world); // This will be abstract
        }
    }

    abstract double[] getEnvironmentInput(World world);
    abstract void checkEatingAction(EventManager eventManager, World world);
    abstract void checkBreedingAction(EventManager eventManager, World world);
    abstract boolean mateWithMe();
    public abstract void consume(Food f);
    public abstract void evaluateAction(World w);
}

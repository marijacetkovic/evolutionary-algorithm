package io.github.evolutionary_algorithm;

import io.github.evolutionary_algorithm.events.DeathEvent;
import io.github.evolutionary_algorithm.events.EventManager;
import io.github.neat.Genome;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import static io.github.evolutionary_algorithm.AbstractCreature.DietType.*;
import static io.github.evolutionary_algorithm.Config.*;
import static io.github.evolutionary_algorithm.Config.Phase.AUTO;

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
    protected String[] actions = { "up", "left", "down", "right", "none", "eat", "attack"};
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
    public double getFitness() { return fitness; }
    public DietType getDietType() { return dietType; }

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
    public abstract void chooseAction(EventManager eventManager, World world);
    public abstract void performAction(EventManager eventManager, World world);
    public abstract void checkHealth(EventManager eventManager, World world);

        //finds closest edible food source
    protected double[] getClosestFoodVector(World world) {
        double[] vector = new double[]{0.0, 0.0};
        boolean[][] visited = new boolean[world.getSize()][world.getSize()];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{i, j, 0});
        visited[i][j] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1], depth = current[2];

            //control for larger worlds
            if (depth > VISION_RANGE)
                break;
            Tile tile = world.world[x][y];

            //check current tile and return vec
            for (Food f : tile.getFoodItems()) {
                if (canEat(f)) {
                    vector[0] = x - i;
                    vector[1] = y - j;
                    return vector;
                }
            }
            //bfs on neighbors
            int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};
            for (int[] d : directions) {
                int next_x = x + d[0], next_y = y + d[1];
                if (world.isWithinBounds(next_x, next_y)
                    && !visited[next_x][next_y]) {
                    visited[next_x][next_y] = true;
                    //enqueue neihgbors
                    queue.add(new int[]{next_x, next_y, depth + 1});
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
            checkEatingAction(eventManager, world);
        } else if (decision == ACTION_ATTACK) {
            //checkAttackAction(eventManager, world);
        } else{
            int i = this.i+actionOffset[decision][0];
            int j = this.j+actionOffset[decision][1];

            world.moveCreature(this,i,j);
        }
        //add one more output node for this next
        if (currentPhase==AUTO && fitness>breedingThreshold){
            checkBreedingAction(eventManager, world);
        }
    }

    protected double[] getDietInput() {
        double[] input = new double[2];
        switch (this.dietType) {
            case HERBIVORE:
                input[0] = 1.0; input[1] = 0.0;
                break;
            case CARNIVORE:
                input[0] = 0.0; input[1] = 1.0;
                break;
            case OMNIVORE:
                input[0] = 1.0; input[1] = 1.0;
                break;
        }
        return input;
    }
    public boolean canEat(Food food) {
        if (food.getCode() == FOOD_CODE_PLANT) {
            return this.dietType == HERBIVORE || this.dietType == OMNIVORE;
        } else if (food.getCode() == FOOD_CODE_MEAT) {
            return this.dietType == CARNIVORE || this.dietType == OMNIVORE;
        }
        return false;
    }
    public boolean isDead() {
        return this.health == 0;
    }

    public void setHealth(int i) {
        this.health = 0;
    }
    public double getAttackDamage() {
        switch (this.dietType) {
            case CARNIVORE:
                return CARNIVORE_ATTACK_DAMAGE;
            case HERBIVORE:
                return HERBIVORE_ATTACK_DAMAGE;
            default:
                return 0.0;
        }
    }
    public double getAttackCost() {
        switch (this.dietType) {
            case CARNIVORE:
                return CARNIVORE_ATTACK_COST;
            case HERBIVORE:
                return HERBIVORE_ATTACK_COST;
            default:
                return 0.0;
        }
    }

    abstract double[] getEnvironmentInput(World world);
    abstract void checkEatingAction(EventManager eventManager, World world);
    abstract void checkBreedingAction(EventManager eventManager, World world);
    abstract void checkAttackAction(EventManager eventManager, World world, Creature target);

    abstract boolean mateWithMe();
    public abstract void consume(Food f);
    public abstract void evaluateAction(World w);
}

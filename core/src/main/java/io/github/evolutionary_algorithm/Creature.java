package io.github.evolutionary_algorithm;

import io.github.neat.Genome;

import java.util.List;
import java.util.Random;

import static io.github.evolutionary_algorithm.Config.inputFeatures;

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
    String[] actions = { "up", "down", "left", "right", "stay"};
    int[][] actionLocation = { {-1,0}, {1,0}, {0,-1} , {0,1}, {0,0}};

    public Creature(int id, int i, int j, int foodType){
        this.id = id;
        this.i = i;
        this.j = j;
        this.foodType = foodType;
        this.wantToMate = false;
        this.mate = null;
        this.eatsFood = true;
        this.genome = new Genome();
        this.health = Config.INITIAL_HEALTH;
        System.out.println("Spawned a creature at positions " +i+","+j);
    }
    public Creature(int id, int i, int j, Genome genome){
        this.id = id;
        this.i = i;
        this.j = j;
        this.foodType = -1;
        this.mate = null;
        this.genome = genome;
        this.health = Config.INITIAL_HEALTH;
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
        int i = this.i+actionLocation[decision][0];
        int j = this.j+actionLocation[decision][1];

        world.moveCreature(this,i,j);

        //process possible event
        if (Config.eat) {
            checkEatingAction(eventManager, world);
        }
        if (Config.breed) {
            checkBreedingAction(eventManager, world);
        }
    }

    private double[] getEnvironmentInput(World world) {
        double[] inputData = new double[inputFeatures];
        int index = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int x = this.i + i;
                int y = this.j + j;

                if (world.isWithinBounds(x,y)) {
                    Tile tile = world.world[x][y];
                    inputData[index++] = tile.hasFood() ? 1 : 0;
                    inputData[index++] = tile.hasCreature(id) ? 1 : 0;
                } else {
                    // default
                    inputData[index++] = 0;
                    inputData[index++] = 0;
                }
            }
        }
        inputData[index] = health;

        return inputData;

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
        return new double[]{random.nextDouble(100), random.nextDouble(100), random.nextDouble(100)};
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
        health--;
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

}

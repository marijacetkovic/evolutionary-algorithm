import java.util.*;

public class Creature {
    private Gene gene;
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
    private final double eatProbability = 0.7;
    private final double breedProbability = 0.5;
    private final int foodType;
    private int health;

    public Creature(int id, int i, int j, int foodType){
        this.id = id;
        this.i = i;
        this.j = j;
        this.foodType = foodType;
        this.wantToMate = false;
        this.mate = null;
        this.eatsFood = true;
        this.gene = new Gene();
        this.health = 10;
        System.out.println("Spawned a creature at positions " +i+","+j);
    }

    public int getFoodType() {
        return foodType;
    }

    public int getHealth() {
        return health;
    }

    public void takeAction(EventManager eventManager, World world) {
        if (shouldEat()) {
            eatingAction(eventManager, world);
        }
        if (shouldBreed()) {
            breedingAction(eventManager, world);
        }
    }
    private boolean shouldEat() {
        return r.nextDouble() < eatProbability;
    }
    private boolean shouldBreed() {
        return r.nextDouble() < breedProbability;
    }


    private void breedingAction(EventManager eventManager, World world) {
        wantToMate = true;
        potentialMates = world.checkMateTile(this);
        if (!mateWithMe()&&wantToMate) {
            this.eatsFood = true;
        }
        else{
            System.out.println("Creature " + id + " found mate " + mate.getId() + " at " + i + " " + j);
            eventManager.publish(new BreedingEvent(this, mate, world),false);
            mate.resetMates();
            this.resetMates();
        }
    }

    private void eatingAction(EventManager eventManager, World world){
        if(world.world[i][j].contains(foodType)){
            //process eating food immediately
            eventManager.publish(new EatingEvent(this, i,j, world),true);
        }
    }


    private boolean mateWithMe() {
        //System.out.println("Potential mates for "+id);
        for (Creature c : potentialMates) {
            //System.out.println("Mate "+c.getId());
            if (c!=null && c.hasMate(id)) {
                this.mate = c;
                this.wantToMate = false;
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

    public Gene getGene(){
        return gene;
    }

    public void updatePosition(int i, int j){
        this.i = i;
        this.j = j;
    }

    public void setGene(int value) {
        this.gene.setGene(value);
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

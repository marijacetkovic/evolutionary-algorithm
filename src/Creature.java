import java.util.*;

public class Creature {
    private Gene gene;
    private int id;
    //position within the world
    private int i;
    private int j;
    private Random r;
    private boolean wantToMate;
    private boolean eatsFood;
    public List<Creature> potentialMates;
    private int calories;
    private Creature mate;
    private final double eatProbability = 0.7;
    private final double breedProbability = 0.5;

    public Creature(int id, int i, int j){
        this.id = id;
        this.i = i;
        this.j = j;
        this.r = new Random();
        this.wantToMate = false;
        this.mate = null;
        this.eatsFood = true;
        this.gene = new Gene();
        System.out.println("Spawned a creature at positions " +i+","+j);
    }

    public void takeAction(EventManager eventManager, World world) {
        //int[] foodLocation = world.checkAvailableFood(this);
        if (r.nextDouble() < eatProbability) {
            eatingAction(eventManager, world);
        }
        if (r.nextDouble() < breedProbability) {
            breedingAction(eventManager, world);
        }
    }

    private void breedingAction(EventManager eventManager, World world) {
        wantToMate = true;
        potentialMates = world.checkMateTile(this);
        if (!mateWithMe()&&wantToMate) {
            this.eatsFood = true;
        }
        else{
            System.out.println("Creature " + id + " found mate " + mate.getId() + " at " + i + " " + j);
            eventManager.publish(new BreedingEvent(this, mate, world));
            mate.resetMates();
            potentialMates = null;
            mate = null;
        }
    }

    private void eatingAction(EventManager eventManager, World world){
        if(world.world[i][j].contains(Config.FOOD_CODE) && eatsFood){
            eatsFood = false;
            //this should be fixed in future - mark food as unavailable until the event is processed or sth
            world.world[i][j].remove((Integer) Config.FOOD_CODE);
            eventManager.publish(new EatingEvent(this, i,j, world));
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

    private void eat(World world){

    }

    private void move(World world){
    }

    public void updatePosition(int i, int j){
        this.i = i;
        this.j = j;
    }

    public void setGene(int value) {
        this.gene.setGene(value);
    }
}

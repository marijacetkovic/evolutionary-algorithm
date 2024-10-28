import java.util.*;

public class Creature {
    private int id;
    //position within the world
    private int i;
    private int j;
    private Random r;
    private boolean wantToMate;
    private boolean eatsFood;
    public Creature potentialMate;
    private int calories;
    public Creature(int id, int i, int j){
        this.id = id;
        this.i = i;
        this.j = j;
        this.r = new Random();
        wantToMate = false;
        potentialMate = null;
        eatsFood = true;
        System.out.println("Spawned a creature at positions " +i+","+j);
    }

    public void takeAction(EventManager eventManager, World world) {
        int[] foodLocation = world.checkAvailableFood(this);
//        if(foodLocation!=null&&eatsFood){
//            eatsFood = false;
//            eventManager.enqueue(new EatingEvent(this, foodLocation));
//        }
        wantToMate = true;
        Creature foundMate = world.checkEligibleMate(this).get(0);
        this.potentialMate = foundMate;
        if(foundMate!=null && foundMate.wantsToMate() && foundMate.potentialMate!=null && foundMate.potentialMate.getId()==id){
            System.out.println("Creature "+id+" found mate "+foundMate.getId()+" at"+i+" "+j);
            eventManager.enqueue(new BreedingEvent(this,potentialMate));
            potentialMate = null;
            wantToMate = false;
        }
        else{
            world.checkAvailableMove(this);
            eatsFood = true;
            System.out.println("Creature "+id+" moved to "+i+" "+j);
        }
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

    private void eat(World world){

    }

    private void move(World world){
    }

    public void updatePosition(int i, int j){
        this.i = i;
        this.j = j;
    }
}

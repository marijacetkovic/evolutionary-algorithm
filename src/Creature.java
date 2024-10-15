import java.util.*;

public class Creature {
    private int id;
    //position within the world
    private int i;
    private int j;
    private Random r;
    public Creature(int id, int i, int j){
        this.id = id;
        this.i = i;
        this.j = j;
        this.r = new Random();
        System.out.println("Spawned a creature at positions " +i+","+j);
    }

    public void takeAction(EventManager eventManager, World world) {
        boolean ate = world.checkAdjacentTile(this,Config.FOOD_CODE);
        if(ate){
            System.out.println("Ate at"+i+" "+j);
        }
        else{
            world.checkAdjacentTile(this,Config.DEFAULT_CODE);
            System.out.println("Moved to "+i+" "+j);
        }


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

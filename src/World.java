import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class World {
    private int[][] world;
    private ArrayList<Creature> population;
    private int size;
    private Random r = new Random();
    private final EventManager eventManager;
    public World(int n){
        this.size = n;
        this.world = new int[n][n];
        this.population = new ArrayList<>();
        this.eventManager = new EventManager();
        init();
        spawnCreatures();
    }

    private void init(){
        for (int i = 0; i < size; i++) {
            Arrays.fill(world[i],-1); // empty tile
        }
    }

    private void spawnCreatures(){
        for (int k = 0; k < 3; k++) {
            int i = r.nextInt(size);
            int j = r.nextInt(size);
            world[i][j]=k;
            population.add(new Creature(k,i,j));
        }
    }

    public void behave(){
        for (Creature c: population) {
            c.takeAction(eventManager,world);
        }
        printMatrix(world);
    }
    public void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------");
    }

}

import java.util.*;
import java.util.function.Predicate;

public class World {
    private final int NUM_CREATURES = 10;
    private final int MAX_CREATURES = 20;
    private final int NUM_FOOD = 0;
    private int[][] world;
    private ArrayList<Creature> population;
    private int size;
    private Random r = new Random();
    private List<int[]> directions;
    private final EventManager eventManager;
    private int lastId;
    public World(int n){
        this.size = n;
        this.world = new int[n][n];
        this.population = new ArrayList<>();
        this.eventManager = new EventManager(this);
        this.lastId = 0;
        this.directions = Arrays.asList(
                new int[]{1, 0},
                new int[]{0, 1},
                new int[]{0, -1},
                new int[]{-1, 0}
        );

        init();
        spawnCreatures();
        spawnFood();
    }

    private void init(){
        for (int i = 0; i < size; i++) {
            Arrays.fill(world[i],-1); // empty tile
        }
    }

    private void spawnCreatures(){
        for (int k = 0; k < NUM_CREATURES; k++) {
            spawnCreature();
        }
    }

    public Creature spawnCreature(){
        int i,j;
        if(lastId==MAX_CREATURES) return null;
        do{
            i = r.nextInt(size);
            j = r.nextInt(size);}
        while (world[i][j]!=-1);
        Creature c = new Creature(lastId,i,j);
        world[i][j] = lastId;
        //population.add(new Creature(lastId,i,j));
        population.add(c);
        lastId++;
        return c;
    }

    private void spawnFood(){
        for (int k = 0; k < NUM_FOOD; k++) {
            int i,j;
            do{
                i = r.nextInt(size);
                j = r.nextInt(size);}
            while (world[i][j]!=-1);
            world[i][j]=-2;
        }
    }
    public void behave(){
        for (Creature c: population) {
            c.takeAction(eventManager,this);
        }
        eventManager.process();
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

    private void moveCreature(Creature creature, int i, int j) {
        world[i][j] = creature.getId();
        world[creature.getI()][creature.getJ()] = -1;
        creature.updatePosition(i,j);
    }
    public Creature checkEligibleMate(Creature c){
        int id = findEligibleMate(c, x->x>=0);
        return findById(id);
    }
    public boolean checkAvailableFood(Creature c){
        return checkAdjacentTile(c, x->x==Config.FOOD_CODE,true);
    }
    public boolean checkAvailableMove(Creature c){
        return checkAdjacentTile(c, x->x==Config.DEFAULT_CODE,true);
    }
    private int findEligibleMate(Creature c, Predicate<Integer> condition){
        List<int[]> directions =  Arrays.asList(
                new int[]{1, 0},
                new int[]{0, 1},
                new int[]{0, -1},
                new int[]{-1, 0}
        );
        int i = c.getI();
        int j = c.getJ();
        Collections.shuffle(directions, r);
        for (int[] dir : directions) {
            int newRow = i + dir[0];
            int newCol = j + dir[1];
            if (isWithinBounds(newRow, newCol) && condition.test(world[newRow][newCol])) {
                return world[newRow][newCol];
            }
        }
        return -1;
    }
    private Creature findById(int id){
        for (Creature c:population) {
            if (c.getId()==id) return c;
        }
        return null;
    }
    private boolean checkAdjacentTile(Creature c, Predicate<Integer> condition, boolean move){
        List<int[]> directions =  Arrays.asList(
                new int[]{1, 0},
                new int[]{0, 1},
                new int[]{0, -1},
                new int[]{-1, 0}
        );
        int i = c.getI();
        int j = c.getJ();
        Collections.shuffle(directions, r);
        for (int[] dir : directions) {
            int newRow = i + dir[0];
            int newCol = j + dir[1];
            if (isWithinBounds(newRow, newCol) && condition.test(world[newRow][newCol])) {
                if(move) moveCreature(c,newRow,newCol);
                return true;
            }
        }
        return false;
    }
    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < world.length && col >= 0 && col < world[0].length;
    }
    public boolean isAvailableTile(int row, int col) {
        return row >= 0 && row < world.length && col >= 0 && col < world[0].length && world[row][col] == -1;
    }


}

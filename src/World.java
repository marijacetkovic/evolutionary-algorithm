import java.util.*;
import java.util.function.Predicate;

public class World {
    private final int NUM_CREATURES = 10;
    private final int MAX_CREATURES = 15;
    private final int NUM_FOOD = 10;
    public List<Integer>[][] world;
    private ArrayList<Creature> population;
    private ArrayList<Food> food;
    private int size;
    private Random r = new Random();
    private List<int[]> directions;
    private final EventManager eventManager;
    private int lastId;
    public World(int n){
        this.size = n;
        this.world = new ArrayList[n][n];
        this.population = new ArrayList<>();
        this.food = new ArrayList<>();
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
            for (int j = 0; j < world[i].length; j++) {
                world[i][j] = new ArrayList<>();
            }
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
        //do{
            i = r.nextInt(size);
            j = r.nextInt(size);
        //}
        //while (!world[i][j].isEmpty());
        Creature c = new Creature(lastId,i,j);
        world[i][j].add(lastId);
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
            while (world[i][j].contains(-2));
            food.add(new Food(i,j,100));
            world[i][j].add(-2);
        }
    }
    public void behave(){
        printMatrix(world);

        for (Creature c: population) {
            c.takeAction(eventManager,this);
        }
        for (Creature c: population){
            checkAvailableMove(c);
        }
        eventManager.process();
    }
    public void printMatrix(List<Integer>[][] matrix) {
        System.out.println("-----------------------------------");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j].isEmpty()) {
                    System.out.print("    | ");
                } else {
                    System.out.print(matrix[i][j] + " | ");
                }
            }
            System.out.println();
            System.out.println("---------------------------------");
        }
    }

    public void moveCreature(Creature creature, int i, int j) {
        world[i][j].add(creature.getId());
        world[creature.getI()][creature.getJ()].remove((Integer) creature.getId());
        System.out.println("Creature " + creature.getId() + " moved to " + i + " " + j);
        creature.updatePosition(i,j);
    }
    public List<Creature> checkEligibleMate(Creature c){
        List<Integer> ids = findEligibleMates(c, x->x>=0);
        return findCreatureById(ids);
    }
    public int[] checkAvailableFood(Creature c){
        return checkAdjacentTile(c, x->x==Config.FOOD_CODE,false);
    }
    public int[] checkAvailableMove(Creature c){
        return checkAdjacentTile(c, x->x==Config.DEFAULT_CODE,true);
    }
    public List<Creature> checkMateTile(Creature c){
        List<Integer> ids = world[c.getI()][c.getJ()].stream().filter(x->x>=0&&x!=c.getId()).toList();
        return findCreatureById(ids);

    }
    private List<Integer> findEligibleMates(Creature c, Predicate<Integer> condition){
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
            if (isWithinBounds(newRow, newCol) && condition.test(world[newRow][newCol].get(0))) {
                return world[newRow][newCol];
            }
        }
        return null;
    }
    private List<Creature> findCreatureById(List<Integer> id){
        List<Creature> res = new ArrayList<>();
        if(id==null) {return res;}
        for (Creature c:population) {
            if (id.contains(c.getId())) res.add(c);
        }
        return res;
    }
    private int[] checkAdjacentTile(Creature c, Predicate<Integer> condition, boolean move){
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
            if (isWithinBounds(newRow, newCol)) {
                if(move) moveCreature(c,newRow,newCol);
                return new int[]{newRow,newCol};
            }
        }
        return null;
    }
    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }
    public boolean isAvailableTile(int row, int col) {
        return row >= 0 && row < world.length && col >= 0 && col < world[0].length && world[row][col].isEmpty();
    }


}

package io.github.evolutionary_algorithm;

import java.util.*;
import java.util.function.Predicate;

public class World {
    private final int NUM_CREATURES = 10;
    private final int MAX_CREATURES = 20;
    private final int NUM_FOOD = 10;
    public List<Integer>[][] world;
    private ArrayList<Creature> population;
    private ArrayList<Food> food;
    private int size;
    private Random r = new Random();
    private List<int[]> directions;
    private final EventManager eventManager;
    private int lastId;
    private static final Map<FoodType, Integer> foodCodes = Map.of(
            FoodType.MEAT, -1,
            FoodType.PLANT, -2
    );
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
        spawnFood(NUM_FOOD);
        printMatrix(world);
    }
    public int getSize(){
        return size;
    }

    public ArrayList<Creature> getPopulation(){
        return population;
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
        do{
            i = r.nextInt(size);
            j = r.nextInt(size);
        }
        while (!world[i][j].isEmpty());
        Creature c = new Creature(lastId,i,j,getRandomFoodCode());
        world[i][j].add(lastId);
        //population.add(new Creature(lastId,i,j));
        population.add(c);
        lastId++;
        return c;
    }

    private int getRandomFoodCode() {
        FoodType[] food = FoodType.values();
        FoodType rnd = food[r.nextInt(food.length)];
        return foodCodes.get(rnd);
    }

    private int calcNutrition(){
        return 100;
    }

    private void spawnFood(int n){
        for (int k = 0; k < n; k++) {
            int i,j;
            do{
                i = r.nextInt(size);
                j = r.nextInt(size);}
            while (world[i][j].contains(-2));
            int x = getRandomFoodCode();
            food.add(new Food(i,j,calcNutrition(),x));
            world[i][j].add(x);
        }
    }

    private void checkFoodQuantity(){
        if (food.size()<NUM_FOOD/3) {
            System.out.println("low on food");
            spawnFood(NUM_FOOD);
        }
    }

    public boolean behave() {
        printMatrix(world);
        population.forEach(c -> c.takeAction(eventManager, this));
        removeDead();
        eventManager.process();
        checkFoodQuantity();
        return !population.isEmpty();
    }

    private void removeDead() {
        List<Creature> toRemove = new ArrayList<>();
        for (Creature c : population) {
            //move everyone at the end of a round by one place
            checkAvailableMove(c);
            //check everyones health
            if (c.checkHealth(this)) toRemove.add(c);
        }
        population.removeAll(toRemove);
        if (population.isEmpty()) System.out.println("Whole generation died.");
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
        remove(creature.getI(),creature.getJ(),creature.getId());
        System.out.println("Creature " + creature.getId() + " H:"+creature.getHealth()+" moved to " + i + "," + j);
        creature.updatePosition(i,j);
    }

    public void remove(int i, int j, int type){
        world[i][j].remove((Integer) type);
    }
    public void cutFood(int code){
        for (Food f:food) if (f.getCode()==code) {food.remove(f); return;}
    }

    public List<Creature> checkEligibleMate(Creature c){
        List<Integer> ids = findEligibleMates(c, x->x>=0);
        return findCreatureById(ids);
    }
    public int[] checkAvailableMove(Creature c){
        return checkAdjacentTile(c);
    }
    public List<Creature> checkMateTile(Creature c){
        List<Integer> ids = world[c.getI()][c.getJ()].stream().filter(x->x>=0&&x!=c.getId()).toList();
        return findCreatureById(ids);

    }
    private List<Integer> findEligibleMates(Creature c, Predicate<Integer> condition){
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
    private int[] checkAdjacentTile(Creature c){
        int i = c.getI();
        int j = c.getJ();
        Collections.shuffle(directions, r);
        for (int[] dir : directions) {
            int newRow = i + dir[0];
            int newCol = j + dir[1];
            if (isWithinBounds(newRow, newCol)) {
                moveCreature(c,newRow,newCol);
                return new int[]{newRow,newCol};
            }
        }
        return null;
    }
    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }
//    public boolean isAvailableTile(int row, int col) {
//        return row >= 0 && row < world.length && col >= 0 && col < world[0].length && world[row][col].isEmpty();
//    }


}

package io.github.evolutionary_algorithm;

import io.github.neat.Genome;

import java.util.*;

public class World {
    public Tile[][] world;
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

    public World(int n) {
        this.size = n;
        this.world = new Tile[n][n];    // Tile[][] matrix
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
        spawnFood(Config.NUM_FOOD);
        //printMatrix();
    }

    public int getSize() {
        return size;
    }

    public ArrayList<Creature> getPopulation() {
        return population;
    }

    private void init() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                world[i][j] = new Tile();
            }
        }
    }

    public ArrayList<Food> getFood() {
        return food;
    }

    private void spawnCreatures() {
        for (int k = 0; k < Config.NUM_CREATURES; k++) {
            spawnCreature();
        }
    }

    public Creature spawnCreature() {
        int i, j;
        if (lastId == Config.MAX_CREATURES) return null;
        do {
            i = r.nextInt(size);
            j = r.nextInt(size);
        } while (!world[i][j].getCreatures().isEmpty());

        Creature c = new Creature(lastId, i, j, getRandomFoodCode());
        world[i][j].addCreature(lastId);
        population.add(c);
        lastId++;
        return c;
    }
    public Creature spawnChild(Genome g){
        int i, j;
        if (lastId == Config.MAX_CREATURES) return null;
        do {
            i = r.nextInt(size);
            j = r.nextInt(size);
        } while (!world[i][j].getCreatures().isEmpty());

        Creature c = new Creature(lastId, i, j, g);
        world[i][j].addCreature(lastId);
        population.add(c);
        lastId++;
        return c;
    }

    private int getRandomFoodCode() {
//        FoodType[] food = FoodType.values();
//        FoodType rnd = food[r.nextInt(food.length)];
//        return foodCodes.get(rnd);
        return -1;
    }

    private void spawnFood(int n) {
        for (int k = 0; k < n; k++) {
            int i, j;
            do {
                i = r.nextInt(size);
                j = r.nextInt(size);
            } while (world[i][j].getFoodItems().contains(-2));
            int foodCode = getRandomFoodCode();
            food.add(new Food(i, j, 100, foodCode));
            world[i][j].addFood(foodCode);
        }
    }

    public boolean behave() {
        //printMatrix();
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
            //should change this for genome
            // moveCreatureRandom(c);
            //check everyones health
            if (c.checkHealth(this)) toRemove.add(c);
        }
        population.removeAll(toRemove);
        if (population.isEmpty()) System.out.println("Whole generation died.");
    }
    private void checkFoodQuantity(){
        System.out.println("food size"+food.size());
        if (food.size()<Config.NUM_FOOD/2) {
            System.out.println("low on food");
            spawnFood(Config.NUM_FOOD);
        }
    }
    public void printMatrix() {
        System.out.println("-----------------------------------");
        for (int i = 0; i < world.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < world[i].length; j++) {
                Tile tile = world[i][j];
                if (tile.getCreatures().isEmpty()) {
                    System.out.print("    | ");
                } else {
                    System.out.print(tile.getCreatures() + " | ");
                }
            }
            System.out.println();
            System.out.println("---------------------------------");
        }
    }

    public void moveCreature(Creature creature, int i, int j) {
        if (!isWithinBounds(i, j)) {
            System.out.println("Out of bounds, couldn't move");
            return;
        }
        world[i][j].addCreature(creature.getId());
        remove(creature.getI(), creature.getJ(), creature.getId());
        creature.updatePosition(i, j);
        System.out.println("Creature " + creature.getId() + " H:"+creature.getHealth()+" moved to " + i + "," + j);

    }


    public void remove(int i, int j, int creatureId) {
        world[i][j].removeCreature(creatureId);
    }
    public void removeFood(int i, int j, int foodCode) {
        world[i][j].removeFood(foodCode);
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }
    public void cutFood(int i, int j){
        for (Food f:food) if (f.getI()==i && f.getJ()==j) {
            boolean flag = food.remove(f);
            System.out.println("Removed"+flag);
            System.out.println("after removing"+food.size());
            return;}
    }

//    public List<Creature> checkEligibleMate(Creature c){
//        List<Integer> ids = findEligibleMates(c, x->x>=0);
//        return findCreatureById(ids);
//    }
//    public int[] moveCreatureRandom(Creature c){
//        return checkAdjacentTile(c);
//    }
        public List<Creature> checkMateTile(Creature c){
            List<Integer> ids = world[c.getI()][c.getJ()].getOtherCreatures(c.getId());
            return findCreaturesById(ids);

        }
//    private List<Integer> findEligibleMates(Creature c, Predicate<Integer> condition){
//        int i = c.getI();
//        int j = c.getJ();
//        Collections.shuffle(directions, r);
//        for (int[] dir : directions) {
//            int newRow = i + dir[0];
//            int newCol = j + dir[1];
//            if (isWithinBounds(newRow, newCol) && condition.test(world[newRow][newCol].get(0))) {
//                return world[newRow][newCol];
//            }
//        }
//        return null;
//    }
        public List<Creature> findCreaturesById(List<Integer> id){
            List<Creature> res = new ArrayList<>();
            if(id==null) {return res;}
            for (Creature c:population) {
                if (id.contains(c.getId())) res.add(c);
            }
            return res;
        }
        public Creature findCreatureById(Integer id){
           return population.stream().filter(c->c.getId()==id).findFirst().orElse(null);
        }
//    private int[] checkAdjacentTile(Creature c){
//        int i = c.getI();
//        int j = c.getJ();
//        Collections.shuffle(directions, r);
//        for (int[] dir : directions) {
//            int newRow = i + dir[0];
//            int newCol = j + dir[1];
//            if (isWithinBounds(newRow, newCol)) {
//                moveCreature(c,newRow,newCol);
//                return new int[]{newRow,newCol};
//            }
//        }
//        return null;
//    }
//    public boolean isWithinBounds(int row, int col) {
//        return row >= 0 && row < size && col >= 0 && col < size;
//    }
//    public double getFoodDistance() {
//    }

//    public double getCreatureDistance() {
//    }
//
//    private double getNearest(int id, int i, int j){
//
//    }
//
//    private double bfs(int id, int i, int j, Queue<int[]> visited){
//        int distance = 0;
//
//    }

//    public boolean isAvailableTile(int row, int col) {
//        return row >= 0 && row < world.length && col >= 0 && col < world[0].length && world[row][col].isEmpty();
//
}

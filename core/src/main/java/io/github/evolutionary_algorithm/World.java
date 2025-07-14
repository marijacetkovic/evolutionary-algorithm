package io.github.evolutionary_algorithm;

import io.github.neat.Genome;

import java.util.*;

import static io.github.evolutionary_algorithm.Config.*;

public class World {
    public Tile[][] world;
    private ArrayList<AbstractCreature> population;
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
    private AbstractCreature bestFitnessCreature;
    private double bestFitness;
    private ArrayList<AbstractCreature> prevPopulation;
    private int generation;

    public World(int n) {
        this.size = n;
        this.world = new Tile[n][n];    // Tile[][] matrix
        this.population = new ArrayList<>();
        this.prevPopulation = new ArrayList<>();
        this.food = new ArrayList<>();
        this.eventManager = new EventManager(this);
        this.lastId = 0;
        this.generation = 0;
        this.directions = Arrays.asList(
            new int[]{1, 0},
            new int[]{0, 1},
            new int[]{0, -1},
            new int[]{-1, 0}
        );
        reset();
        //printMatrix();
    }

    public int getSize() {
        return size;
    }

    public ArrayList<AbstractCreature> getPopulation() {
        return population;
    }
    public ArrayList<AbstractCreature> getPrevPopulation() {
        return prevPopulation;
    }

    public void reset(){
        this.lastId = 0;
        initializeTiles();
        this.population = new ArrayList<>();
        this.prevPopulation = new ArrayList<>();
        //spawnCreatures();
        this.food = new ArrayList<>();
        //spawnFood(Config.NUM_FOOD);
        this.bestFitness = Double.NEGATIVE_INFINITY;
    }

    private void initializeTiles() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                world[i][j] = new Tile();
            }
        }
    }

    public ArrayList<Food> getFood() {
        return food;
    }

    public void spawnCreatures(ArrayList<Genome> genomes) {
        //int i = r.nextInt(CREATURE_LOCATION_BOUND);
        //int j = r.nextInt(CREATURE_LOCATION_BOUND);
        //int i = CREATURE_LOCATION_BOUND;
        for (Genome g: genomes) {
            spawnCreature(g,0,0);
        }
    }

    public Creature spawnCreature(Genome g, int i, int j) {
        if (lastId == Config.MAX_CREATURES) return null;
        //do {
        //    i = r.nextInt(size);
        //    j = r.nextInt(size);
        //} while (!world[i][j].getCreatures().isEmpty());
        i = r.nextInt(CREATURE_LOCATION_BOUND);
        j = r.nextInt(CREATURE_LOCATION_BOUND);
        Creature c = new Creature(lastId, i, j, getRandomFoodCode(), g);
        world[i][j].addCreature(lastId);
        population.add(c);
        lastId++;
        return c;
    }
    public Creature spawnChild(Genome g){
        int i, j;
        if (lastId == Config.MAX_CREATURES) return null;
        i = r.nextInt(size);
        j = r.nextInt(size);

        Creature c = new Creature(lastId, i, j, getRandomFoodCode(),g);
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

    public boolean behave() {
        population.forEach(c -> c.takeAction(eventManager, this));
        population.forEach(c -> c.evaluateAction(this));
        removeDead();
        eventManager.process();
        updateBestFitnessIndividual();
       // updateBreedingThreshold();
        return !population.isEmpty();
    }

    private void updateBreedingThreshold() {
        Config.breedingThreshold = bestFitnessCreature.getFitness() / 2;
    }

    private void removeDead() {
        List<AbstractCreature> toRemove = new ArrayList<>();
        for (AbstractCreature c : population) {
            if (c.checkHealth(this)) toRemove.add(c);
        }
        population.removeAll(toRemove);
        //keep track of prev pop
        prevPopulation.addAll(toRemove);
        if (population.isEmpty()) System.out.println("Whole generation died.");
    }

    private void updateBestFitnessIndividual() {
        AbstractCreature best = null;
        double highestFitness = bestFitness;

        for (AbstractCreature c : population) {
            if (c.getFitness() > highestFitness) {
                highestFitness = c.getFitness();
                best = c;
            }
        }

        if (best != null) {
            bestFitnessCreature = best;
            bestFitness = bestFitnessCreature.getFitness();
            //System.out.println("Updated best fitness creature"+bestFitnessCreature.getId()+" with fitness: " + highestFitness);
        }
    }

    public void moveCreature(AbstractCreature creature, int i, int j) {
        if (!isWithinBounds(i, j)) {
            //System.out.println("Out of bounds, couldn't move");
            return;
        }
        world[i][j].addCreature(creature.getId());
        remove(creature.getI(), creature.getJ(), creature.getId());
        creature.updatePosition(i, j);
        //System.out.println("Creature " + creature.getId() + " H:"+creature.getHealth()+" moved to " + i + "," + j);

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
            //System.out.println("Removed"+flag);
            //System.out.println("after removing"+food.size());
            return;}
    }

//    public List<Creature> checkEligibleMate(Creature c){
//        List<Integer> ids = findEligibleMates(c, x->x>=0);
//        return findCreatureById(ids);
//    }
//    public int[] moveCreatureRandom(Creature c){
//        return checkAdjacentTile(c);
//    }

    //must be changed
        public List<AbstractCreature> checkMateTile(Creature c){
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
        public List<AbstractCreature> findCreaturesById(List<Integer> id){
            List<AbstractCreature> res = new ArrayList<>();
            if(id==null) {return res;}
            for (AbstractCreature c:population) {
                if (id.contains(c.getId())) res.add(c);
            }
            return res;
        }
        public AbstractCreature findCreatureById(Integer id){
           return population.stream().filter(c->c.getId()==id).findFirst().orElse(null);
        }

    public boolean isPopulationAlive() {
        return !population.isEmpty();
    }

    public int increaseGeneration() {
        return ++this.generation;
    }

    public boolean isWall(int x, int y) {
        return x == 0 || x == world.length - 1 || y == 0 || y == world.length - 1;
    }
    public int getPopulationSize(){
        return population.size();
    }

    public void addFood(int i, int j) {
        int foodCode = getRandomFoodCode();
        food.add(new Food(i, j, 100, foodCode));
        world[i][j].addFood(foodCode);
    }
}

package io.github.evolutionary_algorithm;

import io.github.neat.*;
import java.util.*;
import static io.github.evolutionary_algorithm.Config.*;
import static io.github.evolutionary_algorithm.Config.Phase.EVOLUTIONARY;

public class EvolutionManager {
    private static EvolutionManager instance;
    private final FoodSpawnManager foodSpawnManager;
    private final NEATManager neatManager;
    private final MetricsManager metricsManager;
    private World world;

    public int getCurrentGeneration() {
        return currentGeneration;
    }
    private int currentGeneration;

    private Random r;
    private ArrayList<Genome> eliteGenomes;

    private EvolutionManager() {
        this.currentGeneration = 0;
        this.world = new World(WORLD_SIZE);
        this.foodSpawnManager = new FoodSpawnManager(world);
        this.neatManager = new NEATManager(NUM_CREATURES);
        this.metricsManager = MetricsManager.getInstance();
        boolean loadFromSavedState = true;

        if (loadFromSavedState) {
            initSavedGenomes("best_genomes.ser");
        } else {
            //starting simulation
            initFirstGeneration();
        }

        this.eliteGenomes = new ArrayList<>();
    }


    public static EvolutionManager getInstance() {
        if (instance == null) {
            instance = new EvolutionManager();
        }
        return instance;
    }

    public boolean update() {
        if (currentPhase == EVOLUTIONARY) return runEvolutionaryPhase();
        else return runAutoPhase();
    }

    //training phase for agents
    private boolean runEvolutionaryPhase() {
        //fitness is evaluated already (from GUI)
        ArrayList<AbstractCreature> currentCreatures = world.getPrevPopulation();
        ArrayList<Genome> nextGen = neatManager.evolveGeneration(currentCreatures);

        //prepare world for next gen
        world.reset();
        world.spawnCreatures(nextGen);
        foodSpawnManager.spawnFood(NUM_FOOD, currentGeneration);
        neatManager.getSpeciesManager().getSpeciesStatistics();
        metricsManager.log(currentCreatures,neatManager.getSpeciesManager().getSpeciesList());
        metricsManager.printSummary();
        boolean endEvolution = ++currentGeneration >= EVOLUTION_GEN;
        if (endEvolution) {
            transitionToAuto();
        }
        return endEvolution;
    }

    //runs autonomous agent phase until dead
    private boolean runAutoPhase(){
        currentGeneration+=1;
        if (currentGeneration>MAX_GEN){
            return true; //simulation complete
        }
        return false;
    }

    //CHECK THIS
    private void transitionToAuto() {
        currentPhase = Phase.AUTO;
        //??
        world.spawnCreatures(eliteGenomes);
        //<----------------------
        saveBestGenomes();
        metricsManager.exportMetrics();
        System.out.println("Transitioned to autonomous phase.");
    }

    public void initFirstGeneration(){
        world.reset();
        ArrayList<Genome> randomGenomes = neatManager.initFirstGeneration();
        world.spawnCreatures(randomGenomes);
        currentGeneration++;
        foodSpawnManager.spawnFood(NUM_FOOD,currentGeneration);
    }

    public World getWorld() {
        return world;
    }

    public void monitor() {
        foodSpawnManager.checkFoodQuantity(currentGeneration);
    }

    public NEATManager getNeatManager() {
        return neatManager;
    }

    private void initSavedGenomes(String s) {
        ArrayList<Genome> loadedGenomes = GenomeSerializer.loadGenomeList(s);

        if (loadedGenomes.isEmpty()) {
            System.out.println("No genomes found to load.");
            initFirstGeneration();
            return;
        }

        ArrayList<Genome> initialPopulation = neatManager.createFromSaved(loadedGenomes, NUM_CREATURES);

        System.out.println("Species number"+neatManager.getSpeciesManager().getSpeciesList().size());

        world.reset();
        world.spawnCreatures(initialPopulation);
        currentGeneration = 1;
        foodSpawnManager.spawnFood(NUM_FOOD, currentGeneration);
        neatManager.getSpeciesManager().getSpeciesStatistics();

        System.out.println("Loaded " + loadedGenomes.size() + " genomes.");
    }

    private void saveBestGenomes() {
        ArrayList<Genome> bestGenomes = neatManager.getBestIndividuals((int) (NUM_CREATURES * SAVE_RATE));
        GenomeSerializer.saveGenomeList(bestGenomes, "best_genomes.ser");
        INManager.saveToFile("inmanager_state.ser");
    }
}

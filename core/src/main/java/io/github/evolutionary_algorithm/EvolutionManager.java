package io.github.evolutionary_algorithm;

import io.github.neat.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

import static io.github.evolutionary_algorithm.Config.*;
import static io.github.evolutionary_algorithm.Config.Phase.EVOLUTIONARY;
import static io.github.neat.GAOperations.createOffspring;
import static io.github.neat.GAOperations.tournamentSelect;
import static io.github.neat.Genome.createRandomGenome;

public class EvolutionManager {
    private static EvolutionManager instance;
    private World world;

    public int getCurrentGeneration() {
        return currentGeneration;
    }

    public SpeciesManager getSpeciesManager() {
        return speciesManager;
    }

    private int currentGeneration;

    private Random r;
    private ArrayList<Genome> eliteGenomes;
    private SpeciesManager speciesManager;

    private EvolutionManager() {
        this.currentGeneration = 0;
        this.world = new World(WORLD_SIZE);
        this.speciesManager = SpeciesManager.getInstance();
        initFirstGeneration();
        this.eliteGenomes = new ArrayList<>();
    }

    public static EvolutionManager getInstance() {
        if (instance == null) {
            instance = new EvolutionManager();
        }
        return instance;
    }

    public boolean update() {
        if (currentPhase == EVOLUTIONARY) {
            return runEvolutionaryPhase();
        }
        else{
            return runAutoPhase();
        }

    }

    //training phase for agents
    private boolean runEvolutionaryPhase() {
        //fitness is evaluated already (from GUI)
        ArrayList<Genome> currentGenomes = world.getParentsGenome();
        System.out.println("DEBUG: currentGenomes.size() fetched from World: " + currentGenomes.size()); // <--- ADD THIS LINE

        ArrayList<Genome> nextGen = new ArrayList<>(NUM_CREATURES);
       // nextGen.addAll(getEliteGenomes());

        //clears old and respecifies new
        speciesManager.update(currentGenomes);
       // int remainingSlots = NUM_CREATURES - nextGen.size();
        int remainingSlots = NUM_CREATURES;

        nextGen.addAll(speciesManager.generateOffspring(remainingSlots));

        //prepare world for next gen
        world.reset();
        world.spawnCreatures(ensurePopulationSize(nextGen));
        speciesManager.getSpeciesStatistics();

        boolean endEvolution = ++currentGeneration >= EVOLUTION_GEN;
        if (endEvolution) {
            transitionToAuto();
        }
        return endEvolution;
    }

    private ArrayList<Genome> getEliteGenomes() {
        ArrayList<Genome> elite = world.getBestIndividuals((int) Math.round(ELITES*world.getPopulationSize()));
        eliteGenomes.clear();
        eliteGenomes = elite;
        return elite;
    }

    private ArrayList<Genome> ensurePopulationSize(ArrayList<Genome> population) {
        while (population.size() < NUM_CREATURES) {
            population.add(Genome.createRandomGenome());
            System.out.println("ALOOOO");
        }
        return population.stream()
            .sorted(Comparator.comparing(Genome::getFitness).reversed())
            .limit(NUM_CREATURES)
            .collect(Collectors.toCollection(ArrayList::new));
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
        System.out.println("Transitioned to autonomous phase.");
    }

    public void initFirstGeneration(){
        world.reset();
        ArrayList<Genome> randomGenomes = spawnRandomGeneration();
        for (Genome genome : randomGenomes) {
            speciesManager.addGenome(genome);
        }
        world.spawnCreatures(randomGenomes);
        currentGeneration++;
        System.out.println("First gen init with " + randomGenomes.size() + " genomes in " + speciesManager.getSpeciesList().size() + " species.");
    }

    private ArrayList<Genome> spawnRandomGeneration() {
        ArrayList<Genome> randomGenomes = new ArrayList<>();
        for (int i = 0; i < Config.NUM_CREATURES; i++) {
            randomGenomes.add(createRandomGenome());
        }
        return randomGenomes;
    }

    private void saveProgress() {
        ArrayList<Genome> bestGenomes = world.getBestIndividuals(5);
        GenomeSerializer.saveGenomeList(bestGenomes, "best_genomes.ser");
        INManager.saveToFile("inmanager_state.ser");
    }

    public World getWorld() {
        return world;
    }
}

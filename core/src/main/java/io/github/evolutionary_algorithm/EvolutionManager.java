package io.github.evolutionary_algorithm;

import io.github.neat.GAOperations;
import io.github.neat.Genome;
import io.github.neat.INManager;

import java.util.ArrayList;
import java.util.Random;

import static io.github.evolutionary_algorithm.Config.*;
import static io.github.evolutionary_algorithm.Config.Phase.EVOLUTIONARY;
import static io.github.neat.GAOperations.tournamentSelect;
import static io.github.neat.Genome.createRandomGenome;

public class EvolutionManager {
    private static EvolutionManager instance;
    private World world;
    private int currentGeneration;

    private Random r;
    private ArrayList<Genome> eliteGenomes;

    private EvolutionManager() {
        this.currentGeneration = 0;
        this.world = new World(WORLD_SIZE);
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

    private boolean runEvolutionaryPhase() {
        //fitness is recorded already

        eliteGenomes.clear();
        eliteGenomes.addAll(world.getBestIndividuals((int) (ELITE*NUM_CREATURES)));
        ArrayList<Genome> parents = world.getParentsGenome();
        ArrayList<Genome> nextGeneration = new ArrayList<>();

        //add unchanged
        nextGeneration.addAll(eliteGenomes);

        while (nextGeneration.size() < NUM_CREATURES) {
            Genome parent1 = tournamentSelect(parents);
            Genome parent2 = tournamentSelect(parents);
            Genome offspring = GAOperations.createOffspring(parent1, parent2);
            //offspring.analyzeWeights();
            nextGeneration.add(offspring);
        }
        world.reset();
        world.spawnCreatures(nextGeneration);
        System.out.println("NExt gen"+nextGeneration.size());
        currentGeneration++;

        if (currentGeneration >= EVOLUTION_GEN) {
            transitionToAuto();
        }

        return false;
    }

    private boolean runAutoPhase(){
        currentGeneration+=1;
        if (currentGeneration>MAX_GEN){
            return true;
        }
        return false;
    }

    private void transitionToAuto() {
        currentPhase = Phase.AUTO;
        //??
        world.spawnCreatures(eliteGenomes);
        System.out.println("Transitioned to autonomous phase.");
    }

    public void initFirstGeneration(){
        world.reset();
        ArrayList randomGenomes = spawnRandomGeneration();
        world.spawnCreatures(randomGenomes);
        currentGeneration++;
    }

    private ArrayList spawnRandomGeneration() {
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

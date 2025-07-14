package io.github.neat;

import io.github.evolutionary_algorithm.AbstractCreature;
import io.github.evolutionary_algorithm.Creature;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import static io.github.neat.Genome.createRandomGenome;

public class NEATManager {
    private SpeciesManager speciesManager;
    private ArrayList<Genome> eliteGenomes;
    private int numCreatures;

    public NEATManager(int numCreatures) {
        this.numCreatures = numCreatures;
        this.speciesManager = new SpeciesManager();
        this.eliteGenomes = new ArrayList<>();
    }

    public ArrayList<Genome> initFirstGeneration() {
        ArrayList<Genome> randomGenomes = spawnRandomGeneration(numCreatures);
        //sepcify
        for (Genome genome : randomGenomes) {
            speciesManager.addGenome(genome);
        }
        System.out.println("First gen init with " + randomGenomes.size() + " genomes in " + speciesManager.getSpeciesList().size() + " species.");
        return randomGenomes;
    }

    public ArrayList<Genome> evolveGeneration(ArrayList<AbstractCreature> currentCreatures) {
        ArrayList<Genome> currentGenomes = new ArrayList<>();
        System.out.println("DEBUG: currentGenomes.size() fetched from World: " + currentCreatures.size());

        for (AbstractCreature creature : currentCreatures) {
            currentGenomes.add(creature.getGenome());
        }

        ArrayList<Genome> nextGen = new ArrayList<>(numCreatures);

        //clears old and respecifies new
        speciesManager.update(currentGenomes);

        int remainingSlots = numCreatures - nextGen.size();

        nextGen.addAll(speciesManager.generateOffspring(remainingSlots));

        return ensurePopulationSize(nextGen);
    }


    private ArrayList<Genome> getEliteGenomes() {
        return new ArrayList<>(eliteGenomes);
    }

    private ArrayList<Genome> spawnRandomGeneration(int count) {
        ArrayList<Genome> randomGenomes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            randomGenomes.add(createRandomGenome());
        }
        return randomGenomes;
    }

    private ArrayList<Genome> ensurePopulationSize(ArrayList<Genome> population) {
        while (population.size() < numCreatures) {
            population.add(Genome.createRandomGenome());
            System.out.println("imhere");
        }
        return population.stream()
            .sorted(Comparator.comparing(Genome::getFitness).reversed())
            .limit(numCreatures)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public SpeciesManager getSpeciesManager() {
        return speciesManager;
    }

    public ArrayList<Genome> getBestIndividuals(int k) {
        return speciesManager.getGenomes().stream()
            .limit(k)
            .collect(Collectors.toCollection(ArrayList::new));
    }
}

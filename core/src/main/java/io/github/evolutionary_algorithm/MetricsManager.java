package io.github.evolutionary_algorithm;

import io.github.neat.Genome;
import io.github.neat.Species;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static io.github.evolutionary_algorithm.Config.METRICS_FILE;
import static io.github.evolutionary_algorithm.Config.actions;

public class MetricsManager {

    private static MetricsManager instance;

    // performance metrics
    private final List<Double> maxFitnessHistory = new ArrayList<>();
    private final List<Double> avgFitnessHistory = new ArrayList<>();
    private final List<Double> medianFitnessHistory = new ArrayList<>();

    // network complexity metrics
    private final List<Double> avgNodesHistory = new ArrayList<>();
    private final List<Double> avgEdgesHistory = new ArrayList<>();
    private final List<Double> networkDensityHistory = new ArrayList<>();

    // population diversity
    private final List<Integer> speciesCountHistory = new ArrayList<>();

    private final List<Double> avgHerbivoreFitnessHistory = new ArrayList<>();
    private final List<Double> avgCarnivoreFitnessHistory = new ArrayList<>();
    private final List<Double> maxHerbivoreFitnessHistory = new ArrayList<>();
    private final List<Double> maxCarnivoreFitnessHistory = new ArrayList<>();


    Map<Integer, Integer> actionCounts = new HashMap<>();
    private int totalAttacks = 0;
    private int successfulAttacks = 0;
    private int genTotalAttacks = 0;
    private int genSuccessfulAttacks = 0;
    private MetricsManager() {}

    public static MetricsManager getInstance() {
        if (instance == null) {
            instance = new MetricsManager();
        }
        return instance;
    }

    public void log(ArrayList<AbstractCreature> population, ArrayList<Species> speciesList) {
        if (population.isEmpty()) {
            System.err.println("Cannot calculate metrics on empty population.");
            return;
        }
        ArrayList<Genome> genomePopulation = new ArrayList<>();

        for (AbstractCreature creature : population) {
            genomePopulation.add(creature.getGenome());
        }
        recordPerformance(genomePopulation);
        recordComplexity(genomePopulation);
        recordDiversity(speciesList);
        recordDietStatistics(population);
    }

    private void recordPerformance(ArrayList<Genome> population) {
        List<Double> fitness = population.stream().map(Genome::getAdjustedFitness).sorted().toList();

        double total = fitness.stream().mapToDouble(Double::doubleValue).sum();
        double avg = total / fitness.size();

        int n = fitness.size();
        double median;

        if (n % 2 == 0) {
            median = (fitness.get(n / 2 - 1) + fitness.get(n / 2)) / 2.0;
        } else {
            median = fitness.get(n / 2);
        }

        double max = fitness.get(fitness.size() - 1);

        avgFitnessHistory.add(avg);
        medianFitnessHistory.add(median);
        maxFitnessHistory.add(max);
    }

    private void recordComplexity(ArrayList<Genome> population) {
        double avgNodes = population.stream().mapToInt(g -> g.getNodeGenes().size()).average().orElse(0);
        double avgEdges = population.stream().mapToInt(g -> g.getEdgeGenes().size()).average().orElse(0);
        double networkDensity = population.stream().mapToDouble(this::computeNetworkDensity).average().orElse(0);
        avgNodesHistory.add(avgNodes);
        avgEdgesHistory.add(avgEdges);
        networkDensityHistory.add(networkDensity);
    }

    private void recordDiversity(ArrayList<Species> speciesList){
        speciesCountHistory.add(speciesList.size());
    }

    private void recordDietStatistics(Collection<AbstractCreature> currentCreatures) {
        int herbivoreCount = 0;
        int carnivoreCount = 0;
        double totalHerbivoreFitness = 0;
        double totalCarnivoreFitness = 0;
        double maxHerbivoreFitness = 0;
        double maxCarnivoreFitness = 0;

        for (AbstractCreature creature : currentCreatures) {
            if (creature.getDietType() == AbstractCreature.DietType.HERBIVORE) {
                herbivoreCount++;
                totalHerbivoreFitness += creature.getFitness();
                double fitness = creature.getFitness();
                if (fitness > maxHerbivoreFitness) {
                    maxHerbivoreFitness = fitness;
                }
            } else {
                carnivoreCount++;
                double fitness = creature.getFitness();
                totalCarnivoreFitness += fitness;
                if (fitness > maxCarnivoreFitness) {
                    maxCarnivoreFitness = fitness;
                }

            }
        }
        double avgHerbivoreFitness = herbivoreCount > 0 ? totalHerbivoreFitness / herbivoreCount : 0.0;
        double avgCarnivoreFitness = carnivoreCount > 0 ? totalCarnivoreFitness / carnivoreCount : 0.0;

        avgHerbivoreFitnessHistory.add(avgHerbivoreFitness);
        avgCarnivoreFitnessHistory.add(avgCarnivoreFitness);
        maxHerbivoreFitnessHistory.add(maxHerbivoreFitness);
        maxCarnivoreFitnessHistory.add(maxCarnivoreFitness);

        System.out.println("Average Herbivore Fitness: "+avgHerbivoreFitness+" Average Carnivore Fitness: " +avgCarnivoreFitness);
        System.out.println("Max Herbivore Fitness: " + maxHerbivoreFitness + " Max Carnivore Fitness: " + maxCarnivoreFitness);

    }


    //this should be changed
    private double computeNetworkDensity(Genome genome) {
        int n = genome.getNodeGenes().size();
        int maxEdges = n * (n - 1); //fully con directed graph
        int edges = genome.getEdgeGenes().size();
        return (double) edges / maxEdges;
    }
    public void exportMetrics() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(METRICS_FILE))) {
            writer.println("Generation,MaxFitness,AvgFitness,MedianFitness,AvgNodes,AvgEdges,NetworkDensity,SpeciesCount");

            for (int i = 0; i < maxFitnessHistory.size(); i++) {
                writer.printf("%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%d%n",
                    i + 1,
                    maxFitnessHistory.get(i),
                    avgFitnessHistory.get(i),
                    medianFitnessHistory.get(i),
                    avgNodesHistory.get(i),
                    avgEdgesHistory.get(i),
                    networkDensityHistory.get(i),
                    speciesCountHistory.get(i)
                );
            }
            System.out.println("Metrics saved in " + METRICS_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save metrics: " + e.getMessage());
        }
    }

    public void updateActionCounts(int actionID){
        actionCounts.put(actionID, actionCounts.getOrDefault(actionID, 0) + 1);
    }


    public void printSummary() {
        printActionData();
        printAttackSummary();
    }

    private void printActionData(){
        int total = 0;
        for (int count : actionCounts.values()) {
            total += count;
        }

        for (int action : actionCounts.keySet()) {
            int count = actionCounts.get(action);
            double percent = 100.0 * count / total;
            System.out.println("Action " + actions[action] + ": " + percent);
        }

        actionCounts = new HashMap<>();
    }

    private void printAttackSummary() {
        double percentThisGen = genTotalAttacks > 0 ? 100.0 * genSuccessfulAttacks / genTotalAttacks : 0;
        System.out.println("Gen Attacks: " + genSuccessfulAttacks + "/" + genTotalAttacks + " " + percentThisGen + "%");

        double percentAllTime = totalAttacks > 0 ? 100.0 * successfulAttacks / totalAttacks : 0;
        System.out.println("Total Attacks: " + successfulAttacks + "/" + totalAttacks + " " + percentAllTime + "%");

        this.genTotalAttacks = 0;
        this.genSuccessfulAttacks = 0;
    }

    public void saveAttack() {
        totalAttacks++;
        genTotalAttacks++;
    }

    public void saveSuccesfulAttack() {
        successfulAttacks++;
        genSuccessfulAttacks++;
    }


}

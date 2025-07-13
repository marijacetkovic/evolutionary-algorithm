package io.github.evolutionary_algorithm;

import io.github.neat.Genome;
import io.github.neat.Species;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static io.github.evolutionary_algorithm.Config.METRICS_FILE;

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

    private MetricsManager() {}

    public static MetricsManager getInstance() {
        if (instance == null) {
            instance = new MetricsManager();
        }
        return instance;
    }

    public void log(ArrayList<Genome> population, ArrayList<Species> speciesList) {
        if (population.isEmpty()) {
            System.err.println("Cannot calculate metrics on empty population.");
            return;
        }

        recordPerformance(population);
        recordComplexity(population);
        recordDiversity(speciesList);
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

    public void printSummary() {
    }



  }

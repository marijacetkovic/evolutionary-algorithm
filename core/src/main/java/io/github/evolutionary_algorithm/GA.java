package io.github.evolutionary_algorithm;

import java.util.Random;

public class GA {

    private static double mutationProbability=0.2;
    private static Random r = new Random();

    public static int apply(Creature parentX, Creature parentY){
        int gene = crossover(parentX,parentY);
        if (r.nextDouble() < mutationProbability) {
           gene = mutate(gene);
        }
        return gene;
    }
    public static int crossover(Creature parentX, Creature parentY) {
        return (parentX.getGene().getValue() + parentY.getGene().getValue()) / 2;
    }

    public static int mutate(int gene) {
        return (int) (gene * Math.random());
    }
}

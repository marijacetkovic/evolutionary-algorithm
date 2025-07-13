package io.github.evolutionary_algorithm;

import io.github.neat.Genome;

import java.util.List;

public interface ICreature {
    int getId();
    int getI();
    int getJ();
    int getFoodType();
    int getHealth();
    Genome getGenome();
    double getFitness();
    void updatePosition(int i, int j);
    void takeAction(EventManager eventManager, World world);
    boolean checkHealth(World world);
    void consume();
    void evaluateAction(World w);
}

package io.github.evolutionary_algorithm;

import io.github.evolutionary_algorithm.events.EventManager;
import io.github.neat.Genome;

public interface ICreature {
    int getId();
    int getI();
    int getJ();
    int getFoodType();
    int getHealth();
    Genome getGenome();
    double getFitness();
    void updatePosition(int i, int j);
    void chooseAction(EventManager eventManager, World world);
    boolean isDead();
    void consume(Food f);
    void evaluateAction(World w);
}

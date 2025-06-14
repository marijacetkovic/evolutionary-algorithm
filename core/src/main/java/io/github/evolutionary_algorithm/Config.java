package io.github.evolutionary_algorithm;

public class Config {
    public static final boolean savePrematurely = false;
    public static final int NUM_CREATURES = 100;
    public static final int MAX_GEN = 100;
    public static final int inputFeatures = 18;
    public static final int MAX_CREATURES = 100;
    public static final int NUM_FOOD = 200;
    public static final int FOOD_CODE = -2;
    public static final int DEFAULT_CODE = -1;
    public static final boolean eat = true;
    public static final boolean breed = true;
    public static final double eatProbability = 1;
    public static final double breedProbability = 1;

    public static final float TIME = 0.07f;
    public static final int WORLD_SIZE = 50;
    public static int INITIAL_HEALTH = 500;
    public static int HEALTH_PENALTY = 2;
    public static int breedingThreshold = 10;
    public static double ELITE = 0.1;
    public static Phase currentPhase = Phase.EVOLUTIONARY;
    public enum Phase {
        EVOLUTIONARY,
        AUTO
    }
}

package io.github.evolutionary_algorithm;

public class Config {
    public static final boolean savePrematurely = false;
    public static final int NUM_CREATURES = 300;
    public static final int MAX_GEN = 26;
    public static final int EVOLUTION_GEN = 25;

    public static final int MAX_CREATURES = 350;
    public static final int NUM_FOOD = 200;
    public static final int FOOD_CODE = -2;
    public static final int DEFAULT_CODE = -1;
    public static final boolean eat = true;
    public static final boolean breed = true;
    public static final double eatProbability = 1;
    public static final double breedProbability = 1;

    public static final float TIME = 0.07f;
    public static final int WORLD_SIZE = 50;
    public static int INITIAL_HEALTH = 300;
    public static int HEALTH_PENALTY = 2;
    public static int breedingThreshold = 10;
    public static double ELITE = 0.1;
    public static double MAX_INITIAL_WEIGHT = 2;
    public static double MIN_INITIAL_WEIGHT = -2;
    public static double ADD_NODE_MUTATION_PROB = 0.4;
    public static double ADD_EDGE_MUTATION_PROB = 0.7;
    private static final int ACTION_MOVE_UP = 0;
    private static final int ACTION_MOVE_LEFT = 1;
    private static final int ACTION_MOVE_DOWN = 2;
    private static final int ACTION_MOVE_RIGHT = 3;
    private static final int ACTION_STAY = 4;
    static final int ACTION_EAT = 5;
    public static Phase currentPhase = Phase.EVOLUTIONARY;
    public enum Phase {
        EVOLUTIONARY,
        AUTO
    }
}

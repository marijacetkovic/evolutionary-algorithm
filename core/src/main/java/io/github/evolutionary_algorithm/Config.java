package io.github.evolutionary_algorithm;

public class Config {
    public static final int WORLD_SIZE = 100;
    public static final int CREATURE_LOCATION_BOUND = WORLD_SIZE;

    public static final int NUM_CREATURES = 500;
    public static final double ELITES = 0.03;
    public static final int MAX_GEN = 70;
    public static final int EVOLUTION_GEN = MAX_GEN+1;

    public static final int MAX_CREATURES = 550;
    public static final int NUM_FOOD = 300;
    public static final int MIN_FOOD_LVL = 2;
    public static final int FOOD_LOCATION_BOUND = 3;
    public static final int FOOD_QUADRANT_BOUND = 2;
    public static final int FOOD_PHASE_END1 = 10 ;
    public static final int FOOD_PHASE_END2 = 20 ;
    public static final int FOOD_PHASE_END3 = 30 ;


    public static final int FOOD_CODE = -2;
    public static final int DEFAULT_CODE = -1;
    public static final boolean eat = true;
    public static final boolean breed = true;
    public static final double eatProbability = 1;
    public static final double breedProbability = 1;

    public static final float TIME = 0.07f;
    public static int INITIAL_HEALTH = 300;
    public static int MAX_HEALTH = 600;

    public static int HEALTH_PENALTY = 2;
    public static double breedingThreshold = 10;

    public static final int ACTION_MOVE_UP = 0;
    public static final int ACTION_MOVE_LEFT = 1;
    public static final int ACTION_MOVE_DOWN = 2;
    public static final int ACTION_MOVE_RIGHT = 3;
    public static final int ACTION_STAY = 4;
    public static final int FOOD_REWARD = 10;
    public static final int ACTION_EAT = 5;
    public static Phase currentPhase = Phase.EVOLUTIONARY;
    public enum Phase {
        EVOLUTIONARY,
        AUTO
    }
    public static final String METRICS_FILE = "output/metrics.csv";

}

package io.github.neat;

public class Config {
    public static final double WEIGHT_MUTATION_RATE = 0.8;
    public static final double STRUCTURAL_MUTATION_RATE = 0.1;
    public static double MAX_INITIAL_WEIGHT = 2;
    public static double MIN_INITIAL_WEIGHT = -2;
    public static double ADD_NODE_MUTATION_PROB = 0.5;
    public static double TOGGLE_CONN_MUTATION_PROB = 0.8;

    public static double ADD_EDGE_MUTATION_PROB = 0.5;
    public static double BIAS_MUTATION_RATE = 0.5;
    public static double NODE_BIAS_MUTATION_RATE = 0.8;
    public static double GAUSSIAN_BIAS_MUTATION_PROB = 0.9;
    public static double BIAS_MUTATION_STRENGTH = 0.1;
    public static double LEAKY_RELU_ALPHA = 0.01;

    public static double MIN_BIAS = -3.0;
    public static double MAX_BIAS = 3.0;

    //species management
    public static final double COMPATIBILITY_THRESHOLD = 1.5;
    public static final double C1 = 1;
    public static final double C2 = 1;
    public static final double C3 = 1;
    public static final double ELITE_SPECIES_RATE = 0.1;


    public static boolean start = false;
    public static boolean structuralMutation = true;
    public static boolean test = false;

    public static int numInputs = 50;
    public static int numOutputs = 7;
    public static int startNodeId = numInputs + numOutputs;
    public static final int TOURNAMENT_SIZE = 5;
    public static final double TOURNAMENT_RND_PROB = 0.03;

}

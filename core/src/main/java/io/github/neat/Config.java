package io.github.neat;

public class Config {
    public static final double WEIGHT_MUTATION_RATE = 0.8;
    public static final double STRUCTURAL_MUTATION_RATE = 0.5;
    public static double BIAS_MUTATION_RATE = 0.5;
    public static double NODE_BIAS_MUTATION_RATE = 0.8;
    public static double GAUSSIAN_BIAS_MUTATION_PROB = 0.9;
    public static double BIAS_MUTATION_STRENGTH = 0.1;
    public static double MIN_BIAS = -3.0;
    public static double MAX_BIAS = 3.0;
    public static boolean start = false;
    public static boolean structuralMutation = true;
    public static boolean test = false;

    public static int numInputs = 29;
    public static int numOutputs = 6;
    public static int startNodeId = numInputs + numOutputs;
    public static final int TOURNAMENT_SIZE = 5;
}

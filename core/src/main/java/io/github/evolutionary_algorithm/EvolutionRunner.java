package io.github.evolutionary_algorithm;

public class EvolutionRunner {
    public static void main(String[] args) {
        EvolutionManager evolutionManager = EvolutionManager.getInstance();
        World world = evolutionManager.getWorld();

        while (true) {
            boolean alive = world.behave();
            evolutionManager.monitor();
            if(!alive) {
                boolean simulationComplete = evolutionManager.update();

                if (simulationComplete) {
                    System.out.println("Simulation done.");
                    break;
                }
            }
        }
    }
}

package io.github.evolutionary_algorithm;

public class Simulation {

    private final int generations;

    public Simulation(int generations){
        this.generations = generations;
    }

    public void run(){
        World world = new World(Config.WORLD_SIZE);
        for (int i = 0; i < generations; i++) {
            boolean alive = world.behave();
            if(!alive) break;
            System.out.println("Generation "+i);
        }
    }
}

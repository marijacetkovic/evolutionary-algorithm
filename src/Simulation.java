public class Simulation {

    private final int generations;

    public Simulation(int generations){
        this.generations = generations;
    }

    public void run(){
        World world = new World(5);
        for (int i = 0; i < generations; i++) {
            world.behave();
        }
    }
}

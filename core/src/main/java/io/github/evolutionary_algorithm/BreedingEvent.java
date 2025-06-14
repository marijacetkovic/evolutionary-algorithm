package io.github.evolutionary_algorithm;

import io.github.neat.GAOperations;
import io.github.neat.Genome;

public class BreedingEvent extends Event{
    private Creature parentX;
    private Creature parentY;
    private World w;
    public BreedingEvent(Creature x, Creature y, World w){
        this.name = "breed";
        this.parentX = x;
        this.parentY = y;
        this.w = w;
        //System.out.println("New breeding event between" +x.getId()+ " and "+y.getId());
    }


    public Creature getParentX() {
        return parentX;
    }

    public Creature getParentY() {
        return parentY;
    }

    @Override
    public void process() {
        Genome childGenome = GAOperations.createOffspring(parentX.getGenome(), parentY.getGenome());
        Creature c = w.spawnChild(childGenome);
        if(c!=null){
            System.out.println("Creature "+ parentX.getId()+" creature "+parentY.getId()+" created "+c.getId());
        }
        else{
            System.out.println("No child could be created.");
        }
    }

}

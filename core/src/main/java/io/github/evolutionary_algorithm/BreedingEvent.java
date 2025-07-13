package io.github.evolutionary_algorithm;

import io.github.neat.GAOperations;
import io.github.neat.Genome;

public class BreedingEvent extends Event{
    private AbstractCreature parentX;
    private AbstractCreature parentY;
    private World w;
    public BreedingEvent(AbstractCreature x, AbstractCreature y, World w){
        this.name = "breed";
        this.parentX = x;
        this.parentY = y;
        this.w = w;
        System.out.println("New breeding event between" +x.getId()+ " and "+y.getId());
    }


    public AbstractCreature getParentX() {
        return parentX;
    }

    public AbstractCreature getParentY() {
        return parentY;
    }

    @Override
    public void process() {
        Genome childGenome = GAOperations.createOffspring(parentX.getGenome(), parentY.getGenome());
        AbstractCreature c = w.spawnChild(childGenome);
        if(c!=null){
            System.out.println("Creature "+ parentX.getId()+" creature "+parentY.getId()+" created "+c.getId());
        }
        else{
            System.out.println("No child could be created.");
        }
    }

}

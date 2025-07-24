package io.github.evolutionary_algorithm.events;

import io.github.evolutionary_algorithm.Creature;
import io.github.evolutionary_algorithm.Food;
import io.github.evolutionary_algorithm.World;

public class EatingEvent extends Event{
    private int i;
    private int j;
    private Food food;
    public EatingEvent(Creature c, World world, Food food){
        super(world,c);
        name = "eat";
        this.food = food;
       //System.out.println("New eating event by" +c.getId()+"at "+i+" "+j);
    }

    @Override
    public void process() {
        //System.out.println("Creature type "+c.getDietType()+" consumed food type "+f.getCode());
        world.removeFood(food);
        initiator.consume(food);
    }
}

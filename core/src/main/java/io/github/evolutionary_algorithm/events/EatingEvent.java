package io.github.evolutionary_algorithm.events;

import io.github.evolutionary_algorithm.Creature;
import io.github.evolutionary_algorithm.Food;
import io.github.evolutionary_algorithm.World;

public class EatingEvent extends Event{
    private int i;
    private int j;
    private Food f;
    public EatingEvent(Creature c, int i, int j, World w, Food f){
        super(w,c);
        name = "eat";
        this.i = i;
        this.j = j;
        this.f = f;
       //System.out.println("New eating event by" +c.getId()+"at "+i+" "+j);
    }

    @Override
    public void process() {
        //System.out.println("Creature type "+c.getDietType()+" consumed food type "+f.getCode());
        world.removeFood(f);
        initiator.consume(f);
    }
}

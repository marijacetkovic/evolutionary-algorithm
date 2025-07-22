package io.github.evolutionary_algorithm;

public class EatingEvent extends Event{
    private int i;
    private int j;
    public Creature c;
    private World w;
    private Food f;
    public EatingEvent(Creature c, int i, int j, World w, Food f){
        name = "eat";
        this.c = c;
        this.i = i;
        this.j = j;
        this.w = w;
        this.f = f;
       //System.out.println("New eating event by" +c.getId()+"at "+i+" "+j);
    }

    @Override
    public void process() {
        //System.out.println("Creature type "+c.getDietType()+" consumed food type "+f.getCode());
        w.removeFood(f);
        c.consume(f);
    }
}

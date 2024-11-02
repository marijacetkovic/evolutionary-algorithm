public class EatingEvent extends Event{
    private int i;
    private int j;
    public Creature c;
    private World w;
    public EatingEvent(Creature c, int i, int j, World w){
        name = "eat";
        this.c = c;
        this.i = i;
        this.j = j;
        this.w = w;
        System.out.println("New eating event by" +c.getId()+"at "+i+" "+j);
    }

    @Override
    public void process() {
        w.remove(i,j,Config.FOOD_CODE);
        c.consume();
    }
}

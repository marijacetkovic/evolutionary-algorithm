public class EatingEvent extends Event{
    public int[] foodLocation;
    public Creature c;
    public EatingEvent(Creature c, int[] foodLocation){
        name = "eat";
        this.c = c;
        this.foodLocation = foodLocation;
        System.out.println("New eating event by" +c.getId()+"at "+foodLocation[0]+" "+foodLocation[1]);
    }
}

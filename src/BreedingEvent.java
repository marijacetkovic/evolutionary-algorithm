public class BreedingEvent extends Event{
    private Creature parentX;
    private Creature parentY;
    public BreedingEvent(Creature x, Creature y){
        name = "breed";
        parentX = x;
        parentY = y;
    }
}

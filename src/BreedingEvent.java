public class BreedingEvent extends Event{
    private Creature parentX;
    private Creature parentY;
    public BreedingEvent(Creature x, Creature y){
        name = "breed";
        parentX = x;
        parentY = y;
        System.out.println("New breeding event between" +x.getId()+ " and "+y.getId());
    }

    public Creature getParentX() {
        return parentX;
    }

    public Creature getParentY() {
        return parentY;
    }
}

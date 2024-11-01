public class BreedingEvent extends Event{
    private Creature parentX;
    private Creature parentY;
    private World w;
    public BreedingEvent(Creature x, Creature y, World w){
        this.name = "breed";
        this.parentX = x;
        this.parentY = y;
        this.w = w;
        System.out.println("New breeding event between" +x.getId()+ " and "+y.getId());
    }



    public Creature getParentX() {
        return parentX;
    }

    public Creature getParentY() {
        return parentY;
    }

    @Override
    public void process() {
        Creature child = w.spawnCreature();
        if(child!=null){
            System.out.println("Creature "+ parentY.getId()+" creature "+parentY.getId()+" created "+child.getId());
        }
        else{
            //System.out.println("No child could be created.");
        }
    }
}

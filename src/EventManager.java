import java.sql.SQLOutput;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class EventManager {
    Queue<Event> events;
    World world;
    public EventManager(World world){
        this.events = new LinkedList<>();
        this.world = world;
    }

    public void enqueue(Event e){
        events.add(e);
    }

    public void process(){
        while(!events.isEmpty()){
            Event e = events.poll();
            if (e.name=="breed") processBreed((BreedingEvent) e);
        }
    }

    public void processBreed(BreedingEvent e){
        Creature child = world.spawnCreature();
        if(child!=null){
        System.out.println("Creature "+ e.getParentX().getId()+" creature "+e.getParentY().getId()+" created "+child.getId());
        }
        else{
            System.out.println("No child could be created.");
        }
    }
}

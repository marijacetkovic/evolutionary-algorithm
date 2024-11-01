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
            e.process();
        }
    }
}

import java.util.PriorityQueue;
import java.util.Queue;

public class EventManager {
    Queue<Event> events;
    public EventManager(){
        events = new PriorityQueue<>();
    }
}

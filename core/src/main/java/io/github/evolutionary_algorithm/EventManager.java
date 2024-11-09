package io.github.evolutionary_algorithm;

import java.util.LinkedList;
import java.util.Queue;

public class EventManager {
    Queue<Event> events;
    World world;
    public EventManager(World world){
        this.events = new LinkedList<>();
        this.world = world;
    }

    public void publish(Event e, boolean processNow){
        if(processNow) e.process();
        else events.add(e);
    }

    public void process(){
        while(!events.isEmpty()){
            Event e = events.poll();
            e.process();
        }
    }
}

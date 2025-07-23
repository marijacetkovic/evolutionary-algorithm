package io.github.evolutionary_algorithm.events;

import io.github.evolutionary_algorithm.AbstractCreature;
import io.github.evolutionary_algorithm.Creature;
import io.github.evolutionary_algorithm.World;

public abstract class Event {
    protected String name;
    protected World world;
    protected AbstractCreature initiator;
    public Event(World world, AbstractCreature creature) {
        this.world = world;
        this.initiator = creature;
    }
    public String getName() {
        return name;
    }

    public abstract void process();
}

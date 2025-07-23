package io.github.evolutionary_algorithm.events;

import io.github.evolutionary_algorithm.Creature;
import io.github.evolutionary_algorithm.World;

public class DeathEvent extends Event {
    public DeathEvent(Creature creature, World world) {
        super(world,creature);
    }

    @Override
    public void process() {
        initiator.setHealth(0);
        world.getPopulation().remove(initiator.getId());
        world.getPrevPopulation().add(initiator);
        world.remove(initiator.getI(), initiator.getJ(), initiator.getId());
    }
}

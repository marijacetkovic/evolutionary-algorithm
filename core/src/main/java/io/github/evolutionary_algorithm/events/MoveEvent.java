package io.github.evolutionary_algorithm.events;

import io.github.evolutionary_algorithm.AbstractCreature;
import io.github.evolutionary_algorithm.World;

public class MoveEvent extends Event {
    private int decision;
    public MoveEvent(AbstractCreature initiator, World world, int decision) {
        super(world, initiator);
        this.decision = decision;
    }

    @Override
    public void process() {
        int i = initiator.getI() + initiator.actionOffset[decision][0];
        int j = initiator.getJ() + initiator.actionOffset[decision][1];
        world.moveCreature(initiator, i, j);
    }
}

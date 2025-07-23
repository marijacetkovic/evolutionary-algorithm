package io.github.evolutionary_algorithm.events;

import io.github.evolutionary_algorithm.Config;
import io.github.evolutionary_algorithm.Creature;
import io.github.evolutionary_algorithm.World;

public class AttackEvent extends Event {
    private final Creature attacker;
    private final Creature target;
    private final World world;

    public AttackEvent(Creature attacker, Creature target, World world) {
        super(world, attacker);
        this.attacker = attacker;
        this.target = target;
        this.world = world;
    }

    public Creature getAttacker() {
        return attacker;
    }

    public Creature getTarget() {
        return target;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void process() {
        initiator.setHealth(initiator.getHealth() - Config.ATTACK_COST);
    }
}

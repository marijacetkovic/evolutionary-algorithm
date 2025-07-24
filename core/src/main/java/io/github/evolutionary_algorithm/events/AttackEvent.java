package io.github.evolutionary_algorithm.events;

import io.github.evolutionary_algorithm.Creature;
import io.github.evolutionary_algorithm.World;

public class AttackEvent extends Event {
    private final Creature target;

    public AttackEvent(Creature attacker, Creature target, World world) {
        super(world, attacker);
        this.target = target;
    }

    public Creature getAttacker() {
        return (Creature) initiator;
    }

    public Creature getTarget() {
        return target;
    }

    public World getWorld() {
        return world;
    }

    // need to check if the target is still viable
    @Override
    public void process() {
        if (initiator.isDead() || target.isDead()) {
            return;
        }

        if (initiator.getI() != target.getI() || initiator.getJ() != target.getJ()) {
            return;
        }

        initiator.setHealth((int) (initiator.getHealth() - initiator.getAttackCost()));

        target.setHealth((int) (target.getHealth() - initiator.getAttackDamage()));

        if (target.getHealth() <= 0) {
            target.setHealth(0);
            if (!target.isDead()) {
                //<---- maybe easier to mark as dead and then remove everyone that died at the end
                world.getEventManager().publish(new DeathEvent(target, world), true);
            }
        }
    }
}

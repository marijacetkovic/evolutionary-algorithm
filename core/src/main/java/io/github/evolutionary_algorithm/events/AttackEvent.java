package io.github.evolutionary_algorithm.events;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import io.github.evolutionary_algorithm.Creature;
import io.github.evolutionary_algorithm.MetricsManager;
import io.github.evolutionary_algorithm.World;

import static io.github.evolutionary_algorithm.Config.ATTACK_RANGE;

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

        /*if (initiator.getI() != target.getI() || initiator.getJ() != target.getJ()) {
            return;
        }*/

        int dx = Math.abs(initiator.getI() - target.getI());
        int dy = Math.abs(initiator.getJ() - target.getJ());

        if (dx > ATTACK_RANGE || dy > ATTACK_RANGE) {
            return;
        }

        MetricsManager.getInstance().saveAttack();

        initiator.setHealth((int) (initiator.getHealth() - initiator.getAttackCost()));

        target.setHealth((int) (target.getHealth() - initiator.getAttackDamage()));

        if (target.getHealth() <= 0) {
            target.setHealth(0);
            if (!target.isDead()) {
                //System.out.println("Creature "+target.getId()+
                //    " died from being attacked by creature " + initiator.getId());
                //<---- maybe easier to mark as dead and then remove everyone that died at the end
                MetricsManager.getInstance().saveSuccesfulAttack();
                target.markDead();
            }
        }
    }
}

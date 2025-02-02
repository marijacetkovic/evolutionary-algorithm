package io.github.neat;

import java.util.ArrayList;
import java.util.Random;

public class Species {
    private int id;
    private Genome representative;
    private ArrayList<Genome> members;
    private double c1, c2, c3;
    private int n;

    public Species(int id,Genome rep) {
        this.id = id;
        this.representative = rep;
        this.members = new ArrayList<>();
        members.add(rep);
        //parameters for compatibility distance
        c1 = 1;
        c2 = 1;
        c3 = 1;
        //nr of genes of the larger genome
        n=1;
    }

    public Genome getRepresentative() {
        return representative;
    }

    public void setRepresentative(Genome representative) {
        this.representative = representative;
    }

    public ArrayList<Genome> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Genome> members) {
        this.members = members;
    }

    public void setNewRepresentative() {
        if (!members.isEmpty()) {
            this.representative = members.get(new Random().nextInt(members.size()));
        }
    }


    public double compatibilityDistance(Genome g) {
        ArrayList<Edge> g1 = g.getGenesSorted();
        ArrayList<Edge> g2 = representative.getGenesSorted();

        int i = 0, j = 0;
        int excess = 0, disjoint = 0, matching = 0;
        double totalDiff = 0;

        // calculate normalization factor
        int N = Math.max(g1.size(), g2.size());

        while (i < g1.size() && j < g2.size()) {
            Edge e1 = g1.get(i);
            Edge e2 = g2.get(j);

            if (e1.getInnovationNumber() == e2.getInnovationNumber()) {
                //matching - get weight difference
                totalDiff += Math.abs(e1.getWeight() - e2.getWeight());
                matching++;
                i++;
                j++;
            } else if (e1.getInnovationNumber() < e2.getInnovationNumber()) {
                // disjoint gene in g1
                disjoint++;
                i++;
            } else {
                // disjoint gene in g2
                disjoint++;
                j++;
            }
        }

        // count excess genes
        while (i < g1.size()) {
            excess++;
            i++;
        }

        while (j < g2.size()) {
            excess++;
            j++;
        }

        double avgDiff = totalDiff / matching;

        // compatibility distance formula
        double delta = (c1 * excess) / N + (c2 * disjoint) / N + c3 * avgDiff;

        // update rep to avoid staticness?
        setNewRepresentative();

        return delta;
    }

    public void addMember(Genome genome) {
        members.add(genome);
        genome.setSpecies(this);
    }
}


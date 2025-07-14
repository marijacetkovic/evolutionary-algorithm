package io.github.neat;

import java.util.*;

import static io.github.neat.Config.*;
import static io.github.neat.GAOperations.createOffspring;
import static io.github.neat.GAOperations.tournamentSelect;

public class Species {
    private int id;
    private Genome representative;
    private ArrayList<Genome> members;

    public Species(int id,Genome rep) {
        this.id = id;
        this.representative = rep;
        this.members = new ArrayList<>();
        members.add(rep);
    }

    public Genome getRepresentative() {
        return representative;
    }

    public void setRepresentative(Genome representative) {
        this.representative = representative;
    }

    public ArrayList<Genome> getMembers() {
        return new ArrayList<>(members);
    }
    public void clearMembers() {
        members.clear();
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
        //should be 1 for small networks?
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
        double avgDiff = 0;

        if (matching>0) {
            avgDiff = totalDiff / matching;
        }
        // compatibility distance formula
        double delta = (C1 * excess) / N + (C2 * disjoint) / N + C3 * avgDiff;
        //System.out.println("Delta "+delta);
        return delta;
    }

    public void addMember(Genome genome) {
        members.add(genome);
        genome.setSpecies(this);
    }

    //fitness adjustment wrt species size
    public void adjustMemberFitness() {
        if (members.isEmpty()) return;
        for (Genome g : members) {
            g.setAdjustedFitness(g.getFitness() / members.size());
        }
    }

    public double getAvgAdjustedFitness() {
        if (members.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Genome g : members) {
            sum += g.getAdjustedFitness();
        }
        return sum / members.size();
    }

    //returns total adjusted fitness of a species
    public double getSpeciesFitness() {
        if (members.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Genome g : members) {
            sum += g.getAdjustedFitness();
        }
        return sum;
    }

    //basic species statistics
    public void printFitnessStats() {
        if (members.isEmpty()) {
            System.out.printf("S%d: 0 members extinct %n", id);
            return;
        }

        DoubleSummaryStatistics stats = members.stream()
            .mapToDouble(Genome::getFitness) //convert double stream
            .summaryStatistics();

        System.out.printf("S%-3d | %8d | %8d | %8d| %8d | %8d%n",
            id,
            members.size(),
            representative.getNodeGenes().size(),
            Math.round(stats.getMin()),
            Math.round(stats.getAverage()),
            Math.round(stats.getMax()));
    }

    //breeding within the species
    public ArrayList<Genome> breedInSpecies(int portion) {
        if (portion <= 0 || members.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Genome> offspring = new ArrayList<>(portion);
        ArrayList<Genome> sorted = getSortedMembers();

        //get top species rate genomes - elites from here or from global pop?
        int elites = (int) Math.min(Math.ceil (ELITE_SPECIES_RATE * portion), sorted.size());
        System.out.println("Elite chosen "+elites);
        offspring.addAll(sorted.subList(0, elites));

        for (int i = 0; i < portion; i++) {
            Genome parent1 = tournamentSelect(sorted);
            Genome parent2 = tournamentSelect(sorted);
            offspring.add(createOffspring(parent1, parent2));
        }
        return offspring;
    }

    private ArrayList<Genome> getSortedMembers() {
        ArrayList<Genome> sorted = new ArrayList<>(members);
        sorted.sort(Comparator.comparing(Genome::getAdjustedFitness).reversed());
        return sorted;
    }


    public int getId() {
        return id;
    }
}


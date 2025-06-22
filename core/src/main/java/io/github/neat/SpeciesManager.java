package io.github.neat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import static io.github.neat.Config.COMPATIBILITY_THRESHOLD;

public class SpeciesManager {
    private static SpeciesManager speciesManager;
    private ArrayList<Species> speciesList;

    private Genome lastAddedGenome;
    private int nextSpeciesId = 0;

    private SpeciesManager() {
        this.speciesList = new ArrayList<>();
    }

    public static SpeciesManager getInstance(){
        if (speciesManager == null){
            speciesManager = new SpeciesManager();
        }
        return speciesManager;
    }

    public Species addGenome(Genome genome) {
        //go through species and if distance is below threshold assign genome the species
        lastAddedGenome = genome;
        for (Species species : speciesList) {
            if (species.compatibilityDistance(genome) < COMPATIBILITY_THRESHOLD) {
                species.addMember(genome);
                return species;
            }
        }
        Species newSpecies = new Species(nextSpeciesId++, genome);
        speciesList.add(newSpecies);
        return newSpecies;
    }

    public ArrayList<Species> getSpeciesList() {
        return speciesList;
    }


    public Genome getLastAddedGenome() {
        return lastAddedGenome;
    }

    //clears all members of species and removes empty species
    public void update(ArrayList<Genome> currentGenomes) {
        speciesList.removeIf(s -> s.getMembers().isEmpty());

        for (Species s : speciesList) {
            s.getMembers().clear();
        }
        //respecify
        for (Genome genome : currentGenomes) {
            addGenome(genome);
        }

        //select new rep
        for (Species s : speciesList) {
            if (!s.getMembers().isEmpty()) {
                s.setNewRepresentative();
            }
        }

        //adjust relative fitness
        for (Species s : speciesList) {
            s.adjustMemberFitness();
        }
    }

    //global adjusted fitness across species
    public double getAllSpeciesFitness() {
        double total = 0;
        for (Species s : speciesList) {
            total += s.getSpeciesFitness();
        }
        return total;
    }
    public void getSpeciesStatistics() {
        int totalSpecies = speciesList.size();
        if (totalSpecies == 0) {
            System.out.println("No active species.");
            return;
        }
        System.out.printf("%-4s | %8s | %8s | %8s | %8s | %8s%n", "Spec", "Members", "Genome Size", "Min", "Avg", "Max");
        System.out.println("--------------------------------------------------------");
        for (Species s : speciesList) {
            s.printFitnessStats();
        }
    }
    public ArrayList<Genome> generateOffspring(int remainingSlots) {
        ArrayList<Genome> offspring = new ArrayList<>();

        double totalFitness = getAllSpeciesFitness();
        //speciesList.sort(Comparator.comparingDouble(Species::getAvgAdjustedFitness).reversed());

        for (Species species : speciesList) {
            //every species gets a portion of next pop to create offspring in
            int portion = calcSpeciesPortion(species, totalFitness, remainingSlots);
            offspring.addAll(species.breedInSpecies(portion));
        }

        return offspring;
    }

    private int calcSpeciesPortion(Species species, double totalFitness, int remainingSlots) {
        if (species.getMembers().isEmpty() || totalFitness <= 0) return 0;
        double proportion = species.getSpeciesFitness() / totalFitness;
        int portion = (int) Math.round(proportion * remainingSlots);

        //protect very small but nonempty species
        if (portion == 0 && !species.getMembers().isEmpty() && remainingSlots > 0) {
            return 1;
        }
        return portion;
    }

}

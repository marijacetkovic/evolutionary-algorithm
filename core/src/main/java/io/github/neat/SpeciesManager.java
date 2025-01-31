package io.github.neat;
import java.util.ArrayList;

public class SpeciesManager {
    private ArrayList<Species> speciesList;
    private double threshold;

    public SpeciesManager() {
        this.speciesList = new ArrayList<>();
    }

    public void addGenome(Genome genome) {
        //go through species and if distance is below threshold assign genome tthe species
        for (Species species : speciesList) {
            if (species.geneticDistance(genome) < threshold) {
                species.addMember(genome);
                return;
            }
        }
        Species newSpecies = new Species(0, genome);
        speciesList.add(newSpecies);
    }

    public ArrayList<Species> getSpeciesList() {
        return speciesList;
    }



}

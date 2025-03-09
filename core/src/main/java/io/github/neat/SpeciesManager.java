package io.github.neat;
import java.util.ArrayList;

public class SpeciesManager {
    private static SpeciesManager speciesManager;
    private ArrayList<Species> speciesList;
    private double threshold;
    private Genome lastAddedGenome;

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
        //go through species and if distance is below threshold assign genome tthe species
        lastAddedGenome = genome;
        for (Species species : speciesList) {
            if (species.compatibilityDistance(genome) < threshold) {
                species.addMember(genome);
                return species;
            }
        }
        Species newSpecies = new Species(0, genome);
        speciesList.add(newSpecies);
        return newSpecies;
    }

    public ArrayList<Species> getSpeciesList() {
        return speciesList;
    }


    public Genome getLastAddedGenome() {
        return lastAddedGenome;
    }
}

package io.github.evolutionary_algorithm;

import io.github.neat.Genome;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GenomeSerializer {
    // method to save genome object to a file
    public static void saveGenome(Genome genome, String filename) {
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(genome);
            System.out.println("Genome saved to " + filename);
        } catch (IOException i) {
            System.out.println("Error saving genome: " + i.getMessage());
            i.printStackTrace();
        }
    }

    // method to load genome object from a file
    public static Genome loadGenome(String filename) {
        Genome genome = null;
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            genome = (Genome) in.readObject();
            genome.setFitness(0); //clear fitness
            System.out.println("Genome loaded from " + filename);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading genome: " + e.getMessage());
            e.printStackTrace();
        }
        return genome;
    }
    public static void saveGenomeList(ArrayList<Genome> genomes, String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(genomes);
            System.out.println("Saved " + genomes.size() + " genomes to " + filename);
        } catch (IOException e) {
            System.err.println("List save error: " + e.getMessage());
        }
    }

    public static ArrayList<Genome> loadGenomeList(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            ArrayList<Genome> loaded = (ArrayList<Genome>) in.readObject();
            System.out.println("Successfully loaded list");
            loaded.forEach(g -> g.setFitness(0)); // Reset fitness for all
            return loaded;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("List load error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

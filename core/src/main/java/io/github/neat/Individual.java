package io.github.neat;

import java.util.ArrayList;

import static io.github.neat.NodeType.*;

public class Individual {
    int id;
    Genome genome;
    ArrayList<Edge> edges;
    ArrayList<Node> nodes;
    String[] actions = { "eat", "breed", "move", "do_nothing" };

    public Individual(int id, Genome genome) {
        this.id = id;
        this.genome = genome;
        this.edges = genome.getEdgeGenes();
        this.nodes = genome.getNodeGenes();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public void makeDecision(double[] input){
        int decision = genome.calcPropagation(input);
        System.out.println("Individuals decision: "+actions[decision]);
    }
//    private void calcPropagation(){
//        propagateLayer(HIDDEN);
//        propagateLayer(OUTPUT);
//    }
//
//    private void propagateLayer(NodeType layer){
//        for (Node n: nodes) {
//            if (n.getNodeType() == layer){
//                n.calculateValue();
//                n.activate();
//            }
//        }
//    }
}

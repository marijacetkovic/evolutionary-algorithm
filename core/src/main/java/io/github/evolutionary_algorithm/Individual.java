package io.github.evolutionary_algorithm;

import java.util.ArrayList;

import static io.github.evolutionary_algorithm.NodeType.*;

public class Individual {
    int id;
    Genome genome;
    ArrayList<Edge> edges;
    ArrayList<Node> nodes;

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

    public void makeDecision(){
        calcPropagation();
    }
    private void calcPropagation(){
        for (Node n: nodes) {
            if (n.getNodeType() == HIDDEN){
                n.calculateValue();
                n.activate();
            }
        }
        for (Node n: nodes) {
            if (n.getNodeType() == OUTPUT){
                n.calculateValue();
                n.activate();
            }
        }
    }
}

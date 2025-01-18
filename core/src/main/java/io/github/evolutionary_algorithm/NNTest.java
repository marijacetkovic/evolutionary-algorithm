package io.github.evolutionary_algorithm;

import java.util.ArrayList;

import static io.github.evolutionary_algorithm.NodeType.*;

public class NNTest {
    public static void main(String[] args) {
        Genome genome = new Genome();

        genome.addNode(new Node(1, INPUT));
        genome.addNode(new Node(2, INPUT));
        genome.addNode(new Node(3, OUTPUT));
        genome.addNode(new Node(4, HIDDEN));

        genome.addEdge(new Edge(1, 4, 0.5, 1));
        genome.addEdge(new Edge(2, 4, -0.7, 2));
        genome.addEdge(new Edge(4, 3, 1.2, 3));

        genome.displayGenotype();
    }
}

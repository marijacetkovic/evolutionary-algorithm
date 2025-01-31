package io.github.neat;
import org.junit.jupiter.api.Test;

import static io.github.neat.NodeType.*;
import static org.junit.jupiter.api.Assertions.*;

class PropagationTest {
    @Test
    public void testPropagationSmall() {
        Genome genome = new Genome();

        Node node1 = new Node(1, INPUT, 1.0);
        Node node2 = new Node(2, INPUT, 2.0);
        Node node3 = new Node(3, OUTPUT, 0.0);
        Node node4 = new Node(4, HIDDEN, 0.0);

        genome.addNode(node1);
        genome.addNode(node2);
        genome.addNode(node3);
        genome.addNode(node4);

        Edge edge1 = new Edge(node1, node4, 0.5, 1);
        Edge edge2 = new Edge(node2, node4, -0.7, 2);
        Edge edge3 = new Edge(node4, node3, 1.2, 3);

        genome.addEdge(edge1);
        genome.addEdge(edge2);
        genome.addEdge(edge3);
        Individual i = new Individual(0, genome);

        i.makeDecision();

        double expectedHiddenNodeValue = 0.5 - 1.4;
        assertEquals(expectedHiddenNodeValue, node4.getActivationValue(), 0.001);
        //System.out.println(expectedHiddenNodeValue);
        double expectedOutputNodeValue = -0.9 * 1.2;
        //System.out.println(expectedOutputNodeValue);
        assertEquals(expectedOutputNodeValue, node3.getActivationValue(), 0.001);
    }
    @Test
    public void testPropagationMedium() {
        Genome genome = new Genome();

        Node node1 = new Node(1, INPUT, 1.0);
        Node node2 = new Node(2, INPUT, 2.0);
        Node node3 = new Node(3, OUTPUT, 0.0);
        Node node4 = new Node(4, OUTPUT, 0.0);
        Node node5 = new Node(5, HIDDEN, 0.0);
        Node node6 = new Node(6, HIDDEN, 0.0);
        Node node7 = new Node(7, HIDDEN, 0.0);

        genome.addNode(node1);
        genome.addNode(node2);
        genome.addNode(node3);
        genome.addNode(node4);
        genome.addNode(node5);
        genome.addNode(node6);
        genome.addNode(node7);

        Edge edge1 = new Edge(node1, node5, 0.5, 1);
        Edge edge2 = new Edge(node2, node5, -0.7, 2);
        Edge edge3 = new Edge(node1, node6, 0.3, 3);
        Edge edge4 = new Edge(node2, node6, -0.8, 4);
        Edge edge5 = new Edge(node1, node7, 0.4, 5);
        Edge edge6 = new Edge(node2, node7, 0.2, 6);

        Edge edge7 = new Edge(node5, node3, 1.2, 7);
        Edge edge8 = new Edge(node6, node3, 0.9, 8);
        Edge edge9 = new Edge(node7, node3, -1.1, 9);

        Edge edge10 = new Edge(node5, node4, -0.5, 10);
        Edge edge11 = new Edge(node6, node4, 1.3, 11);
        Edge edge12 = new Edge(node7, node4, 0.7, 12);

        genome.addEdge(edge1);
        genome.addEdge(edge2);
        genome.addEdge(edge3);
        genome.addEdge(edge4);
        genome.addEdge(edge5);
        genome.addEdge(edge6);
        genome.addEdge(edge7);
        genome.addEdge(edge8);
        genome.addEdge(edge9);
        genome.addEdge(edge10);
        genome.addEdge(edge11);
        genome.addEdge(edge12);

        Individual i = new Individual(0, genome);

        i.makeDecision();

        double expectedHiddenNodeValue1 = (1.0 * 0.5) + (2.0 * -0.7);
        double expectedHiddenNodeValue2 = (1.0 * 0.3) + (2.0 * -0.8);
        double expectedHiddenNodeValue3 = (1.0 * 0.4) + (2.0 * 0.2);

        assertEquals(expectedHiddenNodeValue1, node5.getActivationValue(), 0.001);
        assertEquals(expectedHiddenNodeValue2, node6.getActivationValue(), 0.001);
        assertEquals(expectedHiddenNodeValue3, node7.getActivationValue(), 0.001);

        double expectedOutputNodeValue1 = (node5.getActivationValue() * 1.2) + (node6.getActivationValue() * 0.9) + (node7.getActivationValue() * -1.1);
        double expectedOutputNodeValue2 = (node5.getActivationValue() * -0.5) + (node6.getActivationValue() * 1.3) + (node7.getActivationValue() * 0.7);
        //System.out.println(expectedOutputNodeValue1);
        assertEquals(expectedOutputNodeValue1, node3.getActivationValue(), 0.001);
        assertEquals(expectedOutputNodeValue2, node4.getActivationValue(), 0.001);
    }

}

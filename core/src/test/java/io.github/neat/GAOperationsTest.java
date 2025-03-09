package io.github.neat;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GAOperationsTest {

    @Test
    public void testAddEdgeMutation() {
        Genome genome = new Genome();
        Node node1 = new Node(1, NodeType.INPUT, 0.0);
        Node node2 = new Node(2, NodeType.HIDDEN, 0.0);
        genome.addNode(node1);
        genome.addNode(node2);

        Edge initialEdge = new Edge(node1, node2, 0.5, 0);
        genome.addEdge(initialEdge);


        GAOperations.addEdgeMutation(genome);
        assertTrue(genome.getEdgeGenes().size() > 1);
    }

    @Test
    public void testAddNodeMutation() {
        Genome genome = new Genome();
        Node node1 = new Node(1, NodeType.INPUT, 0.0);
        Node node2 = new Node(2, NodeType.HIDDEN, 0.0);
        genome.addNode(node1);
        genome.addNode(node2);

        int IN = INManager.getInstance().getInnovationID(node1,node2);
        Edge initialEdge = new Edge(node1, node2, 0.5, IN);
        genome.addEdge(initialEdge);

        GAOperations.addNodeMutation(genome);
        GAOperations.addNodeMutation(genome);
        GAOperations.addNodeMutation(genome);
        GAOperations.addNodeMutation(genome);

        assertTrue(genome.getEdgeGenes().size() > 5);

        assertTrue(genome.getNodeGenes().size() > 5);

        Edge newEdge = genome.getEdgeGenes().get(1);

        assertTrue(newEdge.getInnovationNumber() >= 0);
    }

    @Test
    public void testInnovationIdAssignment() {
        Genome genome = new Genome();
        Node node1 = new Node(1, NodeType.INPUT, 0.0);
        Node node2 = new Node(2, NodeType.HIDDEN, 0.0);
        genome.addNode(node1);
        genome.addNode(node2);

        int IN = INManager.getInstance().getInnovationID(node1,node2);
        Edge edge1 = new Edge(node1, node2, 0.5, IN);
        genome.addEdge(edge1);

        GAOperations.addEdgeMutation(genome);

        Edge newEdge = genome.getEdgeGenes().get(1);

        assertTrue(newEdge.getInnovationNumber() > 0);
    }


    @Test
    public void testCrossoverWithInnovationNumbers() {
        Node node1 = new Node(1, NodeType.INPUT, 0.0);
        Node node2 = new Node(2, NodeType.HIDDEN, 0.0);
        Node node3 = new Node(3, NodeType.OUTPUT, 0.0);
        Node node4 = new Node(4, NodeType.HIDDEN, 0.0);

        int IN1 = INManager.getInstance().getInnovationID(node1, node2);
        int IN2 = INManager.getInstance().getInnovationID(node2, node3);
        int IN3 = INManager.getInstance().getInnovationID(node1, node4);
        int IN4 = INManager.getInstance().getInnovationID(node4, node3);

        Edge edge1 = new Edge(node1, node2, 0.5, IN1);
        Edge edge2 = new Edge(node2, node3, 0.8, IN2);
        Edge edge3 = new Edge(node1, node4, 0.6, IN3);
        Edge edge4 = new Edge(node4, node3, 0.7, IN4);

        ArrayList<Node> parent1Nodes = new ArrayList<>();
        parent1Nodes.add(node1);
        parent1Nodes.add(node2);
        parent1Nodes.add(node3);

        ArrayList<Edge> parent1Edges = new ArrayList<>();
        parent1Edges.add(edge1);
        parent1Edges.add(edge2);

        Genome parent1 = new Genome(parent1Nodes, parent1Edges);
        parent1.setFitness(1.0);

        ArrayList<Node> parent2Nodes = new ArrayList<>();
        parent2Nodes.add(node1);
        parent2Nodes.add(node4);
        parent2Nodes.add(node3);

        ArrayList<Edge> parent2Edges = new ArrayList<>();
        parent2Edges.add(edge3);
        parent2Edges.add(edge4);

        Genome parent2 = new Genome(parent2Nodes, parent2Edges);
        parent2.setFitness(0.8);

        GAOperations.crossover(parent1, parent2);

        SpeciesManager speciesManager = SpeciesManager.getInstance();
        Genome child = speciesManager.getLastAddedGenome();

        assertNotNull(child, "Child genome should not be null");

        ArrayList<Node> childNodes = child.getNodeGenes();
        assertEquals(3, childNodes.size(), "Child should have 3 nodes");

        ArrayList<Edge> childEdges = child.getEdgeGenes();
        assertEquals(2, childEdges.size(), "Child should have 2 edges");

        assertTrue(childEdges.stream().anyMatch(e -> e.getInnovationNumber() == IN1),
            "Child should contain edge1 with IN1");
        assertTrue(childEdges.stream().anyMatch(e -> e.getInnovationNumber() == IN2),
            "Child should contain edge2 with IN2");

        assertFalse(childEdges.stream().anyMatch(e -> e.getInnovationNumber() == IN3),
            "Child should not contain edge3 with IN3");
        assertFalse(childEdges.stream().anyMatch(e -> e.getInnovationNumber() == IN4),
            "Child should not contain edge4 with IN4");

        for (Edge edge : childEdges) {
            assertTrue(edge.getInnovationNumber() >= 0, "Innovation number should be greater than 0");
        }
    }
}

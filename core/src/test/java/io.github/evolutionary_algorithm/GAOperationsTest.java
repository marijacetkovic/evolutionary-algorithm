package io.github.evolutionary_algorithm;
import org.junit.jupiter.api.Test;

import static io.github.evolutionary_algorithm.NodeType.*;
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

        assertTrue(newEdge.getInnovationNumber() > 0);
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

        assertFalse(newEdge.getInnovationNumber() > 0);
    }
}

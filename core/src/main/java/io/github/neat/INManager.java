package io.github.neat;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

//global innovation number manager
public class INManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static INManager instance;
    private final Map<String, Integer> edgeINMap = new HashMap<>();
    private final Map<Integer, Integer> nodeINMap = new HashMap<>();
    private int lastInnovationId = 0;
    private int lastNodeId = Config.startNodeId;



    private INManager() {
    }

    public static INManager getInstance() {
        if (instance == null) {
            //try {
              //  loadFromFile("inmanager_state.ser");
               // System.out.println("IMLOADED FROM FILEEE");
            //} catch (Exception e) {
            instance = new INManager();
            //}
        }
        return instance;
    }

    //edge innovation id
    public int getInnovationID(Node sourceNode, Node targetNode) {
        if (sourceNode == null || targetNode == null) {
            throw new IllegalArgumentException("Source and target nodes must not be null.");
        }

        String edgeKey = sourceNode.getId() + "-" + targetNode.getId();

        if (edgeINMap.containsKey(edgeKey)) {
            return edgeINMap.get(edgeKey);
        } else {
            edgeINMap.put(edgeKey, lastInnovationId);
            return lastInnovationId++;
        }
    }

    //assure everytime edge e is split, node connecting two new edges has same id
    public int getNodeID(int edgeIN){
        if (!nodeINMap.containsKey(edgeIN)){
            nodeINMap.put(edgeIN,lastNodeId++);
        }
        return nodeINMap.get(edgeIN);
    }
    public static void saveToFile(String filename){
        getInstance();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(instance);
            System.out.println(
                "Current lastInnovationId: " + instance.lastInnovationId +
                    " | lastNodeId: " + instance.lastNodeId
            );
            printEdgeInnovations();
            printNodeInnovations();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            instance = (INManager) in.readObject();
            System.out.println(
                "Current lastInnovationId: " + instance.lastInnovationId +
                    " | lastNodeId: " + instance.lastNodeId
            );
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printEdgeInnovations() {
        if (instance == null) {
            System.out.println("INManager not initialized - no edges to display.");
            return;
        }

        System.out.println("\n=== Edge Innovation Map Contents ===");
        System.out.println("Total edges: " + instance.edgeINMap.size());
        System.out.println("Last Innovation ID: " + instance.lastInnovationId);
        System.out.println("Last Node ID: " + instance.lastNodeId);
        System.out.println("----------------------------------");

        instance.edgeINMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .forEach(entry ->
                System.out.printf("Edge: %s → Innovation: %d\n",
                    entry.getKey(), entry.getValue())
            );

        System.out.println("==================================\n");
    }

    public static void printNodeInnovations() {
        if (instance == null) {
            System.out.println("INManager not initialized - no nodes to display.");
            return;
        }

        System.out.println("\n=== Node Innovation Map Contents ===");
        System.out.println("Total nodes created from splits: " + instance.nodeINMap.size());
        System.out.println("Last Node ID: " + instance.lastNodeId);
        System.out.println("----------------------------------");

        instance.nodeINMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry ->
                System.out.printf("Split Edge: %d → Assigned Node ID: %d\n",
                    entry.getKey(), entry.getValue())
            );

        System.out.println("==================================\n");
    }
}

package io.github.neat;

import java.util.HashMap;
import java.util.Map;

//global innovation number manager
public class INManager {
    private static INManager instance;
    private Map<String, Integer> edgeINMap = new HashMap<>();
    private Map<Integer, Integer> nodeINMap = new HashMap<>();

    private int lastInnovationId = 0;
    private int lastNodeId = Config.startNodeId;

    private INManager() {
    }

    public static INManager getInstance() {
        if (instance == null) {
            instance = new INManager();
        }
        return instance;
    }

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

}

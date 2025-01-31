package io.github.neat;

import java.util.HashMap;
import java.util.Map;

//global innovation number manager
public class INManager {
    private static INManager instance;
    private Map<String, Integer> INMap = new HashMap<>();
    private int lastInnovationId = 0;

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

        if (INMap.containsKey(edgeKey)) {
            return INMap.get(edgeKey);
        } else {
            INMap.put(edgeKey, lastInnovationId);
            return lastInnovationId++;
        }
    }

}

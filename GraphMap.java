import java.util.*;

public class GraphMap {
    private Map<String, List<String>> adjacencyList;

    public GraphMap() {
        adjacencyList = new HashMap<>();
    }

    public void addEdge(String from, String to) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.putIfAbsent(to, new ArrayList<>());
        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from); // Assuming undirected graph
    }

    public List<String> getNeighbors(String node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }
}
import java.util.*;

public class InvestigationGraph {
    private Map<String, List<InvestigationNode>> adjacencyList;
    private Map<String, InvestigationNode> nodes;

    public InvestigationGraph() {
        adjacencyList = new HashMap<>();
        nodes = new HashMap<>();
    }

    // Node class to store location information and evidence
    public static class InvestigationNode {
        String locationName;
        String evidence;
        int reliabilityScore; // Higher score means more reliable evidence
        boolean isVisited;
        Set<String> rumors; // Stores rumors heard at this location

        public InvestigationNode(String locationName, String evidence, int reliabilityScore) {
            this.locationName = locationName;
            this.evidence = evidence;
            this.reliabilityScore = reliabilityScore;
            this.isVisited = false;
            this.rumors = new HashSet<>();
        }
    }

    // Add a new location to the investigation
    public void addLocation(String locationName, String evidence, int reliabilityScore) {
        InvestigationNode node = new InvestigationNode(locationName, evidence, reliabilityScore);
        nodes.put(locationName, node);
        adjacencyList.putIfAbsent(locationName, new ArrayList<>());
    }

    // Connect two locations in the investigation map
    public void connectLocations(String location1, String location2) {
        if (!nodes.containsKey(location1) || !nodes.containsKey(location2)) {
            throw new IllegalArgumentException("Lokasi Keduanya harus ada di dalam graph");
        }
        adjacencyList.get(location1).add(nodes.get(location2));
        adjacencyList.get(location2).add(nodes.get(location1));
    }

    // BFS to find all connected evidence within certain steps
    public Map<String, Integer> findConnectedEvidence(String startLocation, int maxSteps) {
        Map<String, Integer> evidenceDistances = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> steps = new HashMap<>();
        
        queue.offer(startLocation);
        steps.put(startLocation, 0);
        evidenceDistances.put(nodes.get(startLocation).evidence, 0);

        while (!queue.isEmpty()) {
            String currentLocation = queue.poll();
            int currentSteps = steps.get(currentLocation);

            if (currentSteps < maxSteps) {
                for (InvestigationNode neighbor : adjacencyList.get(currentLocation)) {
                    if (!steps.containsKey(neighbor.locationName)) {
                        queue.offer(neighbor.locationName);
                        steps.put(neighbor.locationName, currentSteps + 1);
                        evidenceDistances.put(neighbor.evidence, currentSteps + 1);
                    }
                }
            }
        }
        return evidenceDistances;
    }

    // BFS to propagate rumors through the network
    public void propagateRumors(String startLocation, String rumor, int maxSpread) {
        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> spreadDistance = new HashMap<>();
        
        queue.offer(startLocation);
        spreadDistance.put(startLocation, 0);
        nodes.get(startLocation).rumors.add(rumor);

        while (!queue.isEmpty()) {
            String currentLocation = queue.poll();
            int currentSpread = spreadDistance.get(currentLocation);

            if (currentSpread < maxSpread) {
                for (InvestigationNode neighbor : adjacencyList.get(currentLocation)) {
                    if (!spreadDistance.containsKey(neighbor.locationName)) {
                        queue.offer(neighbor.locationName);
                        spreadDistance.put(neighbor.locationName, currentSpread + 1);
                        // Rumors become less reliable as they spread
                        String modifiedRumor = modifyRumorByDistance(rumor, currentSpread + 1);
                        neighbor.rumors.add(modifiedRumor);
                    }
                }
            }
        }
    }

    // Helper method to modify rumors based on distance from source
    private String modifyRumorByDistance(String rumor, int distance) {
        // The further the rumor travels, the more uncertain it becomes
        if (distance == 1) {
            return "Saya mendengar " + rumor;
        } else if (distance == 2) {
            return "Seseorang menyebutkan " + rumor;
        } else {
            return "Ada rumor yang tidak jelas " + rumor;
        }
    }

    // BFS to find shortest investigation path
    public List<String> findInvestigationPath(String startLocation, String targetLocation) {
        Queue<String> queue = new LinkedList<>();
        Map<String, String> previousLocation = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.offer(startLocation);
        visited.add(startLocation);

        while (!queue.isEmpty()) {
            String currentLocation = queue.poll();

            if (currentLocation.equals(targetLocation)) {
                return reconstructPath(previousLocation, startLocation, targetLocation);
            }

            for (InvestigationNode neighbor : adjacencyList.get(currentLocation)) {
                if (!visited.contains(neighbor.locationName)) {
                    visited.add(neighbor.locationName);
                    previousLocation.put(neighbor.locationName, currentLocation);
                    queue.offer(neighbor.locationName);
                }
            }
        }
        return new ArrayList<>();
    }

    // Helper method to reconstruct the investigation path
    private List<String> reconstructPath(Map<String, String> previousLocation, String start, String target) {
        List<String> path = new ArrayList<>();
        String current = target;

        while (current != null) {
            path.add(0, current);
            current = previousLocation.get(current);
        }
        return path;
    }

    // Get all rumors at a location
    public Set<String> getRumorsAtLocation(String location) {
        return nodes.get(location).rumors;
    }

    // Get reliability score at a location
    public int getReliabilityScore(String location) {
        return nodes.get(location).reliabilityScore;
    }

    // Mark a location as visited
    public void markVisited(String location) {
        if (nodes.containsKey(location)) {
            nodes.get(location).isVisited = true;
        }
    }

    // Check if a location has been visited
    public boolean isVisited(String location) {
        return nodes.containsKey(location) && nodes.get(location).isVisited;
    }
}
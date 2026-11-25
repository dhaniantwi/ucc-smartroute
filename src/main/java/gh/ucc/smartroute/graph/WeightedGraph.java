package gh.ucc.smartroute.graph;

import gh.ucc.smartroute.model.Location;
import gh.ucc.smartroute.model.Route;

import java.util.*;

public final class WeightedGraph {
    public record Edge(String destinationId, double weight) {
        public Edge {
            if (destinationId == null || destinationId.isBlank()) throw new IllegalArgumentException("Destination id is required");
            if (!Double.isFinite(weight) || weight <= 0) throw new IllegalArgumentException("Edge weight must be positive");
        }
    }

    private final Map<String, Location> vertices = new LinkedHashMap<>();
    private final Map<String, List<Edge>> adjacency = new LinkedHashMap<>();

    public void addLocation(Location location) {
        Objects.requireNonNull(location, "location");
        if (vertices.putIfAbsent(location.id(), location) != null) {
            throw new IllegalArgumentException("Duplicate location id: " + location.id());
        }
        adjacency.put(location.id(), new ArrayList<>());
    }

    public void addConnection(String fromId, String toId, double distanceMetres) {
        requireVertex(fromId);
        requireVertex(toId);
        if (fromId.equals(toId)) throw new IllegalArgumentException("A location cannot connect to itself");
        addDirectedEdge(fromId, toId, distanceMetres);
        addDirectedEdge(toId, fromId, distanceMetres);
    }

    private void addDirectedEdge(String from, String to, double weight) {
        List<Edge> edges = adjacency.get(from);
        if (edges.stream().anyMatch(edge -> edge.destinationId().equals(to))) {
            throw new IllegalArgumentException("Connection already exists: " + from + " -> " + to);
        }
        edges.add(new Edge(to, weight));
    }

    public Location getLocation(String id) {
        requireVertex(id);
        return vertices.get(id);
    }

    public Collection<Location> getLocations() {
        return Collections.unmodifiableCollection(vertices.values());
    }

    public List<Edge> getNeighbours(String id) {
        requireVertex(id);
        return Collections.unmodifiableList(adjacency.get(id));
    }

    public int size() {
        return vertices.size();
    }

    public Optional<Route> shortestPath(String startId, String endId) {
        requireVertex(startId);
        requireVertex(endId);
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingDouble(NodeDistance::distance));

        for (String id : vertices.keySet()) distances.put(id, Double.POSITIVE_INFINITY);
        distances.put(startId, 0.0);
        queue.add(new NodeDistance(startId, 0.0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            if (current.distance() > distances.get(current.id())) continue;
            if (current.id().equals(endId)) break;

            for (Edge edge : adjacency.get(current.id())) {
                double candidate = current.distance() + edge.weight();
                if (candidate < distances.get(edge.destinationId())) {
                    distances.put(edge.destinationId(), candidate);
                    previous.put(edge.destinationId(), current.id());
                    queue.add(new NodeDistance(edge.destinationId(), candidate));
                }
            }
        }

        if (Double.isInfinite(distances.get(endId))) return Optional.empty();

        LinkedList<Location> path = new LinkedList<>();
        String cursor = endId;
        path.addFirst(vertices.get(cursor));
        while (!cursor.equals(startId)) {
            cursor = previous.get(cursor);
            path.addFirst(vertices.get(cursor));
        }
        return Optional.of(new Route(path, distances.get(endId)));
    }

    public List<Location> breadthFirstSearch(String startId) {
        requireVertex(startId);
        List<Location> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        visited.add(startId);
        queue.add(startId);
        while (!queue.isEmpty()) {
            String current = queue.remove();
            order.add(vertices.get(current));
            for (Edge edge : adjacency.get(current)) {
                if (visited.add(edge.destinationId())) queue.add(edge.destinationId());
            }
        }
        return List.copyOf(order);
    }

    public List<Location> depthFirstSearch(String startId) {
        requireVertex(startId);
        List<Location> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(startId);
        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (!visited.add(current)) continue;
            order.add(vertices.get(current));
            List<Edge> edges = adjacency.get(current);
            for (int i = edges.size() - 1; i >= 0; i--) {
                if (!visited.contains(edges.get(i).destinationId())) stack.push(edges.get(i).destinationId());
            }
        }
        return List.copyOf(order);
    }

    private void requireVertex(String id) {
        if (!vertices.containsKey(id)) throw new IllegalArgumentException("Unknown location id: " + id);
    }

    private record NodeDistance(String id, double distance) {}
}

package gh.ucc.smartroute.graph;

import gh.ucc.smartroute.model.Location;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeightedGraphTest {
    private WeightedGraph sampleGraph() {
        WeightedGraph graph = new WeightedGraph();
        graph.addLocation(new Location("A", "Alpha", 5.0, -1.0));
        graph.addLocation(new Location("B", "Bravo", 5.1, -1.1));
        graph.addLocation(new Location("C", "Charlie", 5.2, -1.2));
        graph.addLocation(new Location("D", "Delta", 5.3, -1.3));
        graph.addConnection("A", "B", 100);
        graph.addConnection("B", "C", 120);
        graph.addConnection("A", "C", 300);
        return graph;
    }

    @Test
    void dijkstraFindsMinimumDistancePath() {
        var route = sampleGraph().shortestPath("A", "C").orElseThrow();
        assertEquals(List.of("A", "B", "C"), route.locations().stream().map(Location::id).toList());
        assertEquals(220.0, route.totalDistanceMetres());
    }

    @Test
    void dijkstraReturnsEmptyWhenNoPathExists() {
        assertTrue(sampleGraph().shortestPath("A", "D").isEmpty());
    }

    @Test
    void bfsVisitsReachableVerticesInBreadthFirstOrder() {
        assertEquals(List.of("A", "B", "C"),
                sampleGraph().breadthFirstSearch("A").stream().map(Location::id).toList());
    }

    @Test
    void dfsVisitsReachableVertices() {
        var visited = sampleGraph().depthFirstSearch("A").stream().map(Location::id).toList();
        assertEquals(3, visited.size());
        assertTrue(visited.containsAll(List.of("A", "B", "C")));
    }

    @Test
    void connectionIsBidirectional() {
        assertEquals(1, sampleGraph().getNeighbours("B").stream()
                .filter(edge -> edge.destinationId().equals("A")).count());
    }

    @Test
    void rejectsNonPositiveDistance() {
        WeightedGraph graph = sampleGraph();
        assertThrows(IllegalArgumentException.class, () -> graph.addConnection("A", "D", 0));
    }
}

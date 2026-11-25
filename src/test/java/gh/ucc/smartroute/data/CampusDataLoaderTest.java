package gh.ucc.smartroute.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CampusDataLoaderTest {
    @Test
    void loadsCampusReferenceMarkersWithoutInventingConnections() throws Exception {
        var graph = CampusDataLoader.loadFromResources();

        assertEquals(3, graph.size());
        assertTrue(graph.shortestPath("llt", "new-exam-centre").isEmpty());
    }
}

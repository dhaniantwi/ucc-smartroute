package gh.ucc.smartroute.data;

import gh.ucc.smartroute.graph.WeightedGraph;
import gh.ucc.smartroute.model.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class CampusDataLoader {
    private CampusDataLoader() {}

    public static WeightedGraph loadFromResources() throws IOException {
        WeightedGraph graph = new WeightedGraph();
        try (BufferedReader reader = reader("/data/locations.csv")) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != 4) throw new IOException("Invalid locations.csv row: " + line);
                try {
                    graph.addLocation(new Location(parts[0].trim(), parts[1].trim(),
                            Double.parseDouble(parts[2].trim()), Double.parseDouble(parts[3].trim())));
                } catch (RuntimeException exception) {
                    throw new IOException("Invalid location row: " + line, exception);
                }
            }
        }

        try (BufferedReader reader = reader("/data/connections.csv")) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != 3) throw new IOException("Invalid connections.csv row: " + line);
                try {
                    graph.addConnection(parts[0].trim(), parts[1].trim(), Double.parseDouble(parts[2].trim()));
                } catch (RuntimeException exception) {
                    throw new IOException("Invalid connection row: " + line, exception);
                }
            }
        }
        return graph;
    }

    private static BufferedReader reader(String resource) throws IOException {
        InputStream stream = CampusDataLoader.class.getResourceAsStream(resource);
        if (stream == null) throw new IOException("Missing resource: " + resource);
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }
}

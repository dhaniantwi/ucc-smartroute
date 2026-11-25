# UCC SmartRoute — Group 28

A Java desktop prototype demonstrating how a **weighted graph** can represent campus locations and pedestrian connections to support route finding at the University of Cape Coast.

## Current status

The initial project foundation contains:
- An adjacency-list weighted graph.
- Dijkstra's shortest-path algorithm.
- Breadth-first search (BFS) and depth-first search (DFS) traversal.
- A JavaFX desktop interface with an OpenStreetMap/Leaflet map.
- A CSV-based campus data format.
- JUnit tests using a separate, synthetic graph.

**Important data-integrity note:** the initial campus CSV contains a few map markers only. The pedestrian-edge file intentionally contains no connections yet. We will not invent walkways or distances. A route between campus places will become available only after the team verifies the pedestrian connection and its distance.

## Requirements

- JDK 17 or newer
- Maven 3.9+
- Internet connection when displaying the Leaflet/OpenStreetMap map tiles

## Run

```bash
mvn clean test
mvn javafx:run
```

If your machine does not recognize `mvn`, install Maven or use the Maven Wrapper once it has been added.

## Graph model

- **Vertex:** a campus place, identified by a unique ID and holding its name and geographic coordinates.
- **Weighted edge:** a verified walkable connection between two vertices. Weight is walking distance in metres.
- **Representation:** adjacency list, suitable for a sparse campus network.
- **Shortest route:** Dijkstra's algorithm with a priority queue.
- **Traversal demonstrations:** BFS and DFS.

## Data files

- `src/main/resources/data/locations.csv` — campus map markers.
- `src/main/resources/data/connections.csv` — verified pedestrian connections; currently header-only.

CSV connection format:

`from_id,to_id,distance_m`

Each connection is treated as bidirectional by the loader. Add only connections the team has checked on the ground or against a reliable campus map, and record the evidence in the project report.

## Team contribution workflow

1. Create a branch for each task.
2. Make focused commits.
3. Open a pull request to `main`.
4. Ask at least one teammate to review before merging.
5. Do not change campus connection data without verification.

Suggested workstreams for the 10-person team:
- Members 1–2: graph model and data structures.
- Members 3–4: shortest-path and traversal algorithms.
- Members 5–6: JavaFX interface and map.
- Members 7–8: campus data collection and integration.
- Members 9–10: testing, documentation and presentation.

## Map attribution

Map display uses Leaflet and OpenStreetMap tiles. OpenStreetMap data is © OpenStreetMap contributors. See https://www.openstreetmap.org/copyright.

## Academic note

This is a student learning project. The algorithms are implemented in this repository for demonstration and should be understood and explained by the group.

# Campus data collection and verification

## Why this matters

A graph route is only as trustworthy as its edges. A marker coordinate identifies a place; it does not prove that two places are joined by a pedestrian walkway. SmartRoute therefore keeps map markers separate from verified walking connections.

## Adding a location

Add one row to `src/main/resources/data/locations.csv`:

```csv
id,name,latitude,longitude
```

Use a stable, unique ID. Check coordinates against a reliable map or a GPS reading. Record the source and date in the team's evidence log.

## Adding a pedestrian connection

Add one row to `src/main/resources/data/connections.csv`:

```csv
from_id,to_id,distance_m
```

The loader creates an undirected edge, so one row represents a walkable connection in both directions. The distance must be a positive number of metres. Do not add a connection merely because two markers look close on the map.

## Evidence log template

| From | To | Distance (m) | How verified | Checked by | Date |
|---|---|---:|---|---|---|
| | | | Walked and measured / reliable map | | |

## Limitations to disclose in the presentation

- Initial coordinates are reference markers, not a complete survey of UCC.
- The initial pedestrian connections dataset is intentionally empty pending verification.
- OpenStreetMap is used for the basemap; it does not automatically provide a complete UCC pedestrian network in this prototype.
- Dijkstra's algorithm is tested on a synthetic graph independently of campus data.

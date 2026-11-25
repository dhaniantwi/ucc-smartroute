package gh.ucc.smartroute.model;

import java.util.List;

public record Route(List<Location> locations, double totalDistanceMetres) {
    public Route {
        locations = List.copyOf(locations);
        if (locations.isEmpty()) throw new IllegalArgumentException("A route must contain at least one location");
        if (!Double.isFinite(totalDistanceMetres) || totalDistanceMetres < 0) {
            throw new IllegalArgumentException("Route distance must be a non-negative finite value");
        }
    }
}

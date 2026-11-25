package gh.ucc.smartroute.model;

import java.util.Objects;

public record Location(String id, String name, double latitude, double longitude) {
    public Location {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Location id is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Location name is required");
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) throw new IllegalArgumentException("Invalid latitude");
        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) throw new IllegalArgumentException("Invalid longitude");
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Location location && id.equals(location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

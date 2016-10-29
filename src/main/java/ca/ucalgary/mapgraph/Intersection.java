package ca.ucalgary.mapgraph;

import ca.ucalgary.ui.ZoneBoundary;

import java.awt.geom.Path2D;
import java.util.Set;

public final class Intersection {
    private static int counter = 0;
    private final int id = counter++;

    public static Intersection SOURCE = new Intersection(0, 0);
    public static Intersection TARGET = new Intersection(1000, 1000);

    private final double lon;
    private final double lat;

    public boolean noDemand = false;

    public Intersection(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        // return String.format("[%f, %f]", lon, lat);
        return "[" + id + "]";
    }

    public boolean isInZones(Set<ZoneBoundary> zones) {
        for (ZoneBoundary zb : zones) {
            if (zb.contains(this))
                return true;
        }

        return false;
    }

    public boolean isSource() {
        return this == SOURCE;
    }

    public boolean isTarget() {
        return this == TARGET;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Intersection that = (Intersection) o;

        if (Double.compare(that.lon, lon) != 0) return false;
        return Double.compare(that.lat, lat) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lon);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

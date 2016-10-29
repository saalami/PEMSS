package ca.ucalgary.mapgraph;

import ca.ucalgary.Util;

import java.util.ArrayList;
import java.util.Iterator;

public final class Way {
    private ArrayList<Intersection> intersections = new ArrayList<>();
    WayType type = WayType.OTHER;

    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }

    public static Way connector(Intersection u, Intersection v) {
        Way connector = new Way();
        connector.setType(WayType.CONNECTOR);
        connector.addIntersection(u);
        connector.addIntersection(v);
        return connector;
    }

    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
    }

    public void setType(String type) {

        try {
            this.type = WayType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.type = WayType.OTHER;
            System.out.println(e.getMessage());
        }

    }

    public void setType(WayType type) {
        this.type = type;
    }

    public WayType getType() {
        return type;
    }

    public double getWayLength() {
        double sumLength = 0;

        for (int i = 0; i < intersections.size() - 1; i++) {
            Intersection int1 = intersections.get(i);
            Intersection int2 = intersections.get(i);
            sumLength += Util.dist(int1, int2);
        }

        return sumLength;
    }

    public Iterable<Edge> edges() {
        return () -> new EdgeIterator();
    }

    private class EdgeIterator implements Iterator<Edge> {
        int i = 1;

        @Override
        public boolean hasNext() {
            return i < intersections.size();
        }

        @Override
        public Edge next() {
            return new Edge(intersections.get(i - 1), intersections.get(i++), Way.this);
        }
    }

    public static class Edge {
        Intersection start, end;
        Way way;

        public Edge(Intersection u, Intersection v, Way way) {
            this.way = way;
            start = u;
            end = v;
        }

        public Edge reverse() {
            return new Edge(end, start, way);
        }

        public Intersection getStart() {
            return start;
        }

        public Intersection getEnd() {
            return end;
        }

        public double getCapacity() {
            return way.getType().getCapacity();
        }

        public double length() {
            return Util.latLonDistanceToMeter(start.getLat(), start.getLon(), end.getLat(), end.getLon());
        }

        @Override
        public String toString() {
            return start + " -> " + end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;

            Edge edge = (Edge) o;

            if (!start.equals(edge.start)) return false;
            return end.equals(edge.end);

        }

        @Override
        public int hashCode() {
            int result = start.hashCode();
            result = 31 * result + end.hashCode();
            return result;
        }
    }
}

package ca.ucalgary.mapgraph;

import ca.ucalgary.Util;
import com.google.common.collect.Iterables;

import java.awt.geom.Rectangle2D;
import java.util.*;

public class MapGraph {
    private Collection<Intersection> intersections;
    private ArrayList<Way> ways = new ArrayList<>(1024);

    private Rectangle2D.Double bounds;

    public ArrayList<Way> getWays() {
        return ways;
    }

    public Rectangle2D.Double getBounds() {
        return bounds;
    }

    public Collection<Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(Collection<Intersection> intersections) {
        this.intersections = intersections;
    }

    public void setBounds(Rectangle2D.Double bounds) {
        this.bounds = bounds;
    }

    public void addWay(Way way) {
        ways.add(way);
    }

    public Iterable<Intersection> intersections() {
        return intersections;
    }

    public Iterable<Way.Edge> edges() {
        Optional<Iterable<Way.Edge>> edges = ways.stream().map(Way::edges).reduce(Iterables::concat);
        return edges.get();
    }

    public MapGraph trim(int minCapacity) {
        MapGraph trimmed = new MapGraph();
        HashSet<Intersection> trimmedIntersections = new HashSet<>();
        trimmed.bounds = this.bounds;
        ways.forEach(w -> {
            if (w.getType().getCapacity() >= minCapacity) {
                trimmed.ways.add(w);
                w.getIntersections().forEach(trimmedIntersections::add);
            }
        });

        trimmed.setIntersections(trimmedIntersections);

        System.out.printf("Was: %s, Become: %s%n", this.intersections.size(), trimmedIntersections.size());

        return trimmed;
    }

}

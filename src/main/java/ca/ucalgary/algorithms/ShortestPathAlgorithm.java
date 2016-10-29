package ca.ucalgary.algorithms;

import ca.ucalgary.Util;
import ca.ucalgary.mapgraph.Intersection;
import ca.ucalgary.mapgraph.MapGraph;
import ca.ucalgary.mapgraph.Way;
import ca.ucalgary.ui.ZoneBoundary;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

import static ca.ucalgary.mapgraph.Intersection.TARGET;

public class ShortestPathAlgorithm implements EvacuationAlgorithm {

    @Override
    public HashMap<Intersection, Distributor> solve(MapGraph mapGraph, Set<ZoneBoundary> hotZones, Set<ZoneBoundary> safeZones) {

        double zeroLength = 1;

        Collection<Intersection> intersections = mapGraph.getIntersections();

        SimpleWeightedGraph<Intersection, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        g.addVertex(TARGET);
        intersections.forEach(g::addVertex);

        for (Way way : mapGraph.getWays()) {
            ArrayList<Intersection> wayInts = way.getIntersections();
            for (int i = 1; i < wayInts.size(); i++) {
                Intersection u = wayInts.get(i - 1);
                Intersection v = wayInts.get(i);
                try {
                    DefaultWeightedEdge e1 = g.addEdge(u, v);
                    double length = Util.latLonDistanceToMeter(u.getLat(), u.getLon(), v.getLat(), v.getLon());

                    if (e1 != null) {
                        g.setEdgeWeight(e1, length);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        intersections.stream().filter(intr -> intr.isInZones(safeZones)).forEach(intr -> {
            DefaultWeightedEdge e = g.addEdge(intr, TARGET);
            g.setEdgeWeight(e, zeroLength);
        });

//        FloydWarshallShortestPaths<Intersection, DefaultWeightedEdge> shortestPath = new FloydWarshallShortestPaths<>(g);
//        List<GraphPath<Intersection, DefaultWeightedEdge>> spList = shortestPath.getShortestPaths(TARGET);


        HashMap<Intersection, Distributor> distMap = new HashMap<>();

        mapGraph.intersections().forEach(intr -> {
            if (intr.isInZones(hotZones)) {
                System.out.println(intr);
                DijkstraShortestPath<Intersection, DefaultWeightedEdge> sp = new DijkstraShortestPath<>(g, intr, TARGET);
                GraphPath<Intersection, DefaultWeightedEdge> path = sp.getPath();
                if (path == null) {
                    System.out.printf("No route to TARGET for %s%n", intr);
                } else {
                    System.out.println("Non-null path: " + path);
                    addPathToDistributors(g, distMap, path);
                }
            }
        });

        return distMap;
    }

    private void addPathToDistributors(SimpleWeightedGraph<Intersection, DefaultWeightedEdge> g, HashMap<Intersection, Distributor> distMap, GraphPath<Intersection, DefaultWeightedEdge> path) {
        Intersection cur = path.getStartVertex();

        for (DefaultWeightedEdge edge:path.getEdgeList()) {
            Intersection u = g.getEdgeSource(edge);
            Intersection v = g.getEdgeTarget(edge);
            if (cur == u) {
                distMap.put(u, new SingleDestinationDistributor(v));
                cur = v;
            } else {
                distMap.put(v, new SingleDestinationDistributor(u));
                cur = u;
            }
        }
    }

}

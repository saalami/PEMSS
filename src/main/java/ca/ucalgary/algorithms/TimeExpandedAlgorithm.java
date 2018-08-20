package ca.ucalgary.algorithms;

import ca.ucalgary.Util;
import ca.ucalgary.mapgraph.Intersection;
import ca.ucalgary.mapgraph.MapGraph;
import ca.ucalgary.mapgraph.Way;
import ca.ucalgary.ui.ZoneBoundary;
import org.jgrapht.alg.flow.MaximumFlowAlgorithmBase;
import org.jgrapht.alg.flow.PushRelabelMFImpl;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ca.ucalgary.mapgraph.Intersection.SOURCE;
import static ca.ucalgary.mapgraph.Intersection.TARGET;

public class TimeExpandedAlgorithm implements EvacuationAlgorithm {
    int i = 0;
    private HashMap<Intersection, TIDistributor> distMapCache;

    @Override
    public HashMap<Intersection, TIDistributor> solve(MapGraph mapGraph, Set<ZoneBoundary> hotZones, Set<ZoneBoundary> safeZones) {

        if (distMapCache != null)
            return distMapCache;

        double infCap = 100000;
        int timespan = 1500;

        Collection<Intersection> intersections = mapGraph.getIntersections();
        SimpleDirectedWeightedGraph<TI, DefaultWeightedEdge> g = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        System.out.println("Adding source and target");
        g.addVertex(TI.SOURCE);
        g.addVertex(TI.TARGET);

        System.out.println("Adding intersections");
        intersections.forEach(intr -> {
            IntStream.range(0, timespan).forEach(t -> g.addVertex(new TI(intr, t)));
        });

        System.out.println("Creating the time expanded network ...");
        for (Way way : mapGraph.getWays()) {
            ArrayList<Intersection> wayInts = way.getIntersections();
            for (int i = 1; i < wayInts.size(); i++) {
                Intersection u = wayInts.get(i - 1);
                Intersection v = wayInts.get(i);

                int len = Math.max(1, (int) Util.dist(u, v));

                System.out.println("len = " + len);

                for (int t = 0; t < timespan - len; t++) {
                    double capacity = way.getType().getCapacity();

                    DefaultWeightedEdge e1 = g.addEdge(new TI(u, t), new TI(v, t + len));
                    DefaultWeightedEdge e2 = g.addEdge(new TI(v, t), new TI(u, t + len));

                    if (e1 != null) {
                        g.setEdgeWeight(e1, capacity);
                    }

                    if (e2 != null) {
                        g.setEdgeWeight(e2, capacity);
                    }
                }

            }
        }

        System.out.printf("Adding %s intersections ...%n", intersections.size());

        i = 0;
        intersections.parallelStream().forEach((intersection) -> {
            if (i % 1000 == 0) {
                System.out.println("intersections: " + i);
            }
            i++;
            if (intersection.isInZones(safeZones)) {
                for (int t = 0; t < timespan; t++) {
                    synchronized (g) {
                        DefaultWeightedEdge e = g.addEdge(new TI(intersection, t), TI.TARGET);
                        g.setEdgeWeight(e, infCap);
                    }
                }
            } else if (intersection.isInZones(hotZones)) {
                for (int t = 0; t < timespan; t++) {
                    synchronized (g) {
                        DefaultWeightedEdge e = g.addEdge(TI.SOURCE, new TI(intersection, t));
                        g.setEdgeWeight(e, infCap);
                    }
                }
            }
        });

        System.out.println("Finding max flow in time expanded network");

        MaximumFlowAlgorithmBase<TI, DefaultWeightedEdge> maxFlow = new PushRelabelMFImpl<>(g, 0.1);
        maxFlow.calculateMaximumFlow(TI.SOURCE, TI.TARGET);

        System.out.printf("max flow value = %s%n", maxFlow.getMaximumFlowValue());

        Map<DefaultWeightedEdge, Double> linksFlow = maxFlow.getFlowMap();
        HashMap<Intersection, TIDistributor> distMap = new HashMap<>();

        linksFlow.entrySet().parallelStream().forEach(e -> {
            Double flow = e.getValue();
            if (flow > 0.0) {
                DefaultWeightedEdge edge = e.getKey();
                TI u = g.getEdgeSource(edge);
                TI v = g.getEdgeTarget(edge);

                TIDistributor tiDistributor = distMap.get(u.getIntersection());
                if (tiDistributor == null) {
                    tiDistributor = new TIDistributor(timespan);
                    distMap.put(u.getIntersection(), tiDistributor);
                }

                tiDistributor.set(u.getTime(), v.getIntersection(), flow);
                System.out.printf("Non-zero flow %s %n", flow);
            } else {
                // System.out.println("Zero flow: " + e);
            }
        });

        distMapCache = distMap;

        return distMap;
    }
}

package ca.ucalgary.algorithms;

import ca.ucalgary.mapgraph.Intersection;
import static ca.ucalgary.mapgraph.Intersection.*;
import ca.ucalgary.mapgraph.MapGraph;
import ca.ucalgary.mapgraph.Way;
import ca.ucalgary.ui.ZoneBoundary;
import org.jgrapht.alg.EdmondsKarpMaximumFlow;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;
import java.util.stream.Stream;

public class MaxFlowAlgorithm implements EvacuationAlgorithm {

    @Override
    public HashMap<Intersection, ProportionalLoadDistributor> solve(MapGraph mapGraph, Set<ZoneBoundary> hotZones, Set<ZoneBoundary> safeZones) {
        double infCap = 100000;

        Collection<Intersection> intersections = mapGraph.getIntersections();
        SimpleDirectedWeightedGraph<Intersection, DefaultWeightedEdge> g = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

        g.addVertex(SOURCE);
        g.addVertex(TARGET);
        intersections.forEach(g::addVertex);

        for (Way way : mapGraph.getWays()) {
            double capacity = way.getType().getCapacity();
            ArrayList<Intersection> wayInts = way.getIntersections();

            for (int i = 1; i < wayInts.size(); i++) {
                Intersection u = wayInts.get(i - 1);
                Intersection v = wayInts.get(i);

                try {
                    DefaultWeightedEdge e1 = g.addEdge(u, v);
                    if (e1 != null) {
                        g.setEdgeWeight(e1, capacity);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e);
                }

                try {
                    DefaultWeightedEdge e2 = g.addEdge(v, u);
                    if (e2 != null) {
                        g.setEdgeWeight(e2, capacity);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e);
                }
            }
        }

        for (Intersection intersection : intersections) {
            if (intersection.isInZones(safeZones)) {
                DefaultWeightedEdge e = g.addEdge(intersection, TARGET);
                g.setEdgeWeight(e, infCap);
            } else if (intersection.isInZones(hotZones)) {
                DefaultWeightedEdge e = g.addEdge(SOURCE, intersection);
                g.setEdgeWeight(e, infCap);
            }
        }

        EdmondsKarpMaximumFlow<Intersection, DefaultWeightedEdge> maxFlow = new EdmondsKarpMaximumFlow<>(g, 0.001);
        maxFlow.calculateMaximumFlow(SOURCE, TARGET);

        Map<DefaultWeightedEdge, Double> linksFlow = maxFlow.getMaximumFlow();
        Map<Intersection, List<Map.Entry<DefaultWeightedEdge, Double>>> grouped = new HashMap<>();

        linksFlow.entrySet().forEach(e -> {
            Intersection from = g.getEdgeSource(e.getKey());
            List<Map.Entry<DefaultWeightedEdge, Double>> list = grouped.getOrDefault(from, new ArrayList<>());
            list.add(e);
            grouped.put(from, list);
        });


        HashMap<Intersection, ProportionalLoadDistributor> distMap = new HashMap<>();

        grouped.entrySet().forEach(group -> {
            Intersection from = group.getKey();
            int n = group.getValue().size();
            Intersection tos[] = getTos(group.getValue().stream().map(Map.Entry::getKey), g);
            double[] flows = group.getValue().stream().mapToDouble(Map.Entry::getValue).toArray();
            if (flows.length > 0) {
                distMap.put(from, new ProportionalLoadDistributor(tos, flows));
            }
        });

        return distMap;
    }

    private Intersection[] getTos(Stream<DefaultWeightedEdge> stream, SimpleDirectedWeightedGraph<Intersection, DefaultWeightedEdge> g) {
        Object[] arr = stream.toArray();
        Intersection ends[] = new Intersection[arr.length];

        for (int i = 0; i < arr.length; i++) {
            Intersection end = g.getEdgeTarget((DefaultWeightedEdge) arr[i]);
            ends[i] = end;
        }

        return ends;
    }
}

package ca.ucalgary.sim;

import ca.ucalgary.algorithms.Distributor;
import ca.ucalgary.mapgraph.Intersection;
import ca.ucalgary.mapgraph.MapGraph;
import ca.ucalgary.mapgraph.Way;
import ca.ucalgary.ui.ZoneBoundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Simulator {

    public static class SimulationResult {
        private int time = 0;
        private int totalTravelTime = 0;
        private int totalDemand = 0;
        private int totalServed = 0;

        private SimulationResult(){}

        public int getTime() {
            return time;
        }

        public int getTotalTravelTime() {
            return totalTravelTime;
        }

        public int getTotalDemand() {
            return totalDemand;
        }

        public int getTotalServed() {
            return totalServed;
        }

        @Override
        public String toString() {
            return String.format(
                    "Time: %s, Total Travel Time: %s, Evacuees Served: %s",
                    time, totalTravelTime, totalServed
            );
        }
    }

    private final MapGraph mapGraph;
    private final Map<Intersection, ? extends Distributor> distributors;
    private final int expectedDemand;
    private Set<ZoneBoundary> safeZones;
    private Set<ZoneBoundary> hotZones;
    private SimulationResult result;


    private final ArrayList<EdgeQueue> edgeQueues = new ArrayList<>(1024 * 4);

    public Simulator(MapGraph mapGraph, Map<Intersection, ? extends Distributor> distributors, Set<ZoneBoundary> safeZones, Set<ZoneBoundary> hotZones, int expectedDemand) {
        this.mapGraph = mapGraph;
        this.distributors = distributors;
        this.expectedDemand = expectedDemand;
        this.safeZones = safeZones;
        this.hotZones = hotZones;
    }

    public SimulationResult run() {
        init();
        while (!finished()) {
            iterate();
        }

        return result;
    }

    private boolean finished() {
        return 0.98 * result.totalDemand <= result.totalServed;
    }

    private void iterate() {
        result.time++;
        System.out.printf("%s: %s of %s%n", result.time, result.totalServed, result.totalDemand);
        edgeQueues.parallelStream().forEach(q -> {
            Intersection u = q.getEnd();
            q.evacueesExit(result.time).forEach(e -> {
                if (u.equals(Intersection.TARGET) || u.isInZones(safeZones)) {
                    result.totalServed++;
                    result.totalTravelTime += result.time;
                } else {
                    Distributor distributor = distributors.get(u);
                    if (distributor != null) {
                        Intersection v = distributor.to(result.time);
                        // System.out.printf("Sending %s from %s to %s%n", e, u, v);
                        EdgeQueue q2 = EdgeQueue.find(u, v);
                        q2.evacueeEnter(e, result.time);
                    } else {
                        System.out.printf("%s evacuees stuck in %s, %s%n", q.numEvacuees(), u, u.isInZones(safeZones) ? "sz" : (u.isInZones(hotZones) ? "hz" : "wz"));
                    }
                }
            });
        });
    }

    private void init() {
        result = new SimulationResult();
        edgeQueues.clear();

        mapGraph.getWays().forEach(way -> way.edges().forEach(e -> {
            edgeQueues.add(new EdgeQueue(e));
            edgeQueues.add(new EdgeQueue(e.reverse()));
        }));

        mapGraph.intersections().forEach(intr -> {
            if (intr.isInZones(safeZones)) {
                edgeQueues.add(new EdgeQueue(new Way.Edge(intr, Intersection.TARGET, null) {
                    @Override
                    public double getCapacity() {
                        return Double.MAX_VALUE;
                    }

                    @Override
                    public double length() {
                        return 1;
                    }
                }));
            }
        });


        List<Intersection> hzIntersections = mapGraph.getIntersections().stream().filter(i -> i.isInZones(hotZones)).collect(Collectors.toList());
        List<Intersection> nonNullDistributors = hzIntersections.stream().filter(i -> distributors.get(i) != null && !distributors.get(i).isDeadEnd()).collect(Collectors.toList());
        // List<Intersection> nullDistributors = hzIntersections.stream().filter(i -> distributors.get(i) == null || distributors.get(i).isDeadEnd()).collect(Collectors.toList());
        // System.out.printf("Non-null distributors = %s%n", nonNullDistributors.size());
        // System.out.printf("Null distributors: %s%n", nullDistributors);

        int n = nonNullDistributors.size();
        if (n == 0)
            throw new RuntimeException("Non-null distributors has zero size");

        for (int i = 0; i < expectedDemand; i++) {
            result.totalDemand++;
            Intersection from = nonNullDistributors.get(i % n);
            Distributor distributor = distributors.get(from);
            Intersection to = distributor.to(result.time);
            EdgeQueue.find(from, to).evacueeEnter(new Evacuee(), 0);
        }
    }
}

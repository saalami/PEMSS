package ca.ucalgary.sim;

import ca.ucalgary.mapgraph.Intersection;
import ca.ucalgary.mapgraph.Way;

import java.util.*;

public class EdgeQueue {
    private static Map<Pair<Intersection>, EdgeQueue> registry = new HashMap<>();
    private Way.Edge edge;
    private Map<Evacuee, Integer> evacueesMap = new HashMap<>();
    private int lastExitTime = 0;

    public Intersection getStart() {
        return edge.getStart();
    }

    public double length() {
        return edge.length();
    }

    public Intersection getEnd() {
        return edge.getEnd();
    }

    public double getCapacity() {
        return edge.getCapacity();
    }

    public EdgeQueue(Way.Edge edge) {
        this.edge = edge;
        registry.put(new Pair<>(edge.getStart(), edge.getEnd()), this);
    }

    public static EdgeQueue find(Intersection start, Intersection end) {
        return registry.get(new Pair<>(start, end));
    }

    public Way.Edge getEdge() {
        return edge;
    }

    public int numEvacuees() {
        return evacueesMap.size();
    }

    public synchronized void evacueeEnter(Evacuee evacuee, int enterTime) {
        evacueesMap.put(evacuee, enterTime);
    }


    public synchronized void evacueesEnter(Set<Evacuee> evacuees, int enterTime) {
        evacuees.forEach(e -> evacueeEnter(e, enterTime));
    }

    public synchronized List<Evacuee> evacueesExit(int exitTime) {
        if (lastExitTime >= exitTime)
            throw new RuntimeException("No way!");

        List<Evacuee> evacuees = new LinkedList<>();
        int cap = (int) (edge.getCapacity() * (exitTime - lastExitTime));
        lastExitTime = exitTime;


        for (Map.Entry<Evacuee, Integer> e : evacueesMap.entrySet()) {
            if (exitTime > e.getValue() + edge.length()) {
                evacuees.add(e.getKey());
                if (evacuees.size() >= cap)
                    break;
            }
        }

        evacuees.forEach(evacueesMap::remove);

        return evacuees;
    }

    private static class Pair<T> {
        private T first, second;

        private Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() {
            return first;
        }

        public T getSecond() {
            return second;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair<?> pair = (Pair<?>) o;

            if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
            return !(second != null ? !second.equals(pair.second) : pair.second != null);

        }

        @Override
        public int hashCode() {
            int result = first != null ? first.hashCode() : 0;
            result = 31 * result + (second != null ? second.hashCode() : 0);
            return result;
        }
    }
}

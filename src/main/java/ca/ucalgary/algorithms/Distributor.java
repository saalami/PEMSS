package ca.ucalgary.algorithms;

import ca.ucalgary.mapgraph.Intersection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public interface Distributor {
    Intersection to(int time);

    boolean isDeadEnd();
}

class SingleDestinationDistributor implements Distributor {
    Intersection dest;

    public SingleDestinationDistributor(Intersection dest) {
        this.dest = dest;
    }

    @Override
    public Intersection to(int time) {
        return dest;
    }

    @Override
    public boolean isDeadEnd() {
        return false;
    }
}

class ProportionalLoadDistributor implements Distributor {

    private final List<Intersection> dests = new ArrayList<>(3);
    private final List<Double> flows = new ArrayList<>(3);
    private final List<Integer> assignedSoFar = new ArrayList<>();

    public ProportionalLoadDistributor() {
    }

    public ProportionalLoadDistributor(Intersection[] dests, double[] flows) {
        if (dests.length != flows.length)
            throw new RuntimeException(String.format("length of dests and flows must agree, %s != %s", dests.length, flows.length));

        if (flows.length == 0)
            throw new RuntimeException("Non-zero length arrays expected");

        for (int i = 0; i < dests.length; i++) {
            addLink(dests[i], flows[i]);
        }
    }

    public ProportionalLoadDistributor addLink(Intersection intr, double flow) {
        if (flow > 0) {
            if (dests.indexOf(intr) != -1)
                System.out.println("Intersection already added to the distributor " + intr + " flow was " + flows.get(dests.indexOf(intr)) + " becomes " + flow);

            dests.add(intr);
            flows.add(flow);
            assignedSoFar.add(0);
        }

        return this;
    }

    @Override
    public Intersection to(int time) {
        if (flows.isEmpty())
            throw new RuntimeException("Empty flow set");

        int minIndex = -1;
        double min = Double.MAX_VALUE;

        for (int i = 0; i < flows.size(); i++) {
            Double f = flows.get(i);
            double cur = assignedSoFar.get(i) / f;
            if (cur < min || (cur == min && flows.get(minIndex) < f)) {
                min = cur;
                minIndex = i;
            }
        }

        assignedSoFar.set(minIndex, assignedSoFar.get(minIndex) + 1);
        return dests.get(minIndex);
    }

    @Override
    public boolean isDeadEnd() {
        return flows.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");

        for (int i = 0; i < flows.size(); i++) {
            sb.append(dests.get(i)).append("->").append(flows.get(i)).append(", ");
        }

        sb.setLength(sb.length() - 2);
        sb.append(")");

        return sb.toString();
    }
}

class TIDistributor implements Distributor {

    private final ProportionalLoadDistributor[] distributors;

    public TIDistributor(int timespan) {
        distributors = new ProportionalLoadDistributor[timespan];
    }

    public void set(int time, Intersection to, double flow) {
        ProportionalLoadDistributor dist = distributors[time] == null ? new ProportionalLoadDistributor() : distributors[time];
        distributors[time] = dist.addLink(to, flow);
    }

    @Override
    public Intersection to(int time) {
        int t;
        t = Math.min(time, distributors.length - 1);
        while (t > 0 && distributors[t] == null) {
            t--;
        }

        if (distributors[t] != null)
            return distributors[t].to(time);

        t = Math.max(time, 0);
        while (t < distributors.length && distributors[t] == null) {
            t++;
        }

        if (distributors[t] != null)
            return distributors[t].to(time);

        throw new RuntimeException("This shouldn't happen!");
    }

    @Override
    public boolean isDeadEnd() {
        return false;
    }
}
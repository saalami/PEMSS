package ca.ucalgary.sim;

import ca.ucalgary.mapgraph.Intersection;
import ca.ucalgary.ui.ZoneBoundary;

import java.awt.geom.Path2D;
import java.util.Set;

public class SimpleDemandEstimator implements DemandEstimator {

    private int demand;
    private Set<ZoneBoundary> hotZones;

    public SimpleDemandEstimator(int demand, Set<ZoneBoundary> hotZones) {
        this.demand = demand;
        this.hotZones = hotZones;
    }

    @Override
    public int getDemand(Intersection intersection) {
        if (intersection.noDemand)
            return 0;

        return (intersection.isInZones(hotZones)) ? demand : 0;
    }
}

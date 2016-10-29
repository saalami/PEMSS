package ca.ucalgary.sim;

import ca.ucalgary.mapgraph.Intersection;

public interface DemandEstimator {
    int getDemand(Intersection intersection);
}


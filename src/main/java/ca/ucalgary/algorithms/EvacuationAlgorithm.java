package ca.ucalgary.algorithms;

import ca.ucalgary.mapgraph.Intersection;
import ca.ucalgary.mapgraph.MapGraph;
import ca.ucalgary.ui.ZoneBoundary;

import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface EvacuationAlgorithm {
    HashMap<Intersection, ? extends Distributor> solve(MapGraph mapGraph, Set<ZoneBoundary> hotZones, Set<ZoneBoundary> safeZones);
}

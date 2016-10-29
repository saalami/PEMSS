package ca.ucalgary.ui;

import ca.ucalgary.mapgraph.Intersection;
import ca.ucalgary.mapgraph.Scaler2D;

import java.awt.*;

public class ZoneBoundary extends Polygon {
    private final Scaler2D scaler2D;

    public ZoneBoundary(Scaler2D scaler2D) {
        this.scaler2D = scaler2D;
    }

    public ZoneBoundary(Scaler2D scaler2D, int[] xpoints, int[] ypoints, int npoints) {
        super(xpoints, ypoints, npoints);
        this.scaler2D = scaler2D;
    }

    public boolean contains(Intersection intr) {
        int x = scaler2D.xGeoToPanel(intr.getLon());
        int y = scaler2D.yGeoToPanel(intr.getLat());
        return super.contains(x, y);
    }

}

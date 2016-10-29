package ca.ucalgary.mapgraph;

import java.awt.geom.Rectangle2D;

public class Scaler2D {
    private final Rectangle2D.Double bounds;
    private int width, height;

    public Scaler2D(Rectangle2D.Double bounds) {
        this.bounds = bounds;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int xGeoToPanel(double lon) {
        return (int) Math.round((lon - bounds.getX()) * (width / bounds.getWidth()));
    }

    public int yGeoToPanel(double lat) {
        return (int) Math.round(height - (lat - bounds.getY()) * (height / bounds.getHeight()));
    }

    public double xPanelToGeo(int x) {
        return (double) x / width * bounds.getWidth() + bounds.getX();
    }

    public double yPanelToGeo(int y) {
        return (double) (height - y) / height * bounds.getHeight() + bounds.getY();
    }

    public static void main(String[] args) {
        double minlat = 51.0321000, minlon = -114.1113000, maxlat = 51.0619000, maxlon = -114.0241000;
        Rectangle2D.Double bounds = new Rectangle2D.Double(minlon, minlat, maxlon - minlon, maxlat - minlat);
        Scaler2D scaler2D = new Scaler2D(bounds);
        scaler2D.setSize(600, 400);
        System.out.println(scaler2D.xGeoToPanel(scaler2D.xPanelToGeo(10)));
        System.out.println(scaler2D.yGeoToPanel(scaler2D.yPanelToGeo(10)));
    }
}

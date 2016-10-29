package ca.ucalgary.ui;

import ca.ucalgary.mapgraph.*;
import ca.ucalgary.sim.Zone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.*;

public class MapPanel extends JPanel {
    private final MapGraph parseResult;
    private final Scaler2D scaler2D;
    private HashMap<ZoneBoundary, Zone> zoneMap = new HashMap<>();
    private ZoneBoundary currentZone = null;

    private HashSet<WayType> enabledWayTypes = new HashSet<>();
    private boolean debug = true;

    public MapPanel(MapGraph parseResult) {
        this.parseResult = parseResult;
        this.scaler2D = new Scaler2D(parseResult.getBounds());
        setBackground(Color.WHITE);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addPointToCurrentZone(e.getX(), e.getY(), e.getClickCount() > 1);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();
        scaler2D.setSize(w, h);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (Way way : parseResult.getWays()) {
            WayType type = way.getType();
            if (enabledWayTypes.contains(type)) {
                g2.setColor(type.getColor());
                BasicStroke stroke = new BasicStroke(
                        (float) type.getDrawWidth(),
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        type.isRail() ? 0.5f : 1f,
                        new float[]{20, 20},
                        0f);
                g2.setStroke(stroke);
                ArrayList<Intersection> intersections = way.getIntersections();

                Color[] colors = new Color[]{Color.RED, Color.BLUE};
                for (int i = 1; i < intersections.size(); i++) {
                    // g2.setColor(colors[i % 2]);
                    Intersection p1 = intersections.get(i - 1);
                    Intersection p2 = intersections.get(i);

                    int x1 = scaler2D.xGeoToPanel(p1.getLon());
                    int y1 = scaler2D.yGeoToPanel(p1.getLat());
                    int x2 = scaler2D.xGeoToPanel(p2.getLon());
                    int y2 = scaler2D.yGeoToPanel(p2.getLat());
                    g2.drawLine(x1, y1, x2, y2);
                }

                if (debug) {
                    intersections.forEach(i -> {
                        Color c = i.isInZones(getHotZones()) ? Color.RED : (
                                i.isInZones(getSafeZones()) ? Color.BLUE : Color.BLACK
                        );

                        g2.setColor(c);
                        g2.drawString(i.toString(), scaler2D.xGeoToPanel(i.getLon()), scaler2D.yGeoToPanel(i.getLat()));
                    });
                }
            }
        }

        BasicStroke zoneStroke = new BasicStroke(1);
        g2.setStroke(zoneStroke);

        Composite origComposite = g2.getComposite();
        Composite alphaComposite = AlphaComposite.SrcOver.derive(0.4f);

        for (Map.Entry<ZoneBoundary, Zone> e : zoneMap.entrySet()) {
            g2.setColor(e.getValue().getColor());
            g2.setComposite(origComposite);
            g2.draw(e.getKey());
            g2.setComposite(alphaComposite);
            g2.fill(e.getKey());
        }
    }

    public void setShowWay(WayType type, boolean show) {
        if (show) {
            enabledWayTypes.add(type);
        } else {
            enabledWayTypes.remove(type);
        }
    }

    public void startMakeZone(Zone zone) {
        ZoneBoundary p = currentZone;
        if (p != null && p.npoints <= 2) {
            zoneMap.remove(p);
        }

        currentZone = new ZoneBoundary(scaler2D);
        zoneMap.put(currentZone, zone);
    }

    public void cancelZone() {
        Polygon p = currentZone;
        if (p != null) {
            zoneMap.remove(p);
            currentZone = null;
        }
        repaint();
    }

    private void addPointToCurrentZone(int x, int y, boolean finish) {
        Polygon p = currentZone;
        if (p == null)
            return;

        p.addPoint(x, y);
        if (finish) {
            currentZone = null;
        }
        repaint();
    }

    public Set<ZoneBoundary> getSafeZones() {
        return getZones(Zone.SAFE);
    }

    public Set<ZoneBoundary> getHotZones() {
        return getZones(Zone.HOT);
    }

    private Set<ZoneBoundary> getZones(Zone zone) {
        Set<ZoneBoundary> zones = new HashSet<>();
//        for (Map.Entry<Polygon, Zone> e : zoneMap.entrySet()) {
//            if (e.getValue() == zone) {
//                int[] xs = e.getKey().xpoints;
//                int[] ys = e.getKey().ypoints;
//                Path2D.Double path = new Path2D.Double();
//
//                path.moveTo(scaler2D.xPanelToGeo(xs[0]), scaler2D.yPanelToGeo(ys[0]));
//                for (int i = 1; i < xs.length; ++i) {
//                    path.lineTo(scaler2D.xPanelToGeo(xs[i]), scaler2D.yPanelToGeo(ys[i]));
//                }
//                path.closePath();
//
//                zones.add(path);
//            }
//        }

        zoneMap.entrySet().stream().filter(e -> e.getValue() == zone).forEach(e -> zones.add(e.getKey()));
        return zones;
    }
}

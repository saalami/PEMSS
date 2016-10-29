package ca.ucalgary.mapgraph;

import java.awt.*;

public enum WayType {
    MOTORWAY(11, Color.GREEN),
    TRUNK(11, Color.GREEN),
    RAIL(2, Color.RED),
    RAIL_STATION(0, Color.RED.darker()),
    RAIL_PLATFORM(0, Color.RED.darker()),
    LIGHT_RAIL(2, Color.ORANGE.darker()),
    PRIMARY(11, Color.GREEN),
    SECONDARY(7, Color.GREEN.darker()),
    TERTIARY(6, Color.PINK),
    UNCLASSIFIED(5, Color.CYAN),
    RESIDENTIAL(5, Color.ORANGE),
    SERVICE(3, Color.BLUE.brighter()),
    LIVING_STREET(3),
    PEDESTRIAN(2),
    TRACK(1),
    BUS_GUIDEWAY(1),
    RACEWAY(1),
    ROAD(3),
    FOOTWAY(1),
    CYCLEWAY(1),
    BRIDLEWAY(1),
    STEPS(1),
    PATH(1),
    MOTORWAY_LINK(2),
    TRUNK_LINK(2),
    PRIMARY_LINK(2),
    SECONDARY_LINK(2),
    TERTIARY_LINK(2),
    OTHER(0, Color.GRAY.brighter()),
    CONNECTOR(10, Color.RED);

    private final double width;
    private final Color color;

    WayType(double capacity) {
        this(capacity, Color.BLUE);
    }

    WayType(double width, Color color) {
        this.width = width;
        this.color = color;
    }

    public double getDrawWidth() {
        return width;
    }

    public double getCapacity() {
        return isRail() ? 0 : width;
    }

    public Color getColor() {
        return color;
    }

    public boolean isRail() {
        return name().endsWith("RAIL") || name().startsWith("RAIL");
    }
}

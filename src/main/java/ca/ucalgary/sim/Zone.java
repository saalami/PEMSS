package ca.ucalgary.sim;

import java.awt.*;

public enum Zone {
    STREET(-1),
    BLOCKED(-16777216),
    SAFE(-16711936),
    HOT(-65536);

    private final int rgb;
    private final Color color;

    Zone(int rgb) {
        this.rgb = rgb;
        this.color = new Color(rgb);
    }

    public int getRGB() {
        return rgb;
    }

    public Color getColor() {
        return color;
    }

    public static Zone findByRGB(int RGB) {
        if (RGB == STREET.rgb)
            return STREET;

        if (RGB == SAFE.rgb)
            return SAFE;

        if (RGB == HOT.rgb)
            return HOT;

        return BLOCKED;
    }
}

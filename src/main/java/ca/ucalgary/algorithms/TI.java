package ca.ucalgary.algorithms;

import ca.ucalgary.mapgraph.Intersection;

public final class TI {
    public static final TI SOURCE = new TI(Intersection.SOURCE, 0);
    public static final TI TARGET = new TI(Intersection.SOURCE, Integer.MAX_VALUE);

    private final Intersection intersection;
    private final int time;

    public TI(Intersection intersection, int time) {
        this.intersection = intersection;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TI)) return false;

        TI ti = (TI) o;

        if (time != ti.time) return false;
        return !(intersection != null ? !intersection.equals(ti.intersection) : ti.intersection != null);

    }

    @Override
    public int hashCode() {
        int result = intersection != null ? intersection.hashCode() : 0;
        result = 31 * result + time;
        return result;
    }

    @Override
    public String toString() {
        return getIntersection() + "@" + getTime();
    }
}

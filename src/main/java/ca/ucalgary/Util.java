package ca.ucalgary;

import ca.ucalgary.mapgraph.Intersection;

public class Util {
    private static final double P180 = Math.PI / 180;
    static final double R = 6378.137; // Radius of earth in KM

    private Util() {
    }

    public static double latLonDistanceToMeter(double lat1, double lon1, double lat2, double lon2) {
        double dLat = (lat2 - lat1) * P180;
        double dLon = (lon2 - lon1) * P180;
        double a = Math.sin(dLat / 2) *
                   Math.sin(dLat / 2) +
                   Math.cos(lat1 * P180) *
                   Math.cos(lat2 * P180) *
                   Math.sin(dLon / 2) *
                   Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000; // meters
    }


    public static double dist(Intersection u, Intersection v) {
        return latLonDistanceToMeter(u.getLat(), u.getLon(), v.getLat(), v.getLon());
    }

    public static void main(String[] args) {
        System.out.println(latLonDistanceToMeter(51.0968274, -114.0415682, 51.0938186, -114.0400509));
    }
}

package be.sailormoon.common.geo;

import java.text.DecimalFormat;

public final class SMGeoPoint {
    /**
     * The minimum allowed latitude
     */
    public static float MIN_LATITUDE = Float.valueOf("-90.000000");

    /**
     * The maximum allowed latitude
     */
    public static float MAX_LATITUDE = Float.valueOf("90.000000");

    /**
     * The minimum allowed longitude
     */
    public static float MIN_LONGITUDE = Float.valueOf("-180.000000");

    /**
     * The maximum allowed longitude
     */
    public static float MAX_LONGITUDE = Float.valueOf("180.000000");

    private double lat;
    private double lon;
    private DecimalFormat format = new DecimalFormat("##.######");

    public SMGeoPoint() {
        this.lat = 0;
        this.lon = 0;
    }

    public SMGeoPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public SMGeoPoint(String coord) {
        this.fromString(coord);
    }

    public SMGeoPoint replace(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        return this;
    }

    public SMGeoPoint replace(String coord) {
        this.fromString(coord);
        return this;
    }

    public SMGeoPoint replaceLat(double lat) {
        this.lat = lat;
        return this;
    }

    public SMGeoPoint replaceLon(double lon) {
        this.lon = lon;
        return this;
    }

    public boolean isValid() {
        return (Double.compare(lat, MAX_LATITUDE) <= 0) &&
               (Double.compare(lat, MIN_LATITUDE) >= 0) &&
               (Double.compare(lon, MAX_LONGITUDE) <= 0) &&
               (Double.compare(lon, MIN_LONGITUDE) >= 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SMGeoPoint geoPoint = (SMGeoPoint) o;
        return ( (Double.compare(geoPoint.lat, lat) == 0) && (Double.compare(geoPoint.lon, lon) == 0) );
    }

    @Override
    public String toString() {
        return format.format(this.lat) + "," + format.format(this.lon);
    }

    @Override
    public int hashCode() {
        return 31*this.toString().hashCode();
    }

    private void fromString(String coord) {
        int c = coord.indexOf(',');
        if (c != -1) {
            lat = Double.parseDouble(coord.substring(0, c).trim());
            lon = Double.parseDouble(coord.substring(c + 1).trim());
        } else {
            lat = 0;
            lon = 0;
        }
    }

    public final double getLat() {
        return this.lat;
    }

    public final double getLon() {
        return this.lon;
    }
}

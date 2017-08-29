package be.sailormoon.common.geo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMGeoBoundingBox {
    /**
     * Main constructor, specify the topLeft coord and bottomRight coord of a square
     * @param topLeftCoord
     *  The top left coord of the square
     * @param bottomRightCoord
     *  The bottomRight coord of the square
     */
    public SMGeoBoundingBox(SMGeoPoint topLeftCoord, SMGeoPoint bottomRightCoord) {

        this.topLeft = topLeftCoord;
        this.bottomRight = bottomRightCoord;

        this.topRight = new SMGeoPoint(this.topLeft.getLat(), this.bottomRight.getLon());
        this.bottomLeft = new SMGeoPoint(this.bottomRight.getLat(), this.topLeft.getLon());

        // The center coord of the square
        SMGeoPoint centerSquare = SMGeoUtils.retrieveCenterPointOfGeoBound(this.topLeft, this.bottomRight);
        this.center = centerSquare;

        // Find the largest circle that can fit inside the square, radius in METER is calculate
        SMGeoPoint centerSide = SMGeoUtils.retrieveCenterPointOfGeoBound(this.topLeft, this.bottomLeft);
        this.radius = SMGeoUtils.distVincenty(centerSide, centerSquare);
    }

    /**
     * Instead of building the circle from the square this does the opposite
     * @param center
     *  Center coord of the square
     * @param distance
     *  Radius distance un meters
     */
    public SMGeoBoundingBox(SMGeoPoint center, double distance) {
        this.center = center;
        this.radius = distance;

        this.getBoundingBox(center, distance);

        this.topLeft = new SMGeoPoint(this.topRight.getLat(), this.bottomLeft.getLon());
        this.bottomRight = new SMGeoPoint(this.bottomLeft.getLat(), this.topRight.getLon());
    }

    /**
     * Overloaded constructor, build the object using Strings
     * @param topLeftCoord
     * @param bottomRightCoord
     */
    public SMGeoBoundingBox(String topLeftCoord, String bottomRightCoord) {
        this(new SMGeoPoint(topLeftCoord), new SMGeoPoint(bottomRightCoord));
    }

    public SMGeoBoundingBox(String center, double distance) {
        this(new SMGeoPoint(center), distance);
    }

    /**
     * Simply returns true if the original coords from the constructor are zero
     * @return boolean
     */
    public boolean isSquareEmpty() {
        return (this.topLeft.getLat() == 0 &&
                this.topLeft.getLon() == 0 &&
                this.bottomRight.getLat() == 0 &&
                this.bottomRight.getLon() == 0);
    }

    /**
     * Output the height of the square
     * @return double
     */
    public double GetHeight() {
        return SMGeoUtils.distVincenty(this.getTopLeft(), this.getBottomLeft());
    }

    /**
     * Utility method to build a Map with all coords and values
     * @return Map<String, Object>
     */
    public Map<String, Object> getNaviHashMap() {
        Map<String, Object> output = new HashMap<>();

        // Corners
        output.put("topLeft", this.getTopLeft());
        output.put("topRight", this.getTopRight());
        output.put("bottomLeft", this.getBottomLeft());
        output.put("bottomRight", this.getBottomRight());

        // The center coord of the square
        output.put("center", this.getCenter());

        // Radius in meters of the largest circle that can be drawn inside the square
        output.put("radius", this.getRadius());

        return output;
    }

    /**
     * Given a list of points, shrink the square to the smallest square containing all the points.
     * @param points    The list of SMGeoPoint to base the shrink on
     */
    public void shrink(List<SMGeoPoint> points) {
        double minLat = this.bottomRight.getLat();
        double minLong = this.bottomRight.getLon();
        double maxLat = this.topLeft.getLat();
        double maxLong = this.topLeft.getLon();

        for (SMGeoPoint point : points) {
            double lat = point.getLat();
            double lon = point.getLon();

            if(minLat > lat) {
                minLat = lat;
            }

            if(minLong > lon) {
                minLong = lon;
            }

            if(maxLat < lat) {
                maxLat = lat;
            }

            if(maxLong < lon) {
                maxLong = lon;
            }
        }

        this.bottomRight = new SMGeoPoint(minLat, minLong);
        this.topLeft = new SMGeoPoint(maxLat, maxLong);
        this.topRight = new SMGeoPoint(this.topLeft.getLat(), this.bottomRight.getLon());
        this.bottomLeft = new SMGeoPoint(this.bottomRight.getLat(), this.topLeft.getLon());

        // The center coord of the square
        SMGeoPoint centerSquare = SMGeoUtils.retrieveCenterPointOfGeoBound(this.topLeft, this.bottomRight);
        this.center = centerSquare;

        // Find the largest circle that can fit inside the square, radius in METER is calculated
        SMGeoPoint centerSide = SMGeoUtils.retrieveCenterPointOfGeoBound(this.topLeft, this.bottomLeft);
        this.radius = SMGeoUtils.distVincenty(centerSide, centerSquare);
    }

    private void getBoundingBox(SMGeoPoint center, double distance) {
        // Bounding box surrounding the point at given coordinates,
        // assuming local approximation of Earth surface as a sphere
        // of radius given by WGS84
        double lat = Math.toRadians(center.getLat());
        double lon = Math.toRadians(center.getLon());

        // Radius of Earth at given latitude
        double radius = WGS84EarthRadius(lat);
        // Radius of the parallel at given latitude
        double pradius = radius * Math.cos(lat);

        double latMin = lat - distance / radius;
        double latMax = lat + distance / radius;
        double lonMin = lon - distance / pradius;
        double lonMax = lon + distance / pradius;

        this.bottomLeft = new SMGeoPoint(Math.toDegrees(latMin), Math.toDegrees(lonMin));
        this.topRight = new SMGeoPoint(Math.toDegrees(latMax), Math.toDegrees(lonMax));
    }

    // Earth radius at a given latitude, according to the WGS-84 ellipsoid [m]
    private static double WGS84EarthRadius(double lat) {
        // http://en.wikipedia.org/wiki/Earth_radius
        double An = SMGeoUtils.WGS84_a * SMGeoUtils.WGS84_a * Math.cos(lat);
        double Bn = SMGeoUtils.WGS84_b * SMGeoUtils.WGS84_b * Math.sin(lat);
        double Ad = SMGeoUtils.WGS84_a * Math.cos(lat);
        double Bd = SMGeoUtils.WGS84_b * Math.sin(lat);
        return Math.sqrt((An*An + Bn*Bn) / (Ad*Ad + Bd*Bd));
    }

    private SMGeoPoint topLeft;
    private SMGeoPoint bottomRight;
    private SMGeoPoint topRight;
    private SMGeoPoint bottomLeft;
    private SMGeoPoint center;
    private double radius;

    /**
     *  Accessors
     */
    public SMGeoPoint getTopLeft() { return this.topLeft; }
    public SMGeoPoint getBottomRight() { return this.bottomRight; }
    public SMGeoPoint getTopRight() { return this.topRight; }
    public SMGeoPoint getBottomLeft() { return this.bottomLeft; }
    public SMGeoPoint getCenter() { return this.center; }
    public Double getRadius() { return this.radius; }
}


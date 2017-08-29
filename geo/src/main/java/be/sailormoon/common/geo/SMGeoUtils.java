package be.sailormoon.common.geo;

public class SMGeoUtils {

    // Semi-axes of WGS-84 geoidal reference
    public static double WGS84_a = 6378137.0; // Major semiaxis [m]
    public static double WGS84_b = 6356752.314245; // Minor semiaxis [m]
    public static double WGS84_f = 1 / 298.257223563; // Inverse flattening [m]

    /**
     * Calculates the midpoint coordinates between the gpa and gpb coords
     * @param gpa
     * @param gpb
     * @return SMGeoPoint
     */
    public static SMGeoPoint retrieveCenterPointOfGeoBound(SMGeoPoint gpa, SMGeoPoint gpb) {
        double lat1 = gpa.getLat();
        double lon1 = gpa.getLon();
        double lat2 = gpb.getLat();
        double lon2 = gpb.getLon();

        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new SMGeoPoint(Math.toDegrees(lat3), Math.toDegrees(lon3));
    }

    /**
     * Calculates geodesic distance between two points specified by latitude/longitude using Vincenty inverse formula
     * for ellipsoids
     * @param gpa
     * @param gpb
     * @return double
     */
    public static double distVincenty(SMGeoPoint gpa, SMGeoPoint gpb) {
        double lat1 = gpa.getLat();
        double lon1 = gpa.getLon();
        double lat2 = gpb.getLat();
        double lon2 = gpb.getLon();

        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - WGS84_f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - WGS84_f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
        double lambda = L, lambdaP, iterLimit = 100;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0)
                return 0; // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM))
                cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
            double C = WGS84_f / 16 * cosSqAlpha * (4 + WGS84_f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * WGS84_f * sinAlpha
                * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
            return Double.NaN; // formula failed to converge

        double uSq = cosSqAlpha * (WGS84_a * WGS84_a - WGS84_b * WGS84_b) / (WGS84_b * WGS84_b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B
            * sinSigma
            * (cos2SigmaM + B
            / 4
            * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
            * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        return WGS84_b * A * (sigma - deltaSigma);
    }
}

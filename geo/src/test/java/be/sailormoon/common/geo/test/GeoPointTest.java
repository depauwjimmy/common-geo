package be.sailormoon.common.geo.test;

import be.sailormoon.common.geo.SMGeoPoint;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeoPointTest {

    @Test
    public void testGeoPointValid() {
        SMGeoPoint point = new SMGeoPoint(50.8503d, 4.3517d);
        assertEquals(true, point.isValid());
    }

    @Test
    public void testGeoPointInvalid() {
        SMGeoPoint point = new SMGeoPoint(91.0123d, 4.3517d);
        assertEquals(false, point.isValid());
    }

    @Test
    public void testGeoPointEquality() {
        SMGeoPoint pointA = new SMGeoPoint(91.0123d, 4.3517d);
        SMGeoPoint pointB = new SMGeoPoint(91.0123d, 4.3517d);
        assertEquals(true, pointA.equals(pointB));
    }
}

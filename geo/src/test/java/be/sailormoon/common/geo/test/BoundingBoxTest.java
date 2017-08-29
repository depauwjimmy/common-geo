package be.sailormoon.common.geo.test;

import be.sailormoon.common.geo.SMGeoBoundingBox;
import be.sailormoon.common.geo.SMGeoPoint;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoundingBoxTest {

    @Test
    public void testGeoBoundingBoxSquare() {
        SMGeoPoint pointBxl = new SMGeoPoint(50.8503d, 4.3517d);
        SMGeoPoint pointNamur = new SMGeoPoint(50.4674d, 4.8720d);

        SMGeoBoundingBox boundingBox = new SMGeoBoundingBox(pointBxl, pointNamur);

        SMGeoPoint expectedCenter = new SMGeoPoint(50.65914d, 4.612911d);

        // Calculation using floats have an error propagation that we cannot avoid thus the delta value for the test
        assertEquals(18470.46841383676d, boundingBox.getRadius(), 0.0002d);
        assertEquals(expectedCenter.getLat(), boundingBox.getCenter().getLat(), 0.0002d);
        assertEquals(expectedCenter.getLon(), boundingBox.getCenter().getLon(), 0.0002d);
    }

    /*
    @Test
    public void testGeoBoundingBoxCircle() {
        SMGeoPoint center = new SMGeoPoint(50.65914d, 4.612911d);
        double radius = 18470.46841383676d;

        SMGeoBoundingBox boundingBox = new SMGeoBoundingBox(center, radius);

        System.out.println(boundingBox.getNaviHashMap());
    }
    */
}

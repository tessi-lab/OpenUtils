/*
 * Copyright 2017 Tessi lab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tessilab.oss.openutils.distance.bbox;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.Rectangle;

import org.junit.Before;
import org.junit.Test;

import io.tessilab.oss.openutils.bbox.BBoxable;
import io.tessilab.oss.openutils.bbox.BBoxer;
import io.tessilab.oss.openutils.distance.bbox.ClosestDistance;

/**
 * @author Juan Camilo Rodriguez Duran
 *
 */
public class TestClosestDistance {

    private static final double TOL_RATE = 1e-5;

    BBoxable w1, w2;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Rectangle r1 = new Rectangle(15, 15, 100, 50);
        Rectangle r2 = new Rectangle(90, 100, 100, 50);
        w1 = new BBoxer(r1);
        w2 = new BBoxer(r2);
    }

    /**
     * Test method for
     * {@link io.tessilab.utils.distance.bbox.ClosestDistance#computeDistance(io.tessilab.utils.bbox.BBoxable, io.tessilab.utils.bbox.BBoxable)}
     * .
     */
    @Test
    public void testComputeDistanceBBoxableBBoxable() {
        ClosestDistance cd = new ClosestDistance();
        double distance = cd.computeDistance(w1, w2);
        assertEquals(35., distance, TOL_RATE);

        distance = cd.computeDistance(w2, w1);
        assertEquals(35., distance, TOL_RATE);

        distance = cd.computeDistance(w1, w1);
        assertEquals(0., distance, TOL_RATE);
    }

    /**
     * Test method for
     * {@link io.tessilab.utils.distance.bbox.ClosestDistance#getClosestPoints(java.awt.Rectangle, java.awt.Rectangle)}
     * .
     */
    @Test
    public void testGetClosestPoints() {
        Point[] closestPoints = ClosestDistance.getClosestPoints(w1.getBBox(), w2.getBBox());
        Point p1 = closestPoints[0];
        assertEquals(102, p1.getX(), TOL_RATE);
        assertEquals(65, p1.getY(), TOL_RATE);

        p1 = closestPoints[1];
        assertEquals(102, p1.getX(), TOL_RATE);
        assertEquals(100, p1.getY(), TOL_RATE);
    }

}

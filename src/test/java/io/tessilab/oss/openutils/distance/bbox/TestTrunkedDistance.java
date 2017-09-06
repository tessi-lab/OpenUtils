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

import io.tessilab.oss.openutils.MyTolerantMath;
import static org.junit.Assert.*;

import org.junit.Test;

import io.tessilab.oss.openutils.bbox.BBoxable;
import io.tessilab.oss.openutils.bbox.BBoxer;
import io.tessilab.oss.openutils.distance.bbox.TrunkedDistance;
import io.tessilab.oss.openutils.distance.bbox.TrunkedDistance.AlignMode;

public class TestTrunkedDistance {

    private double TOL_RATE = 1e-5;
    private MyTolerantMath tolM = new MyTolerantMath(TOL_RATE);
    private TrunkedDistance td;

    @Test
    public void testBasics() {
        td = new TrunkedDistance();
        assertEquals(AlignMode.NONE, td.getAlignMode());
        assertEquals(1.0, td.getFactor(), TOL_RATE);

        double factor = 3.5;
        td = new TrunkedDistance(AlignMode.HORIZONTAL, factor);
        assertEquals(AlignMode.HORIZONTAL, td.getAlignMode());
        assertEquals(factor, td.getFactor(), TOL_RATE);

        // change align mode
        td.setAlignMode(AlignMode.VERTICAL);
        assertEquals(AlignMode.VERTICAL, td.getAlignMode());

        // change factor
        double factor2 = 50.0;
        td.setFactor(factor2);
        assertEquals(factor2, td.getFactor(), TOL_RATE);
    }

    @Test
    public void testSelf() {
        td = new TrunkedDistance();
        BBoxable bb = new BBoxer(15, 110, 100, 50);
        double d0 = td.computeDistance(bb, bb);
        assertEquals(0., d0, TOL_RATE);
    }

    @Test
    public void testDistance() {
        td = new TrunkedDistance();
        BBoxable bb0 = new BBoxer(0, 0, 50, 50);
        BBoxable bb1 = new BBoxer(100, 0, 50, 50);
        BBoxable bb2 = new BBoxer(0, 100, 50, 50);
        BBoxable bbDiag = new BBoxer(100, 100, 50, 50);

        // test the value
        double d1 = td.computeDistance(bb0, bb1);
        assertEquals(50.0, d1, TOL_RATE);
        // test symetry
        double d1Reverse = td.computeDistance(bb1, bb0);
        assertEquals(d1, d1Reverse, TOL_RATE);

        // test same distance
        double d2 = td.computeDistance(bb0, bb2);
        assertEquals(d1, d2, TOL_RATE);

        // test diag
        double dDiag = td.computeDistance(bb0, bbDiag);
        assertEquals(true, tolM.tolCompare(d1, dDiag) < 0);
    }

    @Test
    public void testAlignModeFactor() {
        double factor = 4.0;
        td = new TrunkedDistance(AlignMode.HORIZONTAL, factor);
        BBoxable bb0 = new BBoxer(0, 0, 50, 50);
        BBoxable bb1 = new BBoxer(100, 0, 50, 50);
        BBoxable bb2 = new BBoxer(0, 100, 50, 50);

        double d1 = td.computeDistance(bb0, bb1);
        double d2 = td.computeDistance(bb0, bb2);
        assertEquals(true, tolM.tolCompare(d1, d2) < 0);
        assertEquals(d2 / factor, d1, TOL_RATE);
    }

}

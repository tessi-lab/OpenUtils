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
package io.tessilab.oss.openutils;

import io.tessilab.oss.openutils.HistogramUtils;
import io.tessilab.oss.openutils.MyTolerantMath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestHistogramUtils {

    private MyTolerantMath tolM = new MyTolerantMath(1e-3);

    @Test
    public void testMinMax() {
        double[] histo = new double[] { 1, 2, 3, 4, 5 };

        assertTrue(tolM.tolEquals(5, HistogramUtils.findHistoMax(histo)));
        assertTrue(tolM.tolEquals(1, HistogramUtils.findHistoMin(histo)));

        histo = new double[] { 5, 4, 3, 2, 1 };
        assertTrue(tolM.tolEquals(5, HistogramUtils.findHistoMax(histo)));
        assertTrue(tolM.tolEquals(1, HistogramUtils.findHistoMin(histo)));
    }

    @Test
    public void testMinMaxStartStop() {
        double[] histo = new double[] { 1, 2, 3, 4, 5 };

        assertTrue(tolM.tolEquals(3, HistogramUtils.findHistoMax(histo, 0, 2)));
        assertTrue(tolM.tolEquals(2, HistogramUtils.findHistoMax(histo, 0, 1)));
        assertTrue(tolM.tolEquals(5, HistogramUtils.findHistoMax(histo, 0, 13)));
        assertTrue(tolM.tolEquals(4, HistogramUtils.findHistoMax(histo, 2, 3)));

        assertTrue(tolM.tolEquals(1, HistogramUtils.findHistoMin(histo, 0, 2)));
        assertTrue(tolM.tolEquals(3, HistogramUtils.findHistoMin(histo, 2, 4)));
        assertTrue(tolM.tolEquals(1, HistogramUtils.findHistoMin(histo, 0, 13)));
        assertTrue(tolM.tolEquals(5, HistogramUtils.findHistoMin(histo, 4, 13)));
        assertTrue(tolM.tolEquals(1, HistogramUtils.findHistoMin(histo, -2, 3)));

        histo = new double[] { 34, 4, 25, -5, 10 };
        assertTrue(tolM.tolEquals(25, HistogramUtils.findHistoMax(histo, 1, 3)));
        assertTrue(tolM.tolEquals(-5, HistogramUtils.findHistoMin(histo, 1, 4)));
    }

    @Test
    public void testEmptyHisto() {
        double[] histo = new double[] {};
        assertEquals(-1, HistogramUtils.inBounds(42, histo));
        assertNull(HistogramUtils.findHistoMax(histo));
        assertNull(HistogramUtils.findHistoMin(histo));
    }

    @Test
    public void testLocalDerivatives() {
        double[] histo = new double[] { 2, 2, 4, 7, 2, 4 };
        double[] derivs = HistogramUtils.localDerivatives(histo, 3);
        assertEquals(histo.length, derivs.length);
        assertTrue(tolM.tolEquals(2, derivs[1]));
        assertTrue(tolM.tolEquals(-2, derivs[3]));
    }

}

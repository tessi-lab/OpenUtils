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

import io.tessilab.oss.openutils.MyTolerantMath;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestMyTolerantMath {

    private MyTolerantMath tolM = new MyTolerantMath(1e-3);

    @Test
    public void testTolIsInRangeOK() {
        assertTrue(tolM.tolIsInRange(0.0, 0.0, 2.0));
        assertTrue(tolM.tolIsInRange(-0.0, 0.0, 2.0));
        assertTrue(tolM.tolIsInRange(1.0, 0.0, 2.0));
        assertTrue(tolM.tolIsInRange(1.333, 0.0, 2.0));
        assertTrue(tolM.tolIsInRange(2.0, 0.0, 2.0));
    }

    @Test
    public void testTolIsInRangeEmptyRange() {
        assertTrue(tolM.tolIsInRange(1.0, 1.0, 1.0));
        assertFalse(tolM.tolIsInRange(0.5, 1.0, 1.0));
    }

    @Test
    public void testTolIsInRangeKO() {
        assertFalse(tolM.tolIsInRange(-0.5, 0.0, 2.0));
        assertFalse(tolM.tolIsInRange(3.2, 0.0, 2.0));
    }

    @Test
    public void testTolContains() {
        List<Double> l = Arrays.asList(1.2, 1.4);
        assertFalse(tolM.tolContains(0.1, l));
        assertFalse(tolM.tolContains(1.3, l));
        assertTrue(tolM.tolContains(1.2, l));
    }
}

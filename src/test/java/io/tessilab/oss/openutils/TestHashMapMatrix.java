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

import io.tessilab.oss.openutils.HashMapMap;
import io.tessilab.oss.openutils.MyTolerantMath;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TestHashMapMatrix {

    private MyTolerantMath tolM = new MyTolerantMath(0.001);
    private HashMapMap<Double> hmm;

    @Before
    public void buildUp() {
        hmm = new HashMapMap<Double>();
    }

    @Test
    public void testPut() {
        hmm.put(1, 1, 2.0);
        assertTrue("The value wasn't set correctly", tolM.tolEquals(hmm.get(1, 1), 2.0));
        hmm.put(1, 1, 3.0);
        assertTrue("The value wasn't set correctly", tolM.tolEquals(hmm.get(1, 1), 3.0));
    }

    @Test
    public void testGet() {
        assertTrue("There shouldn't be a value", hmm.get(1, 1) == null);
        hmm.put(1, 1, 2.0);
        assertTrue("The value wasn't set correctly", tolM.tolEquals(hmm.get(1, 1), 2.0));
    }

    @Test
    public void testContainsKey() {
        assertFalse("There shouldn't be a key", hmm.containsKey(1, 1));
        hmm.put(1, 1, 2.0);
        assertTrue("The key should exist", hmm.containsKey(1, 1));
        hmm.put(2, 4, 2.0);
        assertTrue("The key should exist", hmm.containsKey(2, 4));
    }

    @Test
    public void testRemoveKeyAndContains() {
        assertTrue("There should be nothing to remove", hmm.removeKey(1, 1) == null);
        hmm.put(1, 1, 2.0);
        assertTrue("The wrong value was returned by the removal feature", tolM.tolEquals(hmm.removeKey(1, 1), 2.0));
        hmm.put(2, 1, 3.0);
        hmm.put(2, 2, 4.0);
        assertTrue("The key should exist", hmm.containsKey(2, 1));
        assertTrue("The key should exist", hmm.containsKey(2, 2));
        assertTrue("The wrong value was returned by the removal feature", tolM.tolEquals(hmm.removeKey(2, 2), 4.0));
        assertTrue("The key should exist", hmm.containsKey(2, 1));
        assertFalse("There shouldn't be a key", hmm.containsKey(2, 2));
        assertTrue("The wrong value was returned by the removal feature", tolM.tolEquals(hmm.removeKey(2, 1), 3.0));
        assertFalse("There shouldn't be a key", hmm.containsKey(2, 1));
        assertFalse("There shouldn't be a key", hmm.containsKey(2, 2));
    }

    @Test
    public void testPrettyToString() {
        hmm.put(1, 2, 2.123456);
        assertTrue("The print wasn't properly formated", hmm.prettyToString(false).trim().equals("1 2 2.12"));
    }

    @Test
    public void testPrettyToStringChar() {
        hmm.put(65, 80, 42.5);
        assertTrue("The print wasn't properly formated", hmm.prettyToString(true).trim().equals("A P 42.5"));
    }

}

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
package io.tessilab.oss.openutils.combinatorial;

import static org.junit.Assert.*;
import io.tessilab.oss.openutils.combinatorial.Distributions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestDistributions {

    @Test
    public void testConstuctorGet() {
        List<List<Integer>> l = new ArrayList<>();
        ArrayList<Integer> ll1 = new ArrayList<>();
        ll1.add(1);
        ll1.add(3);
        ll1.add(5);
        ArrayList<Integer> ll2 = new ArrayList<>();
        ll2.add(1);
        ll2.add(13);
        l.add(ll1);
        l.add(ll2);
        Distributions d = new Distributions(l);

        assertEquals(l, d.getDistribs());
    }

}

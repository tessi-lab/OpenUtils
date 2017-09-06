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
import io.tessilab.oss.openutils.combinatorial.Combinations;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestCombinations {

    private static int size = 1000;
    private static int[] factTab = new int[size];
    private static int currentLength = 0;
    static {
        Arrays.fill(factTab, -1);
    }

    public static int fact(int k) {
        if (k >= currentLength) {
            computeFact(k);
            currentLength = k + 1;
        }
        return factTab[k];
    }

    private static void computeFact(int k) {
        if (k == 0)
            factTab[0] = 1;
        else {
            if (factTab[k - 1] == -1)
                computeFact(k - 1);

            factTab[k] = k * factTab[k - 1];
        }
    }

    @Test
    public void testComputeBinomialCombinations() {
        oneBinomTest(3, 5);
        oneBinomTest(1, 10);
        oneBinomTest(4, 4);
        oneBinomTest(0, 4);
    }

    @Test
    public void testComputeBinomialCombinationsLimit() {
        oneBinomTest(1, 1);
        oneBinomTest(0, 0);
        List<List<Integer>> l = Combinations.computeBinomialCombinations(4, 3);
        assertEquals(0, l.size());
    }

    private void oneBinomTest(int k, int n) {
        List<List<Integer>> l = Combinations.computeBinomialCombinations(k, n);
        int binom = fact(n) / (fact(k) * fact(n - k));
        assertEquals(binom, l.size());
    }

    @Test
    public void testDistributeKAmongN() {
        int k = 10;

        // simple
        Distributions distribs = Combinations.distributeKAmongN(k, 4, -1);
        testSumMax(distribs, k, k);

        // with max distribution per receiver
        int maxPerReceiver = 4;
        distribs = Combinations.distributeKAmongN(k, 4, maxPerReceiver);
        testSumMax(distribs, k, maxPerReceiver);

        // with max distribution per receiver too low
        // is augmented to allow zeros
        int maxPerReceiver2 = 3;
        distribs = Combinations.distributeKAmongN(k, 4, maxPerReceiver2);
        testSumMax(distribs, k, 4);
    }

    private void testSumMax(Distributions distribs, int k, int expectedMax) {
        for (List<Integer> l : distribs.getDistribs()) {
            int sum = 0;
            int max = 0;
            for (Integer x : l) {
                sum += x;
                if (x > max)
                    max = x;
            }
            assertEquals(k, sum);
            assertTrue(expectedMax >= max);
        }
    }
}

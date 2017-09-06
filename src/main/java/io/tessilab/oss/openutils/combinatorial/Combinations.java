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

import io.tessilab.oss.openutils.combinatorial.Distributions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains static methods to compute combinations and distributions.
 * 
 * @author galaad
 *
 */
public class Combinations {

    private static int precomputedValuesSize = 11;
    private static Distributions[][] distribsDefault = new Distributions[precomputedValuesSize][precomputedValuesSize];
    private static int precomputedMaxCap = 7;
    private static Distributions[][][] distribsRestricted = new Distributions[precomputedMaxCap][precomputedValuesSize][precomputedValuesSize];

    static {
        precomputeDistribs();
    }

    private Combinations() {
        // dummy constructor
    }

    /**
     * @param k
     * @param n
     * @return An array containing all the possible k-tuple (in increasing
     *         order) in [0, n-1]
     */
    public static List<List<Integer>> computeBinomialCombinations(int k, int n) {
        List<List<Boolean>> tempRes = new ArrayList<>();
        List<Boolean> initCombi = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            initCombi.add(false);
        }
        findAllKAmongN(tempRes, initCombi, k, 0);

        List<List<Integer>> methodRes = new ArrayList<>();
        for (List<Boolean> tab : tempRes) {
            methodRes.add(new ArrayList<Integer>());
            for (int x = 0; x < tab.size(); x++) {
                if (tab.get(x))
                    methodRes.get(methodRes.size() - 1).add(x);
            }
        }
        return methodRes;
    }

    /**
     * @param k
     * @param n
     * @return An array containing all the possible k-tuple (in increasing
     *         order) in [0, n-1]
     */
    public static List<List<Boolean>> computeBooleanBinomialCombinations(int k, int n) {
        ArrayList<List<Boolean>> tempRes = new ArrayList<>();
        ArrayList<Boolean> initCombi = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            initCombi.add(false);
        }
        findAllKAmongN(tempRes, initCombi, k, 0);
        return tempRes;
    }

    /**
     * @param res
     *            An array in which we put the results. Must be initialized.
     * @param combi
     *            A boolean array which size is n. Must be initialized to false.
     * @param k
     * @param indexCour
     *            the starting index at which the value can be true. Should be
     *            initialized to 0.
     */
    private static void findAllKAmongN(List<List<Boolean>> res, List<Boolean> combi, int k, int indexCour) {
        // terminal case
        if (indexCour == combi.size()) {
            if (k == 0)
                res.add(combi);
            return;
        }

        // general case
        findAllKAmongN(res, new ArrayList<Boolean>(combi), k, indexCour + 1);
        combi.set(indexCour, true);
        findAllKAmongN(res, new ArrayList<Boolean>(combi), k - 1, indexCour + 1);
    }

    /**
     * Distribute k among n, in all the possible fashion.
     * 
     * @param k
     *            The number of element to distribute
     * @param n
     *            The number of receivers.
     * @param maxDistribPerReceiver
     *            Maximum distributed to a receiver. It will be adjusted up when
     *            not possible. If <b>-1 (negative)</b>, does not use.
     * @return A list of array of possible distributions of k among n. We can
     *         give 0 element or all the element to a receiver.
     */
    public static Distributions distributeKAmongN(int k, int n, int maxDistribPerReceiver) {
        if (maxDistribPerReceiver >= 0) {
            if (k >= precomputedValuesSize || n >= precomputedValuesSize || maxDistribPerReceiver >= precomputedMaxCap)
                return new Distributions(doDistributeKAmongN(k, n, maxDistribPerReceiver));
            else
                return distribsRestricted[maxDistribPerReceiver][k][n];
        } else {
            if (k >= precomputedValuesSize || n >= precomputedValuesSize)
                return new Distributions(doDistributeKAmongN(k, n, maxDistribPerReceiver));
            else
                return distribsDefault[k][n];
        }
    }

    private static void precomputeDistribs() {
        for (int n = 0; n < precomputedValuesSize; n++) {
            for (int k = 0; k < precomputedValuesSize; k++) {
                distribsDefault[k][n] = new Distributions(doDistributeKAmongN(k, n, precomputedValuesSize));
            }
        }
        for (int l = 0; l < precomputedMaxCap; l++) {
            for (int n = 0; n < precomputedValuesSize; n++) {
                for (int k = 0; k < precomputedValuesSize; k++) {
                    distribsRestricted[l][k][n] = new Distributions(doDistributeKAmongN(k, n, l));
                }
            }
        }
    }

    private static List<List<Integer>> doDistributeKAmongN(int k, int n, int maxDistribPerReceiver) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> initDistrib = new ArrayList<>();
        for (int i = 0; i < n; i++) 
            initDistrib.add(0);

        // Check if maxDistribPerReceiver is possible,
        // if not we adjust it.
        int m = maxDistribPerReceiver;
        if (m >= 0 && n > 1 && k > (n - 1) * maxDistribPerReceiver)
            m = (int) Math.ceil((double) k / (n - 1));

        recursiveDistribKAmongN(res, initDistrib, k, 0, m);
        return res;
    }

    /**
     * @param res
     *            The list of distributions.
     * @param distrib
     *            The distribution we are building.
     * @param k
     *            The current number of element we have yet to distribute.
     * @param indexCour
     *            The current index in the distribution.
     */
    private static void recursiveDistribKAmongN(List<List<Integer>> res, List<Integer> distrib, int k, int indexCour, int maxDistribPerReceiver) {
        // ** Terminal case
        if (indexCour == distrib.size()) {
            if (k == 0)
                res.add(distrib);
            return;
        }

        // ** General case
        int m = k;
        if (maxDistribPerReceiver >= 0)
            m = Math.min(k, maxDistribPerReceiver);
        for (int p = 0; p <= m; p++) {
            distrib.set(indexCour, p);
            recursiveDistribKAmongN(res, new ArrayList<>(distrib), k - p, indexCour + 1, maxDistribPerReceiver);
        }
    }

}

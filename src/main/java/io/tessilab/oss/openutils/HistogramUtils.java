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

import java.util.ArrayList;
import java.util.List;

public class HistogramUtils {

    private static MyTolerantMath tolM = new MyTolerantMath(1e-3);

    // private dummy constructor
    private HistogramUtils() {
    }

    /**
     * @param pos
     *            The position we want to get in bounds.
     * @param histo
     *            The array that defines the bounds.
     * @return The "inbound" position. -1 if the array is empty.
     */
    protected static int inBounds(int pos, double[] histo) {
        return Math.min(Math.max(pos, 0), histo.length - 1);
    }

    /**
     * @return The max of the histogram
     */
    public static Double findHistoMax(double[] histo) {
        return findHistoMax(histo, 0, histo.length);
    }

    /**
     * @param histo
     * @param start
     *            The (included) starting position from which we look for the
     *            max.
     * @param stop
     *            The (included) last position.
     * @return The max of the histogram
     */
    public static Double findHistoMax(double[] histo, int start, int stop) {
        if (histo.length == 0)
            return null;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = inBounds(start, histo); i <= inBounds(stop, histo); i++) {
            if (tolM.tolCompare(histo[i], max) > 0) {
                max = histo[i];
            }
        }
        return max;
    }

    /**
     * @return The min of the histogram
     */
    public static Double findHistoMin(double[] histo) {
        return findHistoMin(histo, 0, histo.length);
    }

    /**
     * @param histo
     * @param start
     *            The (included) starting position from which we look for the
     *            min.
     * @param stop
     *            The (included) last position.
     * @return The min of the histogram
     */
    public static Double findHistoMin(double[] histo, int start, int stop) {
        if (histo.length == 0)
            return null;
        double min = Double.POSITIVE_INFINITY;
        for (int i = inBounds(start, histo); i <= inBounds(stop, histo); i++) {
            if (tolM.tolCompare(histo[i], min) < 0) {
                min = histo[i];
            }
        }
        return min;
    }

    /**
     * @param histo
     * @param kernel1D
     *            (must be of length odd)
     * @return
     */
    public static double[] convolveHisto(double[] histo, double[] kernel1D) {
        double[] convolve = new double[histo.length];
        int halfsize = kernel1D.length / 2;
        for (int k = 0; k < convolve.length; k++) {
            double temp = 0.0;
            for (int l = 0; l < kernel1D.length; l++) {
                if (k - halfsize + l >= 0 && k - halfsize + l < histo.length)
                    temp += histo[k - halfsize + l] * kernel1D[l];
            }
            convolve[k] = temp;
        }
        return convolve;
    }

    /**
     * Compute a local derivative in a window of size localSumWidth
     * 
     * @param histo
     *            the values to compute the derivatives on
     * @param localSumWidth
     *            the size of the moving window (should be of odd parity).
     * @return an array with the value of the local derivatives
     */
    public static double[] localDerivatives(double[] histo, int localSumWidth) {
        double[] derivs = new double[histo.length];
        final int halfWidth = localSumWidth / 2;

        int localSum = 0;
        for (int i = 0; i < halfWidth; i++) {
            if (i >= derivs.length) {
                break;
            }
            localSum += histo[i];
        }

        for (int i = 0; i < derivs.length; i++) {
            int previousLocalSum = localSum;

            int endTip = i + halfWidth;
            if (endTip < derivs.length) {
                localSum += histo[endTip];
            }

            int startTip = i - halfWidth;
            if (startTip >= 0) {
                localSum -= histo[startTip];
            }

            int diff = localSum - previousLocalSum;
            derivs[i] = diff;
        }
        return derivs;
    }

    public static List<Integer> findSplitsFromHistoZeros(double[] histo, double histoMax) {
        ArrayList<Integer> splits = new ArrayList<>();
        double threshold = 0.05 * histoMax;

        boolean zero = tolM.tolCompare(histo[0], threshold) <= 0;
        for (int k = 0; k < histo.length; k++) {
            if (zero && tolM.tolCompare(histo[k], threshold * 2) > 0) {
                zero = false;
            }

            if (!zero && tolM.tolCompare(histo[k], threshold) <= 0) {
                zero = true;
                splits.add(k);
            }
        }
        return splits;
    }
}

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
package io.tessilab.oss.openutils.distance.words;

import io.tessilab.oss.openutils.MyTolerantMath;


/**
 * http://en.wikipedia.org/wiki/Levenshtein_distance
 */
public class LevenshteinDistance implements IntDistance<String> {

    private static int CACHE_SIZE = 30;
    private MyTolerantMath tolMath;
    private int[][] cache = new int[CACHE_SIZE][CACHE_SIZE];

    public LevenshteinDistance(double tolerance) {
        tolMath = new MyTolerantMath(tolerance);
    }

    public double getNormDistance(String string1,String string2) {
    	return (1.0 - (double)getDistance(string1, string2) / Math.max(string1.length(), string2.length()));
    }
    
    @Override
    public double getDistance(String string1, String string2) {
        return getDistanceInt(string1, string2);
    }

    @Override
    public int getDistanceInt(String string1, String string2) {

        int n; // length of first string
        int m; // length of second string
        int i; // iterates through first string
        int j; // iterates through second string
        char si; // ith character of first string
        char tj; // jth character of second string
        int cost; // cost
        int[][] distance;

        // Step 1
        n = string1.length();
        m = string2.length();
        if (n == 0)
            return m;
        if (m == 0)
            return n;
        if (CACHE_SIZE <= n || CACHE_SIZE <= m)
            distance = new int[n + 1][m + 1];
        else
            distance = cache;

        // Step 2
        for (i = 0; i <= n; i++)
            distance[i][0] = i;
        for (j = 0; j <= m; j++)
            distance[0][j] = j;

        // Step 3
        for (i = 1; i <= n; i++) {
            si = string1.charAt(i - 1);

            // Step 4
            for (j = 1; j <= m; j++) {
                tj = string2.charAt(j - 1);

                // Step 5
                if (si == tj)
                    cost = 0;
                else
                    cost = 1;

                // Step 6
                // distance[i][j] = findMinimum(distance[i - 1][j] + 1,
                // distance[i][j - 1] + 1, distance[i - 1][j - 1] + cost);
                int min = distance[i - 1][j - 1] + cost;
                int c = distance[i - 1][j] + 1;
                int b = distance[i][j - 1] + 1;
                if (b < min)
                    min = b;
                if (c < min)
                    min = c;
                distance[i][j] = min;

            }
        }

        // Step 7
        return distance[n][m];
    }

    public boolean isCloseEnough(String string1, String string2, double maxDistanceAuthorized) {
        double d = getDistance(string1, string2);
        return tolMath.tolCompare(d, maxDistanceAuthorized) <= 0;
    }

    protected int findMinimum(int a, int b, int c) {
        int min = a;
        if (b < min) {
            min = b;
        }
        if (c < min) {
            min = c;
        }
        return min;
    }

    /**
     * 
     * @param str
     *            The string to consider.
     * @param minThreshold
     * 
     * @param stepIncr
     *            The step (for the string length) we use to increase the
     *            distance authorized.
     * @return The maximum distance authorized for a levenshtein distance for
     *         this string, and with those parameters. Returns 0 if the string
     *         is shorter than the threshold.
     */
    public double computeMaxDistanceAuthorized(String str, int minThreshold, int stepIncr) {
        if (str.length() < minThreshold)
            return 0;
        else
            // We use a euclidean division
            return (double) (1 + (str.length() - minThreshold) / stepIncr);
    }

}

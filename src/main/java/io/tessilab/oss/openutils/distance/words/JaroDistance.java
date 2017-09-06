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


public class JaroDistance implements Distance<String> {

    private MyTolerantMath tolMath;
	
    public JaroDistance(double tolerance) {
        tolMath = new MyTolerantMath(tolerance);
	}

	@Override
	public double getDistance(String string1, String string2) {
		int string1_len = string1.length();
		int string2_len = string2.length();

		if (string1_len == 0 && string2_len == 0)
			return 1;

		int match_distance = Integer.max(string1_len, string2_len) / 2 - 1;

		boolean[] string1_matches = new boolean[string1_len];
		boolean[] string2_matches = new boolean[string2_len];

		int matches = 0;
		int transpositions = 0;

		for (int i = 0; i < string1_len; ++i) {
			int start = Integer.max(0, i - match_distance);
			int end = Integer.min(i + match_distance + 1, string2_len);

			for (int j = start; j < end; ++j) {
				if (string2_matches[j])
					continue;
				if (string1.charAt(i) != string2.charAt(j))
					continue;
				string1_matches[i] = true;
				string2_matches[j] = true;
				++matches;
				break;
			}
		}

		if (matches == 0)
			return 0;

		int k = 0;
		for (int i = 0; i < string1_len; i++) {
			if (!string1_matches[i])
				continue;
			while (!string2_matches[k])
				++k;
			if (string1.charAt(i) != string2.charAt(k))
				transpositions++;
			++k;
		}

		return (((double) matches / string1_len) + ((double) matches / string2_len)
				+ (((double) matches - transpositions / 2.0) / matches)) / 3.0;
	}
	
	public boolean isCloseEnough(String string1, String string2, double maxDistanceAuthorized) {
        double d = getDistance(string1, string2);
        return tolMath.tolCompare(d, 1.0) <= maxDistanceAuthorized;
    }
	
}
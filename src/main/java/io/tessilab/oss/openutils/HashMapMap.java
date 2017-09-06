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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class represents a hashmap of hashmap.
 * 
 * @author galaad
 *
 * @param <T>
 *            The type of the values stored in the HashMapMap.
 */
public class HashMapMap<T> {

    private Map<Integer, Map<Integer, T>> mapMatrix;
    private DecimalFormat df;

    /**
     * Simple constructor.
     */
    public HashMapMap() {
        mapMatrix = new HashMap<>();

        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(custom);
    }

    /**
     * Retrieve the value which key is (x,y)
     * 
     * @param x
     *            The first coordinate of the key (Integer).
     * @param y
     *            The second coordinate of the key (Integer).
     * @return The values stored. null if nothing was stored in (x,y).
     */
    public T get(Integer x, Integer y) {
        Map<Integer, T> map = mapMatrix.get(x);
        if (map != null) {
            T value = map.get(y);
            if (value != null) {
                return value;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Put a value in the HashMapMatrix. If there was already a value, it is
     * overwritten.
     * 
     * @param x
     *            The first coordinate of the key (Integer).
     * @param y
     *            The second coordinate of the key (Integer).
     * @param value
     *            The value we want to store.
     */
    public void put(Integer x, Integer y, T value) {
        if (!mapMatrix.containsKey(x)) {
            mapMatrix.put(x, new HashMap<Integer, T>());
        }
        mapMatrix.get(x).put(y, value);
    }

    /**
     * Removes a value from the HashMapMatrix, which key is (x,y).
     * 
     * @param x
     *            The first coordinate of the key (Integer).
     * @param y
     *            The second coordinate of the key (Integer).
     * @return The value that was removed. null if nothing was removed.
     */
    public T removeKey(Integer x, Integer y) {
        Map<Integer, T> map = mapMatrix.get(x);
        if (map == null) {
            return null;
        }
        return map.remove(y);
    }

    /**
     * @param x
     *            The first coordinate of the key (Integer).
     * @param y
     *            The second coordinate of the key (Integer).
     * @return TRUE if the HashMapMatrix contains the key (x,y).
     */
    public boolean containsKey(Integer x, Integer y) {
        Map<Integer, T> map = mapMatrix.get(x);
        if (map == null) {
            return false;
        }
        return map.containsKey(y);
    }

    /**
     * @return The entryset on the rows of the matrix.
     */
    public Set<Entry<Integer, Map<Integer, T>>> getEntrySet() {
        return mapMatrix.entrySet();
    }

    /**
     * @param isIndexChar
     *            Set this parameter to TRUE if you want to print the keys as if
     *            the integer are to be considered characters.
     * @return A String containing the informations of the HashMapMatrix. The
     *         format is x y1 v1 y2 v2 ...
     */
    public String prettyToString(boolean isIndexChar) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Map<Integer, T>> entry : mapMatrix.entrySet()) {
            if (entry.getValue().size() > 0) {
                if (isIndexChar) {
                    builder.append((char) (int) entry.getKey() + " ");
                } else {
                    builder.append((int) entry.getKey() + " ");
                }
                for (Map.Entry<Integer, T> pair : entry.getValue().entrySet()) {
                    if (isIndexChar) {
                        builder.append((char) (int) pair.getKey() + " ");
                    } else {
                        builder.append((int) pair.getKey() + " ");
                    }
                    T value = pair.getValue();
                    if (value instanceof Double) {
                        builder.append(df.format(value) + " ");
                    } else {
                        builder.append(value + " ");
                    }
                }
                builder.append('\n');
            }
        }
        return builder.toString();
    }

}

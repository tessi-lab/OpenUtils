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

import java.util.Collection;

/**
 * This class allow us to do some mathematical operations with a set tolerance.
 * 
 * @author galaad
 *
 */
public class MyTolerantMath {

    private double tolerance;

    /**
     * Constructor.
     * 
     * @param tolerance
     *            The tolerance we will use for the mathematical operations.
     */
    public MyTolerantMath(double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * @return The tolerance used.
     */
    public double getTolerance() {
        return this.tolerance;
    }

    /**
     * 
     * @param a First double
     * @param b Second double
     * @return TRUE if a is equal to b with a tolerance
     */
    public boolean tolEquals(double a, double b) {
        return Math.abs(a - b) <= tolerance;
    }

    /**
     * 
     * @param a The first double
     * @param b The second double
     * @return 0 if a is equal to b with a tolerance. A value less than zero if
     *         a is less than b. A value greater than zero if a is greater than
     *         b.
     */
    public int tolCompare(double a, double b) {
        return tolEquals(a, b) ? 0 : Double.compare(a, b);
    }

    /**
     * Check whether a double <b>x</b> is between <b>d1</b> and <b>d2</b>. Not
     * Strict (can be equal to the extrema).
     * 
     * @param x The value to test
     * @param d1 To lowest border
     * @param d2 The higher border
     * @return True if x is between d1 and d2, false otherwise.
     */
    public boolean tolIsInRange(double x, double d1, double d2) {
        return tolCompare(x, d1) >= 0 && tolCompare(x, d2) <= 0;
    }

    /**
     * Check whether a collection of double <b>c</b> contains a double <b>x</b>.
     * 
     * @param x
     *            The double we are searching.
     * @param c
     *            A collection of double.
     * @return True if the collection c contains x, false otherwise
     */
    public boolean tolContains(double x, Collection<Double> c) {
        return c.stream().filter(d -> tolEquals(d, x)).findFirst().isPresent();
    }
}

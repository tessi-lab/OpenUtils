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
package io.tessilab.oss.openutils.distance.bbox;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

import io.tessilab.oss.openutils.bbox.BBoxable;

public class TrunkedDistance implements BBoxDistance {

    // direction right down=true left up=false
    private AlignMode alignMode;
    private double factor;

    public enum AlignMode {
        HORIZONTAL, VERTICAL, NONE
    }

    public TrunkedDistance() {
        this(AlignMode.NONE, 1.0);
    }

    /**
     * @param alignMode
     *            Defines the direction of search.
     * @param factor
     *            Amplify the search in that distance. For good results, set at
     *            least 4.0;
     */
    public TrunkedDistance(AlignMode alignMode, double factor) {
        super();
        this.alignMode = alignMode;
        this.factor = factor;
    }

    @Override
    public double computeDistance(BBoxable bb1, BBoxable bb2) {
        return trunkedDistance(bb1.getBBox(), bb2.getBBox());
    }

    /**
     * 
     * @param rect1
     * @param rect2
     * @return The trunked distance between the rect1 and the rect2.</br> If
     *         they intersect, return 0.
     * 
     */
    private double trunkedDistance(Rectangle2D rect1, Rectangle2D rect2) {
        if (rect1.intersects(rect2)) {
            return 0;
        }

        Point[] points = ClosestDistance.getClosestPoints(rect1, rect2);
        double distX = Math.abs(points[0].getX() - points[1].getX());
        double distY = Math.abs(points[0].getY() - points[1].getY());

        if (alignMode == AlignMode.HORIZONTAL)
            distX = distX / factor;
        else if (alignMode == AlignMode.VERTICAL)
            distY = distY / factor;
        // nothing to do for AlignMode.NONE

        return distX * distY + distX + distY;
    }

    public AlignMode getAlignMode() {
        return this.alignMode;
    }

    public void setAlignMode(AlignMode alignMode) {
        this.alignMode = alignMode;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double getFactor() {
        return this.factor;
    }

}

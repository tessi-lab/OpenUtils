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

import io.tessilab.oss.openutils.MyTolerantMath;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import io.tessilab.oss.openutils.bbox.BBoxable;

public class ClosestDistance implements BBoxDistance {

    private static MyTolerantMath tm = new MyTolerantMath(1E-5);

    @Override
    public double computeDistance(BBoxable bb1, BBoxable bb2) {
        if (bb1.intersects(bb2)) {
            return 0;
        }
        Point[] points = getClosestPoints(bb1.getBBox(), bb2.getBBox());
        return points[0].distance(points[1]);
    }

    public static Point[] getClosestPoints(Rectangle2D word1, Rectangle2D word2) {
        Point[] points = new Point[2];
        double dx1, dx2, dy1, dy2;
        double cX1, cX2, cY1, cY2, meanX, meanY;

        cX1 = word1.getCenterX();
        cX2 = word2.getCenterX();
        // is right positioned
        if (tm.tolCompare(word1.getMaxX(), word2.getMinX()) < 0) {
            dx1 = word1.getWidth() / 2.0;
            dx2 = -word2.getWidth() / 2.0;
        } // is left positioned
        else if (tm.tolCompare(word1.getMinX(), word2.getMaxX()) > 0) {
            dx1 = -word1.getWidth() / 2.0;
            dx2 = word2.getWidth() / 2.0;
        } // intersection X so we find the distance between the centers / 2
        else {
            meanX = (cX1 - cX2) / 2.0;
            dx1 = -meanX;
            dx2 = meanX;
        }
        cX1 = cX1 + dx1;
        cX2 = cX2 + dx2;
        if (Double.compare(cX1, cX2) == 0) {
            if (Double.compare(cX1, word2.getMinX()) < 0) {
                cX1 += word2.getMinX() - cX1;
                cX2 += word2.getMinX() - cX2;
            } else if (Double.compare(cX1, word2.getMaxX()) > 0) {
                cX1 += word2.getMaxX() - cX1;
                cX2 += word2.getMaxX() - cX2;
            }
        }

        cY1 = word1.getCenterY();
        cY2 = word2.getCenterY();
        // is below positioned
        if (tm.tolCompare(word1.getMaxY(), word2.getMinY()) < 0) {
            dy1 = word1.getHeight() / 2.0;
            dy2 = -word2.getHeight() / 2.0;
        } // is up positioned
        else if (tm.tolCompare(word1.getMinY(), word2.getMaxY()) > 0) {
            dy1 = -word1.getHeight() / 2.0;
            dy2 = word2.getHeight() / 2.0;
        } // intersection Y
        else {
            meanY = (cY1 - cY2) / 2.0;
            dy1 = -meanY;
            dy2 = meanY;
        }
        cY1 = cY1 + dy1;
        cY2 = cY2 + dy2;

        if (Double.compare(cY1, cY2) == 0) {
            if (Double.compare(cY1, word2.getMinY()) < 0) {
                cY1 += word2.getMinY() - cY1;
                cY2 += word2.getMinY() - cY2;
            } else if (Double.compare(cY1, word2.getMaxY()) > 0) {
                cY1 += word2.getMaxY() - cY1;
                cY2 += word2.getMaxY() - cY2;
            }
        }

        points[0] = new Point((int) cX1, (int) cY1);
        points[1] = new Point((int) cX2, (int) cY2);
        return points;
    }

}

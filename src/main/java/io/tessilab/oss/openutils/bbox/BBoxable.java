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
package io.tessilab.oss.openutils.bbox;


import io.tessilab.oss.openutils.hocr.HTMLConstants;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * A class representing a object which has a bounding box.</br>For more useful
 * methods, see BBoxableUtils.
 * 
 * @author galaad
 */
@FunctionalInterface
public interface BBoxable {

    public Rectangle2D getBBox();

    public default double getX1() {
        return getBBox().getX();
    }

    public default double getX2() {
        return getBBox().getMaxX();
    }

    public default double getY1() {
        return getBBox().getY();
    }

    public default double getY2() {
        return getBBox().getMaxY();
    }

    public default double getWidth() {
        return getBBox().getWidth();
    }

    public default double getHeight() {
        return getBBox().getHeight();
    }

    public default double getCenterX() {
        return getBBox().getCenterX();
    }

    public default double getCenterY() {
        return getBBox().getCenterY();
    }

    public default double getArea() {
        return (double) getWidth() * getHeight();
    }

    public default String getBBoxHocrString() {
        int x1 = (int) getX1();
        int y1 = (int) getY1();
        int x2 = (int) getX2();
        int y2 = (int) getY2();
        return HTMLConstants.BBOX + " " + x1 + " " + y1 + " " + x2 + " " + y2;
    }

    public default void applyTransform(AffineTransform transform) {
        getBBox().setFrame(transform.createTransformedShape(getBBox()).getBounds());
    }

    public default boolean intersects(BBoxable other) {
        return getBBox().intersects(other.getBBox());
    }

    public default boolean bboxEquals(BBoxable other) {
        return getBBox().equals(other.getBBox());
    }
}

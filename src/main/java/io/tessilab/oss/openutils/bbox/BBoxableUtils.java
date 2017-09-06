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

import io.tessilab.oss.openutils.MyTolerantMath;
import io.tessilab.oss.openutils.distance.bbox.BBoxDistance;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BBoxableUtils {

    private static final MyTolerantMath TOL_MATH = new MyTolerantMath(1e-3);
    
    private BBoxableUtils() {
        // private dummy constructor
    }

    /**
     * @param elements
     *            A list of elements with a bounding box.
     * @param distance
     *            The distance used for this segmentation.
     * @param threshold
     *            The distance to consider under which two elements are in the
     *            same component.
     * @return A list of connected components (represented as list of elements).
     */
    public static <T extends BBoxable> List<List<T>> computeConnectedComponents(List<T> elements, BBoxDistance distance, double threshold) {
        Set<T> notConnectedElements = new HashSet<>(elements);
        List<List<T>> connectedGroups = new ArrayList<>();
        while (!notConnectedElements.isEmpty()) {
            // Reset the pool with the next component starting element
            T start = notConnectedElements.iterator().next();
            List<T> currentGroupList = doConnectedComponents(notConnectedElements, start, distance, threshold);

            notConnectedElements.removeAll(currentGroupList);
            connectedGroups.add(new ArrayList<T>(currentGroupList));
        }
        return connectedGroups;
    }

    // Same but for one component.
    public static <T extends BBoxable> List<T> computeOneConnectedComponents(List<T> elements, T start, BBoxDistance distance, double threshold) {
        Set<T> notConnectedElements = new HashSet<>(elements);
        List<T> currentGroupList = doConnectedComponents(notConnectedElements, start, distance, threshold);
        return currentGroupList;
    }

    private static <T extends BBoxable> List<T> doConnectedComponents(Set<T> notConnectedElements, T start, BBoxDistance distance, double threshold) {
        Set<T> pool2Add = new HashSet<>();
        pool2Add.add(start);
        Set<T> currentGroupList = new HashSet<>();
        while (!pool2Add.isEmpty()) {
            // Get the first element from the pool
            T element = pool2Add.iterator().next();
            pool2Add.remove(element);
            // Add all its non-connected neighbors to the pool
            for (T mayBeNeighbor : notConnectedElements) {
                if (!element.equals(mayBeNeighbor) && !pool2Add.contains(mayBeNeighbor) && !currentGroupList.contains(mayBeNeighbor)) {
                    double dist = distance.computeDistance(element, mayBeNeighbor);
                    if (TOL_MATH.tolCompare(dist, threshold) < 0)
                        pool2Add.add(mayBeNeighbor);
                }
            }
            currentGroupList.add(element);
        }
        return new ArrayList<T>(currentGroupList);
    }

    /**
     * Go through all the element, and merge a element into a larger element if
     * that element contains at least <b>percentContainedMin</b> percent of it.
     * 
     * @param elements
     *            A list of element with a bounding box.
     * @param pourcentContainedMin
     *            Must be between 0 and 1.
     * @return A list of list of BBoxable that could be merged together.
     */
    public static <T extends BBoxable> List<List<T>> mergeInsideBBoxes(List<T> elements, double pourcentContainedMin) {
        if (pourcentContainedMin < 0 || pourcentContainedMin > 1)
            throw new IllegalArgumentException();

        Map<Rectangle2D, List<T>> mergingBBoxes = new HashMap<>();
        for (T element : elements) {
            List<T> l = mergingBBoxes.get(element.getBBox());
            if (l == null) {
                l = new ArrayList<>();
                mergingBBoxes.put(element.getBBox(), l);
            }
            l.add(element);
        }
        boolean needRelaunch;
        do {
            needRelaunch = false;
            for (Map.Entry<Rectangle2D, List<T>> maybeContained : mergingBBoxes.entrySet()) {
                double mcArea = maybeContained.getKey().getWidth() * maybeContained.getKey().getHeight();

                Map.Entry<Rectangle2D, List<T>> smallestContainer = null;
                double smallestArea = Integer.MAX_VALUE;
                for (Map.Entry<Rectangle2D, List<T>> container : mergingBBoxes.entrySet()) {
                    // Test if the current group is contained in another group
                    Rectangle2D intersection = new Rectangle2D.Double();
                    Rectangle2D.intersect(maybeContained.getKey(), container.getKey(), intersection);

                    if (!container.equals(maybeContained) && !intersection.isEmpty()) {
                        double intersectionArea = intersection.getWidth() * intersection.getHeight();
                        if (TOL_MATH.tolCompare(intersectionArea, pourcentContainedMin * mcArea) >= 0) {
                            double area = container.getKey().getWidth() * container.getKey().getHeight();
                            if (area < smallestArea) {
                                smallestContainer = container;
                                smallestArea = area;
                            }
                        }
                    }

                    if (smallestContainer != null) {
                        List<T> mergedList = new ArrayList<>();
                        mergedList.addAll(smallestContainer.getValue());
                        mergedList.addAll(maybeContained.getValue());
                        mergingBBoxes.remove(smallestContainer.getKey());
                        mergingBBoxes.remove(maybeContained.getKey());

                        Rectangle2D mergedRectangle = new Rectangle2D.Double();
                        Rectangle2D.union(smallestContainer.getKey(), maybeContained.getKey(), mergedRectangle);
                        mergingBBoxes.put(mergedRectangle, mergedList);
                        needRelaunch = true;
                        break;
                    }
                }
                if (needRelaunch)
                    break;
            }

        } while (needRelaunch);

        return new ArrayList<>(mergingBBoxes.values());
    }

    /***
     * 
     * @param elements
     *            list of elements
     * @param rectangle
     *            bounds to compare
     * @param allowedRate
     *            Must be between 0 and 1.
     * @return list of elements in the given rectangle and elements with at
     *         least<b>allowedRate</b> percentage into
     */
    public static <T extends BBoxable> List<T> getElementsIn(List<T> elements, BBoxable rectangle, double allowedRate) {
        if (allowedRate < 0 || allowedRate > 1)
            throw new IllegalArgumentException();

        List<T> ins = new ArrayList<>();
        for (T elem : elements) {
            if (rectangle.getBBox().contains(elem.getBBox())) {
                ins.add(elem);
            } else {
                double elemArea = elem.getWidth() * elem.getHeight();
                double intersection = getIntersectionArea(rectangle, elem);
                if (TOL_MATH.tolCompare(intersection, allowedRate * elemArea) >= 0)
                    ins.add(elem);
            }
        }
        return ins;
    }

    /**
     * @return the area of intersection between r1 and r2
     */
    public static double getIntersectionArea(BBoxable r1, BBoxable r2) {
        Rectangle2D intersection = new Rectangle2D.Double();
        Rectangle2D.intersect(r1.getBBox(), r2.getBBox(), intersection);
        if (!intersection.isEmpty()) {
            return intersection.getWidth() * intersection.getHeight();
        }
        return 0.0;
    }

    /**
     * @param bbs
     *            A list of BBoxable
     * @return The union of all the bboxes of the elements of bbs. </br>
     *         <b>null</b> if bbs was empty.
     */
    public static <T extends BBoxable> Rectangle2D bBoxUnion(List<T> bbs) {
        if (bbs == null || bbs.isEmpty())
            return null;

        Rectangle2D union = (Rectangle2D) bbs.get(0).getBBox().clone();
        for (int i = 1; i < bbs.size(); i++) {
            Rectangle2D.union(bbs.get(i).getBBox(), union, union);
        }
        return union;
    }

    public static <T extends BBoxable> Comparator<T> getX1Comparator() {
        return Comparator.comparingDouble(T::getX1);
    }

    public static <T extends BBoxable> Comparator<T> getX2Comparator() {
        return Comparator.comparingDouble(T::getX2);
    }

    public static <T extends BBoxable> Comparator<T> getY1Comparator() {
        return Comparator.comparingDouble(T::getY1);
    }

    public static <T extends BBoxable> Comparator<T> getY2Comparator() {
        return Comparator.comparingDouble(T::getY2);
    }

    public static <T extends BBoxable> Comparator<T> getLectureComparator() {
        return Comparator.comparingDouble(T::getY1).thenComparing(getX1Comparator());
    }

}

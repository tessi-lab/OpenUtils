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

import io.tessilab.oss.openutils.bbox.BBoxer;
import io.tessilab.oss.openutils.bbox.BBoxableUtils;
import io.tessilab.oss.openutils.bbox.BBoxable;
import static org.junit.Assert.*;
import io.tessilab.oss.openutils.distance.bbox.EuclidianCenterBBoxDistance;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class TestBBoxableUtils {

    private List<BBoxable> rects = Arrays.asList(new BBoxer(0, 0, 5, 5), new BBoxer(10, 10, 5, 5));

    @Test
    public void testComputeConnectedComponentsBasic() {
        List<List<BBoxable>> groups = BBoxableUtils.computeConnectedComponents(rects, new EuclidianCenterBBoxDistance(), 15);
        assertEquals(1, groups.size());
        List<BBoxable> g1 = groups.get(0);
        assertEquals(2, g1.size());
        assertTrue(g1.contains(rects.get(0)));
        assertTrue(g1.contains(rects.get(1)));

    }

    @Test
    public void testComputeConnectedComponentsTooFar() {
        List<List<BBoxable>> groups = BBoxableUtils.computeConnectedComponents(rects, new EuclidianCenterBBoxDistance(), 1);
        assertEquals(2, groups.size());
        assertEquals(1, groups.get(0).size());
        assertEquals(1, groups.get(1).size());
    }

    @Test
    public void testBBoxUnion() {
        Rectangle2D r = BBoxableUtils.bBoxUnion(rects);
        // save the original bboxes
        List<Rectangle2D> originalBboxes = rects.stream().map(bb -> (Rectangle2D) bb.getBBox().clone()).collect(Collectors.toList());

        Rectangle2D manualUnion = (Rectangle2D) rects.get(0).getBBox().clone();
        manualUnion = manualUnion.createUnion(rects.get(1).getBBox());
        assertEquals(manualUnion, r);

        // test if the bboxes are the same after the union
        for (int k = 0; k < rects.size(); k++) {
            assertEquals(originalBboxes.get(k), rects.get(k).getBBox());
        }
    }

    @Test
    public void testMergeInsideBBoxesOKSimple() {
        List<BBoxable> rrs = Arrays.asList(new BBoxer(5, 5, 5, 5), new BBoxer(6, 6, 3, 4));
        List<List<BBoxable>> groups = BBoxableUtils.mergeInsideBBoxes(rrs, 1.0);
        assertEquals(1, groups.size());
        assertEquals(2, groups.get(0).size());
    }

    @Test
    public void testMergeInsideBBoxesOverlaps() {
        List<BBoxable> rrs = Arrays.asList(new BBoxer(5, 5, 5, 5), new BBoxer(6, 6, 5, 5));
        List<List<BBoxable>> groups = BBoxableUtils.mergeInsideBBoxes(rrs, 1.0);
        assertEquals(2, groups.size());
        assertEquals(1, groups.get(0).size());
        assertEquals(1, groups.get(1).size());

        Rectangle2D inter = new Rectangle2D.Double();
        Rectangle2D.intersect(rrs.get(0).getBBox(), rrs.get(1).getBBox(), inter);
        double interArea = inter.getHeight() * inter.getWidth();
        double ratio = interArea / (5 * 5); // 0.64
        groups = BBoxableUtils.mergeInsideBBoxes(rrs, ratio);
        assertEquals(1, groups.size());
        assertEquals(2, groups.get(0).size());

        groups = BBoxableUtils.mergeInsideBBoxes(rrs, ratio + 0.01);
        assertEquals(2, groups.size());
        assertEquals(1, groups.get(0).size());
        assertEquals(1, groups.get(1).size());
    }

    @Test
    public void testMergeInsideBBoxesDistant() {
        List<BBoxable> rrs = Arrays.asList(new BBoxer(2, 2, 2, 2), new BBoxer(10, 10, 5, 5));
        List<List<BBoxable>> groups = BBoxableUtils.mergeInsideBBoxes(rrs, 0);
        assertEquals(2, groups.size());
        assertEquals(1, groups.get(0).size());
        assertEquals(1, groups.get(1).size());
    }

    @Test
    public void testCompare() {
        BBoxable tr0 = new BBoxer(4, 5, -3, 17);
        BBoxable tr1 = new BBoxer(2, 6, 3, 8);
        BBoxable tr2 = new BBoxer(8, 2, 6, 3);
        BBoxable tr3 = new BBoxer(3, 4, 6, 13);

        List<BBoxable> l = Arrays.asList(tr0, tr1, tr2, tr3);
        Collections.shuffle(l);

        Collections.sort(l, BBoxableUtils.getX1Comparator());
        assertEquals(tr1, l.get(0));
        assertEquals(tr3, l.get(1));
        assertEquals(tr0, l.get(2));
        assertEquals(tr2, l.get(3));

        Collections.sort(l, BBoxableUtils.getX2Comparator());
        assertEquals(tr0, l.get(0));
        assertEquals(tr1, l.get(1));
        assertEquals(tr3, l.get(2));
        assertEquals(tr2, l.get(3));

        Collections.sort(l, BBoxableUtils.getY1Comparator());
        assertEquals(tr2, l.get(0));
        assertEquals(tr3, l.get(1));
        assertEquals(tr0, l.get(2));
        assertEquals(tr1, l.get(3));

        Collections.sort(l, BBoxableUtils.getY2Comparator());
        assertEquals(tr2, l.get(0));
        assertEquals(tr1, l.get(1));
        assertEquals(tr3, l.get(2));
        assertEquals(tr0, l.get(3));
    }

    @Test
    public void testCompareEquals() {
        BBoxable tr0 = new BBoxer(1, 2, 2, 2);
        BBoxable tr1 = new BBoxer(1, 6, 1, 8);
        List<BBoxable> l = Arrays.asList(tr0, tr1);

        // Keep the elements in place
        Collections.sort(l, BBoxableUtils.getX1Comparator());
        assertEquals(tr0, l.get(0));
        assertEquals(tr1, l.get(1));
    }

    @Test
    public void testGetElementsIn() {
        BBoxable elem1 = new BBoxer(2, 2, 10, 10);// in
        BBoxable elem2 = new BBoxer(85, 40, 20, 10);// in , a little out
        BBoxable elem3 = new BBoxer(90, 50, 20, 10);// barely enough in
        BBoxable elem4 = new BBoxer(91, 50, 20, 10);// barely not enough in
        BBoxable elem5 = new BBoxer(120, 50, 10, 10);// outise
        List<BBoxable> elements = Arrays.asList(elem1, elem2, elem3, elem4, elem5);

        BBoxable bounds = new BBoxer(0, 0, 100, 100);
        List<BBoxable> res = BBoxableUtils.getElementsIn(elements, bounds, 0.5);
        assertTrue(res.contains(elem1));
        assertTrue(res.contains(elem2));
        assertTrue(res.contains(elem3));
        assertFalse(res.contains(elem4));
        assertFalse(res.contains(elem5));
    }

}

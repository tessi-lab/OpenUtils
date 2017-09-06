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
import io.tessilab.oss.openutils.bbox.BBoxable;
import static org.junit.Assert.*;

import java.awt.geom.AffineTransform;

import org.junit.Before;
import org.junit.Test;

public class TestBBoxable {
    private BBoxable tr;
    private AffineTransform af;

    @Before
    public void buildUp() {
        tr = new BBoxer(1, 1, 10, 10);
        af = new AffineTransform();
    }

    @Test
    public void testApplyTransformTranslate() {
        af.translate(2, 3);
        tr.applyTransform(af);
        assertTrue(new BBoxer(3, 4, 10, 10).bboxEquals(tr));
    }

    @Test
    public void testApplyTransformScale2() {
        af.scale(2, 4);
        tr.applyTransform(af);
        assertTrue(new BBoxer(2, 4, 20, 40).bboxEquals(tr));
    }

}

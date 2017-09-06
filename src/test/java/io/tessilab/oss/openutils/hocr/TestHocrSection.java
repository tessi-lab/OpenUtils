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
package io.tessilab.oss.openutils.hocr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import io.tessilab.oss.openutils.MyTolerantMath;
import io.tessilab.oss.openutils.testing.HocrTestUtilities;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestHocrSection {
    private MyTolerantMath tolM = new MyTolerantMath(1e-3);
    private HocrPage page;
    private List<HocrXWord> words;

    @Before
    public void buildUp() {
        page = HocrTestUtilities.loadPageFromResource("/documents/greatReceipt.html");
        words = page.getAllWords();
    }

    @Test
    public void testContainsAllWords() {
        for (HocrXWord hxw : words) {
            assertTrue("Error: Every word's bounding box should be contain in the page's bounding box", page.getBBox().contains(hxw.getBBox()));
        }
    }

    @Test
    public void testBBoxArea() {
        assertTrue(tolM.tolEquals(page.getWidth() * page.getHeight(), page.getArea()));
    }

    @Test
    public void testEqualityHashCode() {
        assertNotEquals(page, null);
        assertNotEquals(page, new Double(1.0));
        assertEquals(page, page);

        HocrPage page2 = HocrTestUtilities.loadPageFromResource("/documents/greatReceipt.html");
        assertEquals(page, page2);

        Integer h1 = page.hashCode();
        Integer h2 = page2.hashCode();
        assertEquals(true, h1.equals(h2));
    }

}

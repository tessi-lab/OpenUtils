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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.tessilab.oss.openutils.FileUtils;
import io.tessilab.oss.openutils.testing.HocrTestUtilities;

import java.awt.Rectangle;
import java.io.IOException;

import org.junit.Test;

public class TestHocrPage {

    @Test
    public void testConstructor() {
        HocrDocument hd = new HocrDocument();
        assertEquals(0, hd.getNbOfPages());
        HocrPage hp = new HocrPage(2, hd);
        assertEquals(1, hd.getNbOfPages());
        assertEquals(2, hp.getPageNumber());
        assertEquals("page_0", hp.getId());
        assertEquals(hd, hp.getParentDocument());
        assertEquals(hp, hp.getPage());
    }

    @Test
    public void testRewriteAllIds() {
        try {
            HocrDocument doc = new HocrDocument(FileUtils.fileToString("/documents/greatReceipt.html", true));
            HocrPage hp = doc.getPage(0);
            hp.rewriteAllIds();
            assertTrue(HocrTestUtilities.uniqueAllSections(doc));
            assertEquals(0, hp.getPageNumber());
            assertEquals("page_0", hp.getId());
        } catch (IOException ioe) {
            fail("Error while parsing the html.");
        }
    }

    /**
     * Test the update of the bounding boxes of the inner section when we add
     * some words. The bbox of the page should not change.
     * 
     * @throws HocrSyntaxException
     */
    @Test
    public void testAddWordsRect() throws HocrSyntaxException {
        HocrPage hp = new HocrPage(2, new HocrDocument());
        Rectangle initPageBBox = new Rectangle(hp.getBBox());

        Rectangle wordBbox = new Rectangle(0, 10, 30, 40);
        HocrXWord w = HocrTestUtilities.createWord("dummy0", wordBbox);
        hp.addWord(w);
        assertEquals(hp, w.getPage());

        HocrSection child = hp.getChilds().get(0); // first section (c area)
        assertEquals(wordBbox, child.getBBox());
        assertEquals(initPageBBox, hp.getBBox());

        Rectangle wordBbox2 = new Rectangle(44, 23, 4, 122);
        HocrXWord w2 = HocrTestUtilities.createWord("dummy2", wordBbox2);
        hp.addWord(w2);
        assertEquals(hp, w2.getPage());

        assertEquals(wordBbox.union(wordBbox2), child.getBBox());
        assertEquals(initPageBBox, hp.getBBox());
    }

}

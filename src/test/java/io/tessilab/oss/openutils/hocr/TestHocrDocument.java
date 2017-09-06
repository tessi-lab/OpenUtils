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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import io.tessilab.oss.openutils.FileUtils;
import io.tessilab.oss.openutils.testing.HocrTestUtilities;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class TestHocrDocument {

    @Test
    public void testBasics() {
        HocrDocument hd = new HocrDocument();
        assertEquals(0, hd.getNbOfPages());
        assertNull(hd.getPage(0));

        HocrPage hp = hd.createNewPage(0);
        assertEquals(1, hd.getNbOfPages());
        HocrPage res = hd.getPage(0);
        assertEquals(hp, res);
        assertEquals(0, res.getPageNumber());
    }

    @Test
    public void testParseMeta2Pages() throws IOException {
        String resourcePath = "/documents/meta2Pages.html";
        String htmlContent = FileUtils.fileToString(resourcePath, true);
        HocrDocument hd = new HocrDocument(htmlContent);

        assertEquals(2, hd.getNbOfPages());

        // page 0
        HocrPage page0 = hd.getPage(0);
        assertEquals(0, page0.getPageNumber());

        // page 0: meta
        HocrMeta meta0 = page0.getMeta();
        assertEquals(2, meta0.getNbOfNTContents());

        // page 1
        HocrPage page1 = hd.getPage(1);
        assertEquals(1, page1.getPageNumber());

        // page 1: meta
        HocrMeta meta1 = page1.getMeta();
        assertEquals(4, meta1.getNbOfNTContents());
    }

    @Test
    public void testCreateNewPage() {
        HocrDocument hd = new HocrDocument();
        assertEquals(0, hd.getNbOfPages());

        HocrPage hp0 = hd.createNewPage(0);
        assertEquals(1, hd.getNbOfPages());
        assertEquals("page_0", hp0.getId());

        HocrPage hp1 = hd.createNewPage(1);
        assertEquals(2, hd.getNbOfPages());
        assertEquals("page_1", hp1.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNewPageKOSameNumber() {
        HocrDocument hd = new HocrDocument();
        hd.createNewPage(0);
        hd.createNewPage(0);
    }

    @Test
    public void testAdoptPage() {
        HocrDocument hd = new HocrDocument();
        hd.createNewPage(0);
        hd.createNewPage(1);

        HocrDocument hd2 = new HocrDocument();
        HocrPage pp1 = hd2.createNewPage(0);
        HocrPage pp2 = hd2.createNewPage(1);

        // The first document adopts the pages from the second document
        hd.adoptPage(pp1, 2);
        hd.adoptPage(pp2, 3);

        assertEquals(4, hd.getNbOfPages());
        assertEquals("page_2", hd.getPage(2).getId());
        assertEquals("page_3", hd.getPage(3).getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdoptPageKOSameNumber() {
        HocrDocument hd = new HocrDocument();
        hd.createNewPage(0);
        hd.createNewPage(1);

        HocrDocument hd2 = new HocrDocument();
        HocrPage pp1 = hd2.createNewPage(0);

        hd.adoptPage(pp1, 0);
    }

    @Test
    public void testUnique() throws IOException {
        String htmlContent = FileUtils.fileToString("/documents/greatReceipt.html", true);
        HocrDocument hd = new HocrDocument(htmlContent);
        assertTrue(HocrTestUtilities.uniqueAllSections(hd));

        HocrPage hp = hd.getPage(0);
        hp.addWord(new HocrXWord("dummy", new Rectangle(10, 23, 49, 49), null, "topkek", 100.0));
        hp.addWord(new HocrXWord("dummy", new Rectangle(10, 23, 49, 49), null, "topkek", 100.0));
        assertTrue(HocrTestUtilities.uniqueAllSections(hd));

        String htmlContent2 = FileUtils.fileToString("/documents/meta2Pages.html", true);
        HocrDocument hd2 = new HocrDocument(htmlContent2);
        List<HocrPage> hd2Pages = hd2.getPageStream().collect(Collectors.toList());
        for (int i = 0; i < hd2Pages.size(); i++) {
            HocrPage hpp = hd2Pages.get(i);
            hd.adoptPage(hpp, hd.getNbOfPages() + i + 1);
        }
        assertTrue(HocrTestUtilities.uniqueAllSections(hd));
    }

    @Test
    public void testWriteReParse() throws IOException {
        String htmlContent = FileUtils.fileToString("/documents/meta2Pages.html", true);

        HocrDocument hd = new HocrDocument(htmlContent);

        testWriteParseWriteEquals(hd);
    }

    @Test
    public void testWriteReParseChangeIds() throws IOException {
        String htmlContent = FileUtils.fileToString("/documents/meta2Pages.html", true);

        HocrDocument hd = new HocrDocument(htmlContent);
        hd.makeEditable(); // change ids

        testWriteParseWriteEquals(hd);
    }

    @Test
    public void testWriteReParseAddWords() throws IOException, HocrSyntaxException {
        String htmlContent = FileUtils.fileToString("/documents/meta2Pages.html", true);

        HocrDocument hd = new HocrDocument(htmlContent);
        HocrXWord w = new HocrXWord("dummy", new Rectangle(0, 10, 30, 40), null, "sauce", 1.0);
        hd.getPage(0).addWord(w);

        testWriteParseWriteEquals(hd);
    }

    private void testWriteParseWriteEquals(HocrDocument hd) {
        String out = hd.toHocrString();
        HocrDocument hd2 = new HocrDocument(out);
        String out2 = hd2.toHocrString();
        assertEquals(out, out2);
    }

}

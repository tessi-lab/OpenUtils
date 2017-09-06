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

import io.tessilab.oss.openutils.hocr.HocrUtils;
import io.tessilab.oss.openutils.hocr.HocrDocument;
import io.tessilab.oss.openutils.hocr.HocrPage;
import io.tessilab.oss.openutils.hocr.HocrXWord;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.tessilab.oss.openutils.FileUtils;
import io.tessilab.oss.openutils.bbox.BBoxable;
import io.tessilab.oss.openutils.bbox.BBoxer;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class TestHocrUtils {

    @Test
    public void testFuseHOCRs() {
        try {
            HocrPage hp1 = new HocrDocument(FileUtils.fileToString("/documents/smallReceipt.html", true)).getPage(0);
            HocrPage hp2 = new HocrDocument(FileUtils.fileToString("/documents/greatReceipt.html", true)).getPage(0);
            int pageNumber = new Random().nextInt(100);
            HocrPage fusion = HocrUtils.fuseHOCRs(new HocrPage[] { hp1, hp2 }, pageNumber);

            // test page number
            assertEquals(pageNumber, fusion.getPageNumber());

            List<HocrXWord> words1 = hp1.getAllWords();
            List<HocrXWord> words2 = hp2.getAllWords();
            List<HocrXWord> wordsF = fusion.getAllWords();

            assertEquals(words1.size() + words2.size(), wordsF.size());
            for (HocrXWord w1 : words1) {
                assertTrue(wordsF.contains(w1));
            }
            for (HocrXWord w2 : words2) {
                assertTrue(wordsF.contains(w2));
            }
        } catch (IOException ioe) {
            fail("Error while parsing the html.");
        }
    }

    @Test
    public void testAddWords() throws IOException {
        try {
            HocrPage hp1 = new HocrDocument(FileUtils.fileToString("/documents/smallReceipt.html", true)).getPage(0);
            int nbWordsInit = hp1.getAllWords().size();
            AffineTransform at = new AffineTransform();

            List<String> ws = Arrays.asList("toto", "tata");
            List<BBoxable> boxes = Arrays.asList(new BBoxer(0, 5, 200, 150), new BBoxer(4, 24, 424, 555));
            List<String> addedIds = HocrUtils.addWords(hp1, ws, boxes, at);

            List<HocrXWord> allWords = hp1.getAllWords();
            assertEquals(nbWordsInit + ws.size(), allWords.size());
            for (String id : addedIds) {
                HocrXWord dummy = new HocrXWord(id, new Rectangle(), null, "dummy", 100.0);
                assertTrue(allWords.contains(dummy)); // look for the id.
            }
        } catch (IOException ioe) {
            fail("Error while parsing the html.");
        }
    }

    @Test
    public void testReadBBox() {
        Rectangle rect1 = HocrUtils.readBoundingBox("bbox 1284 300 1316 350; x_wconf 82");
        assertEquals("Bad string parsing for bounding box", 1284, rect1.x);
        assertEquals("Bad string parsing for bounding box", 300, rect1.y);
        assertEquals("Bad string parsing for bounding box", 32, rect1.width);
        assertEquals("Bad string parsing for bounding box", 50, rect1.height);

        Rectangle rect2 = HocrUtils.readBoundingBox("image \"\"; bbox 100 200 400 1050; ppageno 0");
        assertEquals("Bad string parsing for bounding box", 100, rect2.x);
        assertEquals("Bad string parsing for bounding box", 200, rect2.y);
        assertEquals("Bad string parsing for bounding box", 300, rect2.width);
        assertEquals("Bad string parsing for bounding box", 850, rect2.height);
    }

}

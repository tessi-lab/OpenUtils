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

import io.tessilab.oss.openutils.hocr.HocrSyntaxException;
import io.tessilab.oss.openutils.hocr.HocrXWord;
import io.tessilab.oss.openutils.hocr.HTMLConstants;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.tessilab.oss.openutils.MyTolerantMath;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;

public class TestHocrXWord {
    private MyTolerantMath tolM = new MyTolerantMath(1e-3);

    private HocrXWord word;

    @Before
    public void buildUp() {
        word = new HocrXWord("id_test", new Rectangle(0, 0, 100, 200), null, "IàMéRrôr", 100);
    }

    @Test
    public void testParse() throws HocrSyntaxException {
        Element elm = new Element(Tag.valueOf("span"), "");
        elm.attr(HTMLConstants.CONFIDENCE, "100");
        elm.attr("id", "id_test");
        elm.attr("title", "bbox 50 1846 237 1883"); // bounding box
        String value = "IàMéRrôr";
        elm.text(value);
        word = new HocrXWord(null, elm); // parent = null
        assertEquals(value, word.getValue());
    }

    @Test
    public void testNormalize() {
        assertEquals("Bad normalization", "iamerror", word.getNormalizedValue());
    }

    @Test
    public void testGetAllWords() {
        List<HocrXWord> words = word.getAllWords();
        assertEquals(1, words.size());
        assertEquals("word.getAllWords() should return only itself (in an array).", word, words.get(0));
    }

    @Test
    public void testToString() {
        assertEquals("Bad toString() value", word.getValue(), word.toString());
    }

    @Test
    public void testComputeMeanWeightedConfidence() throws HocrSyntaxException {
        MyTolerantMath tolM = new MyTolerantMath(1e-3);

        double meanConf = HocrXWord.computeMeanWeightedConfidence(Arrays.asList(word));
        assertTrue("Wrong computation of the mean weighted confidence.", tolM.tolEquals(meanConf, word.getConfidence()));

        HocrXWord word2 = word = new HocrXWord("id_test2", new Rectangle(50, 51, 133, 42), null, "toto", 50);
        meanConf = HocrXWord.computeMeanWeightedConfidence(Arrays.asList(word, word2));
        int l1 = word.getNormalizedValue().length();
        int l2 = word2.getNormalizedValue().length();
        assertTrue("Wrong computation of the mean weighted confidence.", tolM.tolEquals(meanConf, (word.getConfidence() * l1 + word2.getConfidence() * l2) / (l1 + l2)));
    }

    @Test
    public void testSplitOrigin() {
        assertEquals(word, word.getOrigin());

        List<HocrXWord> splits = word.split(Arrays.asList(2), 30);
        assertEquals("Bad split", "ia", splits.get(0).getNormalizedValue());
        assertEquals("Bad split", "merror", splits.get(1).getNormalizedValue());
        assertEquals("Bad split", word, splits.get(0).getOrigin());
        assertEquals("Bad split", word, splits.get(1).getOrigin());

        // Change id
        HocrXWord w3 = new HocrXWord(splits.get(0));
        w3.setId("tata");
        assertEquals(word, w3.getOrigin());
        
        // Further split
        List<HocrXWord> splits2 = splits.get(0).split(Arrays.asList(1), 30);
        assertEquals("Bad split", word, splits2.get(0).getOrigin());
        assertEquals("Bad split", word, splits2.get(1).getOrigin());
    }

    @Test
    public void testSpace() throws HocrSyntaxException {
        HocrXWord w = new HocrXWord("id0", new Rectangle(10, 0, 10, 1), null, " ", 1.0);
        assertEquals("", w.getValue());
        assertEquals("", w.getNormalizedValue());
    }

    @Test
    public void testNonAlphaNumRatio() throws HocrSyntaxException {
        oneTestNonAlphaNumRatio("salut", 0.0);
        oneTestNonAlphaNumRatio("a2r3", 0.0);
        oneTestNonAlphaNumRatio("", 0.0);
        oneTestNonAlphaNumRatio("a/", 0.5);
        oneTestNonAlphaNumRatio(".,", 1.0);
        oneTestNonAlphaNumRatio("j.'ER", 2 / 5.0);
    }

    private void oneTestNonAlphaNumRatio(String value, double ratio) {
        HocrXWord w = new HocrXWord("id0", new Rectangle(10, 0, 10, 1), null, value, 1.0);
        assertTrue(tolM.tolEquals(ratio, w.getNonAlphaNumRatio()));
    }
}

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

import io.tessilab.oss.openutils.hocr.HocrPage;
import io.tessilab.oss.openutils.hocr.HocrXWord;
import io.tessilab.oss.openutils.testing.HocrTestUtilities;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestBBoxableUtilsComparators {
    private HocrPage page;
    private List<HocrXWord> words;

    @Before
    public void buildUp() {
        page = HocrTestUtilities.loadPageFromResource("/documents/meta2Pages.html");
        words = page.getAllWords();
    }

    @Test
    public void testGetX1Comparator() {
        Collections.shuffle(words);
        Collections.sort(words, BBoxableUtils.getX1Comparator());
        for (int i = 0; i < words.size() - 1; i++) {
            assertTrue("Badly ordered word: getX1Comparator()", words.get(i).getX1() <= words.get(i + 1).getX1());
        }
    }

    @Test
    public void testGetX2Comparator() {
        Collections.shuffle(words);
        Collections.sort(words, BBoxableUtils.getX2Comparator());
        for (int i = 0; i < words.size() - 1; i++) {
            assertTrue("Badly ordered word: getX2Comparator()", words.get(i).getX2() <= words.get(i + 1).getX2());
        }
    }

    @Test
    public void testGetY1Comparator() {
        Collections.shuffle(words);
        Collections.sort(words, BBoxableUtils.getY1Comparator());
        for (int i = 0; i < words.size() - 1; i++) {
            assertTrue("Badly ordered word: getY1Comparator()", words.get(i).getY1() <= words.get(i + 1).getY1());
        }
    }

    @Test
    public void testGetY2Comparator() {
        Collections.shuffle(words);
        Collections.sort(words, BBoxableUtils.getY2Comparator());
        for (int i = 0; i < words.size() - 1; i++) {
            assertTrue("Badly ordered word: getY2Comparator()", words.get(i).getY2() <= words.get(i + 1).getY2());
        }
    }
}

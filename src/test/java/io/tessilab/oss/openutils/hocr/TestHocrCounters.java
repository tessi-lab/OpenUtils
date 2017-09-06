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

import io.tessilab.oss.openutils.hocr.HocrCounters;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestHocrCounters {

    @Test
    public void test() {
        HocrCounters c = new HocrCounters();
        assertEquals("page_0", c.nextPageId());
        assertEquals("carea_0", c.nextContentAreaId());
        assertEquals("par_0", c.nextParagraphId());
        assertEquals("line_0", c.nextLineId());
        assertEquals("word_0", c.nextWordId());

        assertEquals("word_1", c.nextWordId());
        assertEquals("word_2", c.nextWordId());

        c.resetCounters();
        assertEquals("line_0", c.nextLineId());
        assertEquals("word_0", c.nextWordId());
    }

}

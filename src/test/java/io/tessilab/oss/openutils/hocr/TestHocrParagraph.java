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

import io.tessilab.oss.openutils.hocr.HocrParagraph;
import io.tessilab.oss.openutils.hocr.HocrSyntaxException;
import io.tessilab.oss.openutils.hocr.ReadingDirection;
import static org.junit.Assert.assertEquals;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;

public class TestHocrParagraph {

    private Element elm;
    private HocrParagraph par;

    @Before
    public void buildUp() throws HocrSyntaxException {
        Tag t = Tag.valueOf("p");
        elm = new Element(t, "");
        elm.attr("id", "id_test");
        elm.attr("title", "bbox 50 1846 237 1883"); // bounding box
    }

    @Test
    public void testDirectionLeft2Right() throws HocrSyntaxException {
        elm.attr("dir", "ltr");
        par = new HocrParagraph(null, elm);
        assertEquals(ReadingDirection.LEFT_TO_RIGHT, par.getReadingDirection());
    }

    @Test
    public void testDirectionRight2Left() throws HocrSyntaxException {
        elm.attr("dir", "rtl");
        par = new HocrParagraph(null, elm);
        assertEquals(ReadingDirection.RIGHT_TO_LEFT, par.getReadingDirection());
    }

    @Test
    public void testDirectionUnknown() throws HocrSyntaxException {
        elm.attr("dir", "toto");
        par = new HocrParagraph(null, elm);
        assertEquals(ReadingDirection.UNDEFINED_DIRECTION, par.getReadingDirection());
    }
}

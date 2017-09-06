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

import io.tessilab.oss.openutils.hocr.ContentType;
import io.tessilab.oss.openutils.hocr.HocrMeta;
import io.tessilab.oss.openutils.hocr.NTContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;

import org.junit.Before;
import org.junit.Test;

public class TestHocrMeta {
    private HocrMeta meta;

    @Before
    public void buildUp() {
        meta = new HocrMeta();
    }

    @Test
    public void testInitEmpty() {
        assertTrue(meta.isEmpty());
        assertEquals(0, meta.getNbOfNTContents());
        for (ContentType t : ContentType.values()) {
            assertEquals(0, meta.getContentsByType(t).size());
        }
    }

    @Test
    public void testAddNTContent() {
        NTContent nt = new NTContent(ContentType.FRAME, new Rectangle(10, 30, 40, 40));
        meta.addNTContent(nt);
        assertFalse(meta.isEmpty());
        assertEquals(1, meta.getNbOfNTContents());
        assertEquals(1, meta.getContentsByType(ContentType.FRAME).size());

        NTContent nt2 = new NTContent(ContentType.FRAME, new Rectangle(10, 30, 40, 40));
        meta.addNTContent(nt2);
        assertEquals(2, meta.getNbOfNTContents());
        assertEquals(2, meta.getContentsByType(ContentType.FRAME).size());

        NTContent nt3 = new NTContent(ContentType.BARCODE, new Rectangle(5, 5, 40, 40));
        meta.addNTContent(nt3);
        assertEquals(3, meta.getNbOfNTContents());
        assertEquals(2, meta.getContentsByType(ContentType.FRAME).size());
        assertEquals(1, meta.getContentsByType(ContentType.BARCODE).size());
    }

}

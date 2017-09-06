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
package io.tessilab.oss.openutils;

import io.tessilab.oss.openutils.MyDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestMyDate {

    @Test
    public void testToString() {
        MyDate date = new MyDate(20, 2, 2005);
        assertEquals("Bad date.toString()", "2005-02-20", date.toString());
        date = new MyDate(1, 4, 45);
        assertEquals("Bad date.toString()", "0045-04-01", date.toString());
        date = new MyDate(1, 1, 00);
        assertEquals("Bad date.toString()", "0000-01-01", date.toString());
    }

    @Test
    public void testIsCorrect() {
        MyDate date = new MyDate(20, 2, 2005);
        assertTrue(date.toString() + " must be valid", date.isCorrect());
        date = new MyDate(1, 4, 45);
        assertTrue(date.toString() + " must be valid", date.isCorrect());
        date = new MyDate(1, 1, 00);
        assertTrue(date.toString() + " must be valid", date.isCorrect());
        date = new MyDate(10, 2, 2000); // bissextile February
        assertTrue(date.toString() + " must be valid", date.isCorrect());
        date = new MyDate(31, 12, 2099);
        assertTrue(date.toString() + " must be valid", date.isCorrect());

        date = new MyDate(0, 2, 2000); // bissextile February
        assertFalse(date.toString() + " must be not valid", date.isCorrect());
        date = new MyDate(1, 0, 2000);
        assertFalse(date.toString() + " must be not valid", date.isCorrect());
        date = new MyDate(3, 13, 2000);
        assertFalse(date.toString() + " must be not valid", date.isCorrect());
        date = new MyDate(01, 1, 2100);
        assertFalse(date.toString() + " must be not valid", date.isCorrect());
    }

    @Test
    public void testIsMoreAncient() {
        MyDate date1 = new MyDate(10, 04, 2004);
        MyDate date2 = new MyDate(20, 04, 2005);
        MyDate date3 = new MyDate(22, 03, 2005);
        MyDate date4 = new MyDate(19, 04, 2005);

        moreAncientOneTest(date1, date2, true);
        moreAncientOneTest(date2, date1, false);
        moreAncientOneTest(date1, date1, false);

        moreAncientOneTest(date3, date2, true);
        moreAncientOneTest(date4, date2, true);
    }

    // test if d1 is more ancient than d2
    private void moreAncientOneTest(MyDate d1, MyDate d2, boolean resultExpected) {
        if (resultExpected) {
            assertTrue(d1 + " must be more ancient than " + d2, d1.isMoreAncient(d2));
        } else {
            assertFalse(d1 + " must not be more ancient than " + d2, d1.isMoreAncient(d2));
        }
    }

    @Test
    public void testSpecialDate() {
        MyDate date = new MyDate(29, 2, 2000); // bissextile February
        assertTrue(date.toString() + " must be valid", date.isCorrect());
        date = new MyDate(29, 2, 2001);
        assertFalse(date.toString() + " must not be valid", date.isCorrect());
        date = new MyDate(29, 2, 2100);
        assertFalse(date.toString() + " must not be valid", date.isCorrect());

        date = new MyDate(30, 9, 2008);
        assertTrue(date.toString() + " must be valid", date.isCorrect());
        date = new MyDate(31, 9, 2008);
        assertFalse(date.toString() + " must not be valid", date.isCorrect());
    }

    @Test
    public void testEqualityHashCode() {
        MyDate date = new MyDate(13, 4, 2000);
        assertNotEquals(date, null);
        assertNotEquals(date, new Double(1.0));
        assertEquals(date, date);

        MyDate date2 = new MyDate(13, 4, 2000);
        assertEquals(date, date2);
        Integer h1 = date.hashCode();
        Integer h2 = date2.hashCode();
        assertEquals(h1.equals(h2), true);
    }

}

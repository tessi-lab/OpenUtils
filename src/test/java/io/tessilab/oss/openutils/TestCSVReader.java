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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Andres BEL ALONSO
 */
public class TestCSVReader {

    private CSVReader reader;

    public void setUp() {
        String csvPath = this.getClass().getClassLoader().getResource("csvtest/CSVTest1").getPath();
        if ("".equals(csvPath)) {
            fail("File not found");
        }
        reader = new CSVReader(",", csvPath, true);
    }

    public void setUpNoHeader() {
        String csvPath = this.getClass().getClassLoader().getResource("csvtest/CSVTestNoHeader").getPath();
        if ("".equals(csvPath)) {
            fail("File not found");
        }
        reader = new CSVReader(",", csvPath, false);
    }

    public void setUpMalformed() {
        String csvPath = this.getClass().getClassLoader().getResource("csvtest/malformedCSV").getPath();
        if ("".equals(csvPath)) {
            fail("File not found");
        }
        reader = new CSVReader(",", csvPath, true);
    }
    
    public void setUpWithEscapeChar() {
        String csvPath = this.getClass().getClassLoader().getResource("csvtest/CSVWithEscape").getPath();
        if ("".equals(csvPath)) {
            fail("File not found");
        }
        reader = new CSVReader(",", csvPath, true, "UTF-8", "\"");
    }
    
    public void setUpMalformedWithEscape() {
        String csvPath = this.getClass().getClassLoader().getResource("csvtest/MalformedWithEscape").getPath();
        if ("".equals(csvPath)) {
            fail("File not found");
        }
        reader = new CSVReader(",", csvPath, true, "UTF-8", "\"");        
    }

    @Test
    public void testReadNextLine() {
        setUp();
        assertEquals(true, reader.readNextLine());
        assertEquals("jonson,smith,limpiador,upyd", reader.getCurLine());
        assertEquals(true, reader.readNextLine());
        assertEquals("armanda,miller,pornostar,podemos", reader.getCurLine());
        assertEquals(true, reader.readNextLine());
        assertEquals("vladimir,lenin,revolucionario,pce", reader.getCurLine());
        assertEquals(true, reader.readNextLine());
        assertEquals("mariano,rajoy,presidente,pp", reader.getCurLine());
        assertEquals(false, reader.readNextLine());
        assertEquals(null, reader.getCurLine());
    }

    @Test
    public void testReadNextLineNoHeader() {
        setUpNoHeader();
        assertEquals(true, reader.readNextLine());
        assertEquals("jonson,smith,limpiador,upyd", reader.getCurLine());
        assertEquals(true, reader.readNextLine());
        assertEquals("armanda,miller,pornostar,podemos", reader.getCurLine());
        assertEquals(true, reader.readNextLine());
        assertEquals("vladimir,lenin,revolucionario,pce", reader.getCurLine());
        assertEquals(true, reader.readNextLine());
        assertEquals("mariano,rajoy,presidente,pp", reader.getCurLine());
        assertEquals(false, reader.readNextLine());
        assertEquals(null, reader.getCurLine());
    }

    @Test(expected = CSVReader.CSVReadingException.class)
    public void testmalformedCSV() {
        setUpMalformed();
        reader.readNextLine();
    }

    @Test
    public void testGetColNumber() {
        setUp();
        reader.readNextLine();
        assertEquals("jonson", reader.getColNumber(0));
        assertEquals("smith", reader.getColNumber(1));
        assertEquals("limpiador", reader.getColNumber(2));
        assertEquals("upyd", reader.getColNumber(3));
        reader.readNextLine();
        assertEquals("armanda", reader.getColNumber(0));
        assertEquals("miller", reader.getColNumber(1));
        assertEquals("pornostar", reader.getColNumber(2));
        assertEquals("podemos", reader.getColNumber(3));
    }

    @Test
    public void testGetColNumberNoHeader() {
        setUpNoHeader();
        reader.readNextLine();
        assertEquals("jonson", reader.getColNumber(0));
        assertEquals("smith", reader.getColNumber(1));
        assertEquals("limpiador", reader.getColNumber(2));
        assertEquals("upyd", reader.getColNumber(3));
        reader.readNextLine();
        assertEquals("armanda", reader.getColNumber(0));
        assertEquals("miller", reader.getColNumber(1));
        assertEquals("pornostar", reader.getColNumber(2));
        assertEquals("podemos", reader.getColNumber(3));
    }

    @Test(expected = CSVReader.CSVReadingException.class)
    public void testBadColNumber1() {
        setUp();
        reader.readNextLine();
        reader.getColNumber(99);
    }

    @Test(expected = CSVReader.CSVReadingException.class)
    public void testBadColNumber1NoHeader() {
        setUpNoHeader();
        reader.readNextLine();
        reader.getColNumber(99);
    }

    @Test(expected = CSVReader.CSVReadingException.class)
    public void testBadColNumber2() {
        setUp();
        reader.readNextLine();
        reader.getColNumber(-1);
    }

    @Test(expected = CSVReader.CSVReadingException.class)
    public void testBadColNumber2NoHeader() {
        setUpNoHeader();
        reader.readNextLine();
        reader.getColNumber(-1);
    }

    @Test
    public void testGetColName() {
        setUp();
        reader.readNextLine();
        assertEquals("jonson", reader.getColName("nombre"));
        assertEquals("smith", reader.getColName("apellido"));
        assertEquals("limpiador", reader.getColName("profesion"));
        assertEquals("upyd", reader.getColName("partido"));
        reader.readNextLine();
        assertEquals("armanda", reader.getColName("nombre"));
        assertEquals("miller", reader.getColName("apellido"));
        assertEquals("pornostar", reader.getColName("profesion"));
        assertEquals("podemos", reader.getColName("partido"));
    }

    @Test(expected = CSVReader.CSVReadingException.class)
    public void testGetColNameNoHeader() {
        setUpNoHeader();
        reader.readNextLine();
        assertEquals("jonson", reader.getColName("nombre"));
    }

    @Test(expected = CSVReader.CSVReadingException.class)
    public void testBadColName() {
        setUp();
        reader.readNextLine();
        reader.getColName("fjkdsl g");
    }
    
    @Test(expected = CSVReader.CSVReadingException.class)
    public void testMalformedWithEscape() {
        setUpMalformedWithEscape();
        reader.readNextLine();
    }
    
    @Test
    public void testCSVWithEscapeChar() {
        setUpWithEscapeChar();
        reader.readNextLine();
        assertEquals("13",reader.getColName("id"));
        assertEquals("supercat",reader.getColNumber(1));
        assertEquals("hi, i'm a super, cleverCat",reader.getColName("talk_phrase"));
        reader.readNextLine();
        assertEquals("31",reader.getColNumber(0));
        assertEquals("dog, the red",reader.getColNumber(1));
        assertEquals("guauguau", reader.getColNumber(2));
        reader.readNextLine();
        assertEquals("45",reader.getColNumber(0));
        assertEquals("Melanie",reader.getColNumber(1));
        assertEquals("hi girls",reader.getColNumber(2));
    }
}

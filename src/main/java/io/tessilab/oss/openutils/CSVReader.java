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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;

/**
 * A class to help to read a csv line by line. It loads a line, and provides an
 * API to get the readed data
 * 
 * @author Andres BEL ALONSO
 */
public class CSVReader {

    public static class CSVReadingException extends RuntimeException {
        public CSVReadingException(String msg) {
            super(msg);
        }

        public CSVReadingException(Throwable ex) {
            super(ex);
        }
    }

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CSVReader.class);

    private final BufferedReader reader;
    private final String[] colNames;
    private final String separator;
    private final boolean hasHeader;

    private String curLine;
    private String[] curTab;
    private int lineCounter = 0;

    /**
     * The constructor sets the current line on the first line (who is expected
     * to be the columns name line)
     * 
     * @param separator
     * @param filePath
     * @param encoding
     *            : by default UTF8
     * @throws io.tessilab.utils.CSVReader.CSVReadingException
     */
    public CSVReader(String separator, String filePath, boolean hasHeader, String encoding) {
        this.separator = separator;
        this.hasHeader = hasHeader;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), encoding));
            if (hasHeader) {
                curLine = reader.readLine();
                colNames = curLine.split(separator);
                if (colNames.length == 1) {
                    LOGGER.warn("Reading a csv file with only a single column... The separator maybe not the correct");
                }
            } else {
                colNames = null;
            }
        } catch (IOException ex) {
            LOGGER.warn(ex);
            throw new CSVReadingException(ex);
        }

    }

    /**
     * The constructor sets the current line on the first line (who is expected
     * to be the columns name line)
     * 
     * @param separator
     * @param filePath
     * @throws io.tessilab.utils.CSVReader.CSVReadingException
     */
    public CSVReader(String separator, String filePath, boolean hasHeader) {
        this(separator, filePath, hasHeader, "UTF8");
    }

    /**
     * Reads the next line
     * 
     * @return true if the readed line is not null
     * @throws io.tessilab.utils.CSVReader.CSVReadingException
     *             : If there is an IOException during the execution of the read
     */
    public boolean readNextLine() {
        try {
            lineCounter++;
            curLine = reader.readLine();
            if (curLine == null) {
                return false;
            }
            curTab = curLine.split(this.separator);
            if (hasHeader && curTab.length != colNames.length) {
                throw new CSVReadingException("The line " + String.valueOf(lineCounter)
                        + " does not have the same size than the titles line");
            }
            return true;
        } catch (IOException ex) {
            LOGGER.warn(ex);
            throw new CSVReadingException(ex);
        }
    }

    /**
     * Returns the content in the column number
     * 
     * @param colNumber
     *            : the first column index is 0
     * @return
     */
    public String getColNumber(int colNumber) {
        if (colNumber < 0 || colNumber > curTab.length) {
            throw new CSVReadingException("The column number is not in the correct range");
        }
        return curTab[colNumber];
    }

    /**
     * 
     * @param columnName
     *            : The name of the column
     * @return The content of the column or an exception if this name does not
     *         exist
     */
    public String getColName(String columnName) {
        if (!hasHeader)
            throw new CSVReadingException("The CSV has no header, can't get column by name : " + columnName);
        
        for (int i = 0; i < colNames.length; i++) {
            if (colNames[i].equals(columnName)) {
                return curTab[i];
            }
        }
        throw new CSVReadingException("The column name " + columnName + " does not exist");
    }

    /**
     * 
     * @return the current line as it is
     */
    public String getCurLine() {
        return curLine;
    }

}

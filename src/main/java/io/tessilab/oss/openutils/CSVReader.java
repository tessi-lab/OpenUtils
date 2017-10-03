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
import java.util.ArrayList;
import java.util.List;
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
    private final String escapeChar;

    private String curLine;
    private String[] curTab;
    private int lineCounter = 0;

    /**
     * The constructor sets the current line on the first line (who is expected
     * to be the columns name line)
     * 
     * Throws io.tessilab.utils.CSVReader.CSVReadingException If there is a problem when readin the file. 
     * @param separator The character used as separator in the csv file
     * @param filePath The absolute path to the csv file that is going to be read. 
     * @param hasHeader True if the file has a first header line. In other words, a line that names the different
     * columns of the csv file. 
     * @param encoding The type of encoding of the csv file. By default UTF8
     * @param escapeChar A character that is used to ignore the separator. If there is a separator between two escape 
     * chars, it will be ignore
     * 
     * 
     */
    public CSVReader(String separator, String filePath, boolean hasHeader, String encoding, String escapeChar) {
        this.separator = separator;
        this.hasHeader = hasHeader;
        this.escapeChar = escapeChar;
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
     * throws io.tessilab.oss.openutil.CSVReader.CSVReadingException  
     * If there is an IOException during the execution of the read
     * 
     * @param separator The character used as separator in the csv file
     * @param filePath The absolute path to the csv file that is going to be read. 
     * @param hasHeader True if the file has a first header line. In other words, a line that names the different
     * columns of the csv file. 
     */
    public CSVReader(String separator, String filePath, boolean hasHeader) {
        this(separator, filePath, hasHeader, "UTF8",null);
    }

    /**
     * Reads the next line
     * throws io.tessilab.oss.openutil.CSVReader.CSVReadingException
     *              If there is an IOException during the execution of the read
     * 
     * @return true if the readed line is not null
     */
    public boolean readNextLine() {
        try {
            lineCounter++;
            curLine = reader.readLine();
            if (curLine == null) {
                return false;
            }
            if(escapeChar == null || !curLine.contains(escapeChar)) {
                curTab = curLine.split(this.separator);    
            } else {
                curTab = readTheLineWithEscapeChars(curLine);
            }
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
     * @param colNumber the first column index is 0
     * @return The field content in colNumber
     */
    public String getColNumber(int colNumber) {
        if (colNumber < 0 || colNumber > curTab.length) {
            throw new CSVReadingException("The column number is not in the correct range");
        }
        return curTab[colNumber];
    }

    /**
     * 
     * @param columnName The name of the column
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
    
    /**
     * Reads a line. If CSVReadingException is throw, the next line is readed. 
     * @param reader
     * @return The output of the last reader.readnextLine() that did not throw an exception
     */
    public static boolean secureReadNextLine(CSVReader reader) {
        try {
            return reader.readNextLine();
        } catch(CSVReadingException ex) {
            return CSVReader.secureReadNextLine(reader);
        }
    }
    
    private String[] readTheLineWithEscapeChars(String newLine) {
        String curLineMet = newLine;
        List<String> separatedBlocks = new ArrayList<>();
        int nextEscape = -1 ;
        int nextSeparator = -1;
        while(!"".equals(curLineMet)) {
           nextEscape = curLineMet.indexOf(this.escapeChar);
           nextSeparator = curLineMet.indexOf(this.separator);
           if(nextEscape == -1) {
               // No more escape characters on the csv
               String[] otherElmts = curLineMet.split(separator);
               String[] res = new String[separatedBlocks.size() + otherElmts.length];
               for(int i=0; i<separatedBlocks.size(); i++) {
                   res[i] = separatedBlocks.get(i);
               }
               for(int i=separatedBlocks.size(); i<separatedBlocks.size() + otherElmts.length; i++) {
                   res[i] = otherElmts[i];
               }
               return res;
           } else if(nextEscape > nextSeparator) {
               // the separator commes first               
               separatedBlocks.add(curLineMet.substring(0,nextSeparator));
               curLineMet = curLineMet.substring(nextSeparator + 1);
           } else {
               // there is first an escape character
               int secondEscapeChar = curLineMet.substring(nextEscape + 1).indexOf(escapeChar);
               if(secondEscapeChar == -1) {
                   throw new CSVReadingException("There is no a close character for open escape char in index" + 
                           String.valueOf(nextEscape));
               }
               nextSeparator = curLineMet.indexOf(separator, secondEscapeChar);
               if(nextSeparator == -1) {
                   // this is the last column
                   nextSeparator = curLineMet.length();
               }
               separatedBlocks.add(curLineMet.substring(0,nextSeparator));
               curLineMet = curLineMet.substring(nextSeparator + 1);
           }
        }
        String[] resTab = new String[separatedBlocks.size()];
        return separatedBlocks.toArray(resTab);
    }

}

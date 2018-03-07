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
package io.tessilab.oss.openutils.io;

import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * The output stream who prints to a Log4j logger. 
 * Not thread safe!
 *
 * @author Andres BEL ALONSO
 */
public class LoggerOutputStream extends OutputStream {
    
    /**
     * Default number of bytes in the buffer.
     */
    private static final int DEFAULT_BUFFER_LENGTH = 2048;
    private static final int MAX_BUFFER_LENGHT = 8192;

    /**
     * The logger belonging to this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerOutputStream.class);

    private final Logger outPut;

    private final Class printingClass;

    private final Level logLevel;
    
    /**
     * Internal buffer where data is stored.
     */
    private byte[] buf;

    /**
     * The number of valid bytes in the buffer.
     */
    private int count;

    /**
     * Remembers the size of the buffer.
     */
    private int curBufLength;


    public LoggerOutputStream(Class printingClass, Level logLevel) {
        this.printingClass = printingClass;
        outPut = LoggerFactory.getLogger(printingClass);
        this.logLevel = logLevel;
        curBufLength = DEFAULT_BUFFER_LENGTH;
        buf = new byte[curBufLength];
        count = 0;
    }

    @Override
    public void write(final int b) throws IOException {
        // don't log nulls
        if (b == 0) {
            return;
        }
        // if there is an end of line, we flush.
        if("\n".equals(new String((new byte[]{(byte) b}))) || "\r".equals(new String((new byte[]{(byte) b})))) {
            flush();
            return;
        }
        // would this be writing past the buffer?
        if (count == curBufLength) {
            // grow the buffer
            final int newBufLength = curBufLength
                    + DEFAULT_BUFFER_LENGTH;
            if(newBufLength > MAX_BUFFER_LENGHT) {
                LOGGER.error("To long string to print cutting it.");
                flush();
            }
            final byte[] newBuf = new byte[newBufLength];
            System.arraycopy(buf, 0, newBuf, 0, curBufLength);
            buf = newBuf;
            curBufLength = newBufLength;
        }
        buf[count] = (byte) b;
        count++;

    }

    /**
     * Flushes this output stream and forces any buffered output bytes to be
     * written out.
     */
    @Override
    public void flush() {
        if (count == 0) {
            return;
        }
        final byte[] bytes = new byte[count];
        System.arraycopy(buf, 0, bytes, 0, count);
        String str = new String(bytes);
        switch (logLevel) {
            case TRACE:
                outPut.trace(str);
                break;
            case DEBUG:
                outPut.debug(str);
                break;
            case INFO:
                outPut.info(str);
                break;
            case WARN:
                outPut.warn(str);
                break;
            case ERROR:
                outPut.error(str);
                break;
        }
        count = 0;
    }

    public Class getPrintingClass() {
        return printingClass;
    }

}

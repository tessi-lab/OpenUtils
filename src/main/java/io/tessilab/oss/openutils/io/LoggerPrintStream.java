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

import java.io.PrintStream;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * A printS
 * @author Andres BEL ALONSO
 */
public class LoggerPrintStream extends PrintStream{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerPrintStream.class);

    
    public LoggerPrintStream(Class printingClass, Level logLevel) {
        super(new LoggerOutputStream(printingClass, logLevel),false);
    }
    
    
    public static void main (String[] args) {
        LoggerPrintStream stream  = new LoggerPrintStream(LoggerPrintStream.class, Level.ERROR);
        stream.println("Test");
        stream.println("Hola que ase");
        stream.println("Esta guay este log");
        stream.println("Verdad premohhhhhhh");
        String toLongString ="";
        for(int i=0; i<8192; i++) {
            toLongString += "a";
        }
        stream.println(toLongString);
        stream.println("Mais tout c'est bien passé, n'estce pas ?");
        stream.close();
    }
    
}

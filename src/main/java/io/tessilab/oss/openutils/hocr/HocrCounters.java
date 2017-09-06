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

public class HocrCounters {
    private int pageCounter;
    private int careaCounter;
    private int parCounter;
    private int lineCounter;
    private int wordCounter;

    public HocrCounters() {
        resetCounters();
    }

    public void resetCounters() {
        pageCounter = 0;
        careaCounter = 0;
        parCounter = 0;
        lineCounter = 0;
        wordCounter = 0;
    }

    private int nextPageCounter() {
        return pageCounter++;
    }

    private int nextContentAreaCounter() {
        return careaCounter++;
    }

    private int nextParagraphCounter() {
        return parCounter++;
    }

    private int nextLineCounter() {
        return lineCounter++;
    }

    private int nextWordCounter() {
        return wordCounter++;
    }

    public String nextPageId() {
        return "page_" + nextPageCounter();
    }

    public String nextContentAreaId() {
        return "carea_" + nextContentAreaCounter();
    }

    public String nextParagraphId() {
        return "par_" + nextParagraphCounter();
    }

    public String nextLineId() {
        return "line_" + nextLineCounter();
    }

    public String nextWordId() {
        return "word_" + nextWordCounter();
    }

}

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

import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the HocrPages of the document, and all their metadata.
 * 
 * @author galaad
 *
 */
public class HocrDocument {

    private static final Logger LOGGER = LoggerFactory.getLogger(HocrDocument.class);

    // keys : page numbers
    private Map<Integer, HocrPage> pages;

    private HocrCounters counters;
    private boolean editUnlocked = false;

    // if true => in hocr space
    // if false => in image space
    private boolean inHocrSpace = false;

    public HocrDocument() {
        pages = new HashMap<>();
        counters = new HocrCounters();
    }

    public HocrDocument(String htmlContent) {
        this();
        parseHocrString(htmlContent);
    }

    private void parseHocrString(String htmlContent) {
        // Sanitize input
        if (htmlContent == null || "".equals(htmlContent.trim())) {
            throw new IllegalArgumentException("The hocr document must be non empty.");
        }

        // parsing
        Document doc = Jsoup.parse(htmlContent, "UTF-8");

        // We get the pages
        // We trust the html for the ids.
        Iterator<Element> pageIt = doc.body().getElementsByClass(HTMLConstants.PAGE_CLASS).iterator();
        int maxPageNumber = -1;
        while (pageIt.hasNext()) {
            Element pageElem = pageIt.next();
            try {
                HocrPage page = new HocrPage(pageElem, this);
                putPageInMap(page);

                if (page.getPageNumber() > maxPageNumber) {
                    maxPageNumber = page.getPageNumber();
                }
            } catch (HocrSyntaxException hse) {
                LOGGER.debug("Exception while parsing the hocr page.", hse);
                throw new IllegalArgumentException("Exception while parsing the hocr page.");
            }
        }

        if (maxPageNumber != getNbOfPages() - 1)
            throw new IllegalArgumentException("The pages have bad numbers");

        if (pages.isEmpty())
            throw new IllegalArgumentException("There must be at least one page in the body.");
    }

    private void putPageInMap(HocrPage page) {
        if (page.getPageNumber() < 0) {
            throw new IllegalArgumentException("A page number cannot be negative.");
        }
        if (pages.containsKey(page.getPageNumber())) {
            throw new IllegalArgumentException("This page number already existed in the document.");
        }
        pages.put(page.getPageNumber(), page);
    }

    /**
     * @return The number of pages in this document.
     */
    public int getNbOfPages() {
        return pages.size();
    }

    /**
     * Return a new page for this document.
     * 
     * @param pageNumber
     *            The number of the page in the document.
     * @return
     */
    public HocrPage createNewPage(int pageNumber) {
        HocrPage page = new HocrPage(pageNumber, this);
        return page;
    }

    /**
     * Inserts a page in this document. The page has to have a good page number.
     */
    public void adoptPage(HocrPage page, int pageNumber) {
        makeEditable();

        // put the page in the map
        page.setPageNumber(pageNumber);
        page.setParentDocument(this);
        putPageInMap(page);

        // change the ids of the page
        page.rewriteAllIds();
    }

    /**
     * Visibility: package
     */
    void makeEditable() {
        if (!editUnlocked) {
            counters.resetCounters();
            for (HocrPage pp : pages.values()) {
                pp.rewriteAllIds();
            }
            editUnlocked = true;
        }
    }

    public HocrCounters getCounters() {
        return this.counters;
    }

    /**
     * @param i
     *            The page number.
     * @return
     */
    public HocrPage getPage(int i) {
        if (i >= pages.size())
            return null;
        return pages.get(i);
    }

    public Stream<HocrPage> getPageStream() {
        return pages.values().stream().sorted((p1, p2) -> p1.getPageNumber() - p2.getPageNumber());
    }

    public String toHocrString() {
        // create a document (element) ...
        Element doc = Jsoup.parse("<html/>", "UTF-8");

        // we set the header
        Element head = doc.getElementsByTag(HTMLConstants.TAG_HEAD).get(0);
        head.appendChild(new Element(Tag.valueOf(HTMLConstants.TAG_TITLE), ""));

        Element metaHtml = new Element(Tag.valueOf(HTMLConstants.TAG_META), "");
        metaHtml.attr("http-equiv", "Content-Type");
        metaHtml.attr("content", "text/html;charset=utf-8");
        head.appendChild(metaHtml);

        Element metaTesseract = new Element(Tag.valueOf(HTMLConstants.TAG_META), "");
        metaTesseract.attr("name", "ocr-system");
        metaTesseract.attr("content", "tesseract");
        head.appendChild(metaTesseract);

        // we get the body
        Element body = doc.getElementsByTag(HTMLConstants.TAG_BODY).get(0);

        // add the pages
        getPageStream().forEach(page -> body.appendChild(page.toHtmlElement()));

        String htmlType = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";

        return htmlType + "\n" + doc.toString();
    }

    public void removeVoidWords() {
        getPageStream().forEach(HocrPage::removeVoidWords);
    }

    /**
     * Goes to the hocr space: for each page, applies the transforms to the
     * words, and sections.</br> If we already are in the hocr space, does
     * nothing.
     */
    public void toHocrSpace() {
        if (inHocrSpace)
            return;
        for (int k = 0; k < getNbOfPages(); k++) {
            HocrPage page = getPage(k);
            AffineTransform aTransform = page.getAffineTransform();

            // Transform all the words
            page.getAllWords().forEach(w -> w.applyTransform(aTransform));
            // Update the bounding boxes of all the intermediate sections
            page.updateAllBBoxes();// NOT THE PAGE
            // Transform the page
            page.applyTransform(aTransform);
        }
        inHocrSpace = true;
    }

    /**
     * Goes to the image space.</br> If we already are in the image space, does
     * nothing.
     */
    public void toImageSpace() {
        if (!inHocrSpace)
            return;
        for (int k = 0; k < getNbOfPages(); k++) {
            HocrPage page = getPage(k);
            AffineTransform aTransform = (AffineTransform) page.getAffineTransform().clone();
            try {
                aTransform.invert();

                // Transform all the words
                page.getAllWords().forEach(w -> w.applyTransform(aTransform));
                // Update the bounding boxes of all the intermediate sections
                page.updateAllBBoxes();// NOT THE PAGE
                // Transform the page
                page.applyTransform(aTransform);
            } catch (Exception e) {
                LOGGER.debug("page transform is not invertible");
            }
        }
        inHocrSpace = false;
    }

}

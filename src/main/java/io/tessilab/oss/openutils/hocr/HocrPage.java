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

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.HashSet;
import java.util.Set;

/**
 * Class: Page in Hocr documents
 * 
 * @author galaad
 *
 */
public class HocrPage extends HocrNode {

    private int pageNumber;

    private HocrMeta meta;
    private int charWidth = 30;
    private int oldCharWidth;
    private AffineTransform affineTransform = new AffineTransform();
    private AffineTransform affineTransformInvert = new AffineTransform();
    private double dpi;

    private HocrDocument parentDocument;

    private HocrContentArea freeCarea;
    private HocrParagraph freePar;
    private HocrLine freeLine;
    private boolean freeInit = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(HocrPage.class);

    private static final long serialVersionUID = -1981770559350916034L;

    private HocrPage(HocrSection parent, Element element) throws HocrSyntaxException {
        super(parent, element);
    }

    private HocrPage(HocrSection parent) {
        super(parent);
    }

    /**
     * Constructor: parsing html. </br>Visibility: package
     */
    HocrPage(Element element, HocrDocument doc) throws HocrSyntaxException {
        this(null, element);
        if (doc == null) {
            throw new IllegalArgumentException("A page must be in a document");
        }
        this.parentDocument = doc;
        readPageNumberFromTitle(element);
        if(meta == null)
            meta = new HocrMeta();

        // read charWidth
        String charWidthStr = element.attr(HTMLConstants.CHAR_WIDTH);
        if (charWidthStr != "")
            this.charWidth = Integer.parseInt(charWidthStr);
        this.oldCharWidth = 0; // by default

        // read affine transform matrix
        String affineStr = element.attr(HTMLConstants.AFFINE_TRANSFORM);
        if (affineStr != "") {
            String[] atSplits = affineStr.trim().split("\\s+");
            double m00 = Double.parseDouble(atSplits[0]);
            double m10 = Double.parseDouble(atSplits[1]);
            double m01 = Double.parseDouble(atSplits[2]);
            double m11 = Double.parseDouble(atSplits[3]);
            double m02 = Double.parseDouble(atSplits[4]);
            double m12 = Double.parseDouble(atSplits[5]);
            setAffineTransform(new AffineTransform(m00, m10, m01, m11, m02, m12));
        }

        // read the dpi
        String dpiStr = element.attr(HTMLConstants.DPI);
        if (dpiStr != "")
            this.dpi = Double.parseDouble(dpiStr);
    }

    /**
     * Constructor: new empty page.
     */
    public HocrPage(int pageNumber, HocrDocument doc) {
        super(null);
        if (doc == null) {
            throw new IllegalArgumentException("A page must be in a document");
        }
        doc.adoptPage(this, pageNumber);
        // parameters by default
        this.meta = new HocrMeta();
        this.bbox = new Rectangle(0, 0, -1, -1);
        this.affineTransform = new AffineTransform();
        this.dpi = 72;
    }

    @Override
    protected HocrSection getChildFromElement(Element element) throws HocrSyntaxException {
        if (HTMLConstants.META_CLASS.equals(element.attr(HTMLConstants.CLASS))) {
            // for the META
            if (meta != null) {
                throw new HocrSyntaxException("There cannot be 2 meta in a page");
            }
            this.meta = new HocrMeta(element);
            return null; // HACK 101
        } else {
            try {
                return new HocrContentArea(this, element);
            } catch (HocrSyntaxException hse) {
                LOGGER.debug("Exception while creating an HocrContentArea from an html element.", hse);
                return null;
            }
        }
    }

    @Override
    protected String getElementTag() {
        return HTMLConstants.TAG_DIV;
    }

    @Override
    protected String getElementClassAttribute() {
        return HTMLConstants.PAGE_CLASS;
    }

    @Override
    protected Element toHtmlElement() {
        Element elem = super.toHtmlElement();
        String title = HTMLConstants.PAGE_TITLE_START + getBBoxHocrString() + HTMLConstants.PAGE_TITLE_END + this.pageNumber;
        elem.attr(HTMLConstants.TITLE, title);
        if (meta != null && !meta.isEmpty())
            elem.appendChild(meta.toHtmlElement()); // for the meta
        // add charwidth
        elem.attr(HTMLConstants.CHAR_WIDTH, Integer.toString(charWidth));

        // add affine transform matrix
        double m00 = affineTransform.getScaleX();
        double m10 = affineTransform.getShearY();
        double m01 = affineTransform.getShearX();
        double m11 = affineTransform.getScaleY();
        double m02 = affineTransform.getTranslateX();
        double m12 = affineTransform.getTranslateY();
        String tranformStr = Double.toString(m00) + " " + Double.toString(m10) + " " + Double.toString(m01) + " " + Double.toString(m11) + " " + Double.toString(m02) + " " + Double.toString(m12);
        elem.attr(HTMLConstants.AFFINE_TRANSFORM, tranformStr);

        // add dpi
        elem.attr(HTMLConstants.DPI, Double.toString(this.dpi));

        return elem;
    }

    @Override
    public HocrPage getPage(){
        return this;
    }
    
    private void readPageNumberFromTitle(Element element) {
        String title = element.attr(HTMLConstants.TITLE);
        String nb = title.split("ppageno")[1].trim();
        this.pageNumber = Integer.parseInt(nb);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Visibility: package
     */
    void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * @param w
     * @return The id of the added word
     */
    public String addWord(HocrXWord w) {
        if (!freeInit) {
            parentDocument.makeEditable();

            freeCarea = new HocrContentArea(this);
            freeCarea.setId(parentDocument.getCounters().nextContentAreaId());
            this.childs.add(freeCarea);

            freePar = new HocrParagraph(freeCarea);
            freePar.setId(parentDocument.getCounters().nextParagraphId());
            freeCarea.childs.add(freePar);

            freeLine = new HocrLine(freePar);
            freeLine.setId(parentDocument.getCounters().nextLineId());
            freePar.childs.add(freeLine);

            freeInit = true;
        }

        // add the word
        String wordId = parentDocument.getCounters().nextWordId();
        w.setId(wordId);
        w.parent = freeLine;
        freeLine.getChilds().add(w);

        // maybe adjust the bboxes
        freeLine.updateBbox();

        return wordId;
    }

    public void setBBox(Rectangle r) {
        this.bbox = r;
    }

    public HocrDocument getParentDocument() {
        return this.parentDocument;
    }

    // We do not change the bounding box of the page
    @Override
    protected void updateBbox() {
        // nothing
    }

    /**
     * Visibility: package
     */
    void setParentDocument(HocrDocument hd) {
        this.parentDocument = hd;
    }

    /**
     * Rewrites the id of this page and all the ids of its sections. Make sure
     * to have unique ids with regard to the related document.
     */
    public void rewriteAllIds() {
        this.setId(parentDocument.getCounters().nextPageId());
        for (HocrSection hs1 : this.getChilds()) {
            HocrContentArea hca = (HocrContentArea) hs1;
            hca.setId(parentDocument.getCounters().nextContentAreaId());
            for (HocrSection hs2 : hca.getChilds()) {
                HocrParagraph hp = (HocrParagraph) hs2;
                hp.setId(parentDocument.getCounters().nextParagraphId());
                for (HocrSection hs3 : hp.getChilds()) {
                    HocrLine hl = (HocrLine) hs3;
                    hl.setId(parentDocument.getCounters().nextLineId());
                    for (HocrSection hs4 : hl.getChilds()) {
                        HocrXWord hw = (HocrXWord) hs4;
                        hw.setId(parentDocument.getCounters().nextWordId());
                    }
                }
            }
        }
    }

    public void updateAllBBoxes() {
        Set<HocrSection> directParents = new HashSet<>();
        getAllWords().forEach(w -> directParents.add(w.parent));
        directParents.forEach(p -> p.updateBbox());
    }

    public HocrMeta getMeta() {
        return this.meta;
    }

    /**
     * @param page
     * @return The number of characters in this page.
     */
    public int getCharCount() {
        int count = 0;
        for (HocrXWord hxw : getAllWords()) {
            count += hxw.getValue().length();
        }
        return count;
    }

    public int getCharWidth() {
        return this.charWidth;
    }

    public void setCharWidth(int w) {
        this.charWidth = w;
    }

    public int getOldCharWidth() {
        return this.oldCharWidth;
    }

    public void setOldCharWidth(int w) {
        this.oldCharWidth = w;
    }

    public AffineTransform getAffineTransformInvert() {
        return this.affineTransformInvert;
    }

    public AffineTransform getAffineTransform() {
        return this.affineTransform;
    }

    public void setAffineTransform(AffineTransform transfo) {
        this.affineTransform = transfo;
        this.affineTransformInvert = new AffineTransform(transfo);
        try {
            affineTransformInvert.invert();
        } catch (NoninvertibleTransformException e) {
            LOGGER.error("Non invertible transformation", e);
        }
    }

    public double getDPI() {
        return this.dpi;
    }

    public void setDPI(double dpi) {
        this.dpi = dpi;
    }

}

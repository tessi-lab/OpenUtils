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

import io.tessilab.oss.openutils.bbox.BBoxable;

import java.awt.geom.Rectangle2D;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 * Non-Textual Content: tables, barcodes, ...
 * 
 * @author galaad
 */
public class NTContent implements BBoxable {

    private ContentType type;
    private Rectangle2D rectangle;

    public NTContent(ContentType type, Rectangle2D bbox) {
        this.type = type;
        this.rectangle = bbox;
    }

    public ContentType getType() {
        return type;
    }

    public Rectangle2D getBBox() {
        return rectangle;
    }

    @Override
    public String toString() {
        return "[" + getType().toString() + "; " + getBBox() + "]";
    }

    public Element toHtmlElement() {
        Element elem = new Element(Tag.valueOf(HTMLConstants.TAG_DIV), "");
        elem.attr(HTMLConstants.CLASS, ContentType.toHocrString(type));
        elem.attr(HTMLConstants.TITLE, getBBoxHocrString());
        return elem;
    }

}

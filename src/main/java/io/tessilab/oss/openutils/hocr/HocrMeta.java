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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class HocrMeta {

    private Map<ContentType, List<NTContent>> ntContents;

    /**
     * Simple constructor
     */
    public HocrMeta() {
        ntContents = new EnumMap<>(ContentType.class);
    }

    /**
     * Constructor used when parsing hocr.
     * 
     * @param elem
     */
    public HocrMeta(Element elem) {
        this();
        // We get the non-textual zones
        ContentType[] types = ContentType.values();
        for (ContentType t : types) {
            Iterator<Element> it = elem.getElementsByClass(ContentType.toHocrString(t)).iterator();
            List<NTContent> nts = new ArrayList<>();
            while (it.hasNext()) {
                Element e = it.next();
                Rectangle bbox = HocrUtils.readBoundingBox(e.attr(HTMLConstants.TITLE));
                nts.add(new NTContent(t, bbox));
            }
            if (!nts.isEmpty())
                ntContents.put(t, nts);
        }
    }

    public void addNTContent(NTContent nt) {
        List<NTContent> l = ntContents.get(nt.getType());
        if (l == null) {
            l = new ArrayList<>();
            ntContents.put(nt.getType(), l);
        }
        l.add(nt);
    }

    /**
     * @param type
     * @return A list with every NTContent for this type.
     */
    public List<NTContent> getContentsByType(ContentType type) {
        List<NTContent> l = ntContents.get(type);
        return l == null ? Collections.emptyList() : l;
    }

    public Stream<NTContent> getNTContentStream() {
        return ntContents.values().stream().flatMap(l -> l.stream());
    }

    public int getNbOfNTContents() {
        return (int) getNTContentStream().count();
    }

    /**
     * @return TRUE if there is no NTcontent of any type on this page.
     */
    public boolean isEmpty() {
        for (Map.Entry<ContentType, List<NTContent>> entry : ntContents.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Metas for this page:\n");
        for (Map.Entry<ContentType, List<NTContent>> entry : ntContents.entrySet()) {
            builder.append(entry.getKey() + ":\n");
            for (NTContent nt : entry.getValue()) {
                builder.append("   " + nt.toString() + "\n");
            }
        }
        return builder.toString();
    }

    public Element toHtmlElement() {
        Element elem = new Element(Tag.valueOf(HTMLConstants.TAG_DIV), "");
        elem.attr(HTMLConstants.CLASS, HTMLConstants.META_CLASS);
        for (Map.Entry<ContentType, List<NTContent>> entry : ntContents.entrySet()) {
            entry.getValue().stream().forEach(nt -> elem.appendChild(nt.toHtmlElement()));
        }
        return elem;
    }
}

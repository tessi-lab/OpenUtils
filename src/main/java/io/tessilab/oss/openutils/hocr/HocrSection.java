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

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;

/**
 * @author galaad
 *
 */
public abstract class HocrSection implements Serializable, BBoxable {

    private static final long serialVersionUID = 1313755691985000424L;
    private static final Logger LOGGER = LogManager.getLogger(HocrSection.class);

    private String id;
    protected Rectangle bbox;

    protected HocrSection parent = null;

    public HocrSection() {
        parent = null;
    }

    public HocrSection(HocrSection parent) {
        this.parent = parent;
    }

    public HocrSection(HocrSection parent, Element element) throws HocrSyntaxException {
        this(parent);
        if (!getElementTag().equals(element.tag().toString()))
            throw new HocrSyntaxException("Bad element tag");
        id = element.attr(HTMLConstants.ID);

        // We read the bounding box from the HTML element title
        try {
            this.bbox = HocrUtils.readBoundingBox(element.attr(HTMLConstants.TITLE));
        } catch (Exception e) {
            this.bbox = new Rectangle(0, 0, -1, -1);
            LOGGER.debug("Error while reading the bounding box in the element's title", e);
        }
    }

    protected abstract String getElementTag();

    protected abstract String getElementClassAttribute();

    protected abstract Element toHtmlElement();

    public HocrPage getPage() {
        if (parent != null)
            return parent.getPage();
        else
            return null;
    }

    // -------

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HocrSection other = (HocrSection) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    // -------

    public String getId() {
        return this.id;
    }

    /**
     * Visibility: package
     */
    void setId(String id) {
        this.id = id;
    }

    public Rectangle getBBox() {
        return this.bbox;
    }

    public abstract List<HocrXWord> getAllWords();

    protected abstract void updateBbox();

    public void destroy() {
        if (parent != null)
            ((HocrNode) parent).destroyChild(this);
    }

    public void removeVoidWords() {
        if (canBeRemoved())
            destroy();
        else
            updateBbox();
    }

    protected abstract boolean canBeRemoved();

}
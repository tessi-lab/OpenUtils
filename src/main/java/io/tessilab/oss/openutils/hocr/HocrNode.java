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
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public abstract class HocrNode extends HocrSection {

    private static final long serialVersionUID = 8818574475198675428L;

    protected ArrayList<HocrSection> childs = new ArrayList<>();

    public HocrNode(HocrSection parent, Element element) throws HocrSyntaxException {
        super(parent, element);
        for (Element child : element.children()) {
            HocrSection hocrChild = getChildFromElement(child);
            if (hocrChild != null) 
                childs.add(hocrChild);
        }
    }

    public HocrNode(HocrSection parent) {
        super(parent);
    }

    public List<HocrSection> getChilds() {
        return childs;
    }

    protected abstract HocrSection getChildFromElement(Element element) throws HocrSyntaxException;

    protected void destroyChild(HocrSection child) {
        childs.remove(child);
        if (childs.isEmpty()) {
            destroy();
        } else {
            updateBbox();
        }
    }

    @Override
    public List<HocrXWord> getAllWords() {
        ArrayList<HocrXWord> res = new ArrayList<>();
        for (HocrSection child : childs)
            res.addAll(child.getAllWords());
        return res;
    }

    @Override
    protected void updateBbox() {
        boolean needToUpdateParent = false;
        Rectangle neoBBox = new Rectangle(0, 0, -1, -1);
        for (HocrSection child : childs) {
            neoBBox = neoBBox.union(child.bbox);
        }
        if (!neoBBox.equals(bbox))
            needToUpdateParent = true;
        this.bbox = neoBBox;
        if (needToUpdateParent && parent != null)
            parent.updateBbox();
    }

    @Override
    protected boolean canBeRemoved() {
        ArrayList<HocrSection> toDestroy = new ArrayList<>();

        for (HocrSection child : childs)
            if (child.canBeRemoved())
                toDestroy.add(child);

        for (HocrSection del : toDestroy)
            childs.remove(del);

        if (!childs.isEmpty() && !toDestroy.isEmpty())
            updateBbox();

        return childs.isEmpty();
    }

    @Override
    protected Element toHtmlElement() {
        Element elem = new Element(Tag.valueOf(getElementTag()), "");
        elem.attr(HTMLConstants.CLASS, getElementClassAttribute());
        elem.attr(HTMLConstants.ID, getId());
        elem.attr(HTMLConstants.TITLE, getBBoxHocrString());
        for (HocrSection child : getChilds()) {
            elem.appendChild(child.toHtmlElement());
        }
        return elem;
    }

}

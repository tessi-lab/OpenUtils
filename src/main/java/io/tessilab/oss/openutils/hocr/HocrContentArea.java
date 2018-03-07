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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jsoup.nodes.Element;

/* Class: Content Area in Hocr documents*/
public class HocrContentArea extends HocrNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(HocrContentArea.class);

    private static final long serialVersionUID = 1842333658103294624L;

    public HocrContentArea(HocrSection parent, Element element) throws HocrSyntaxException {
        super(parent, element);
    }

    public HocrContentArea(HocrSection parent) {
        super(parent);
    }

    @Override
    protected HocrSection getChildFromElement(Element element) {
        try {
            return new HocrParagraph(this, element);
        } catch (HocrSyntaxException hse) {
            LOGGER.debug("Exception while creating an HocrParagraph from an html element.", hse);
            return null;
        }
    }

    @Override
    protected String getElementTag() {
        return HTMLConstants.TAG_DIV;
    }

    @Override
    protected String getElementClassAttribute() {
        return HTMLConstants.AREA_CLASS;
    }
}

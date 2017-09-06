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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;

/* Class: Paragraph in Hocr documents */
public class HocrParagraph extends HocrNode {

    private static final Logger LOGGER = LogManager.getLogger(HocrParagraph.class);

    private static final long serialVersionUID = 8562855616568852356L;

    private ReadingDirection dir;

    public HocrParagraph(HocrSection parent, Element element) throws HocrSyntaxException {
        super(parent, element);
        if ("ltr".equals(element.attr("dir"))) {
            dir = ReadingDirection.LEFT_TO_RIGHT;
        } else if ("rtl".equals(element.attr("dir"))) {
            dir = ReadingDirection.RIGHT_TO_LEFT;
        } else {
            dir = ReadingDirection.UNDEFINED_DIRECTION;
        }
    }

    public HocrParagraph(HocrSection parent) {
        super(parent);
        dir = ReadingDirection.UNDEFINED_DIRECTION;
    }

    @Override
    protected HocrSection getChildFromElement(Element element) {
        try {
            return new HocrLine(this, element);
        } catch (HocrSyntaxException hse) {
            LOGGER.debug("Exception while creating an HocrLine from an html element.", hse);
            return null;
        }
    }

    public ReadingDirection getReadingDirection() {
        return this.dir;
    }

    @Override
    protected String getElementTag() {
        return HTMLConstants.TAG_P;
    }

    @Override
    protected String getElementClassAttribute() {
        return HTMLConstants.PAR_CLASS;
    }

    @Override
    protected Element toHtmlElement() {
        Element elem = super.toHtmlElement();
        elem.attr("dir", ReadingDirection.toHocrString(this.getReadingDirection()));
        return elem;
    }

}

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
package io.tessilab.oss.openutils.testing;

import io.tessilab.oss.openutils.FileUtils;
import io.tessilab.oss.openutils.hocr.HocrContentArea;
import io.tessilab.oss.openutils.hocr.HocrDocument;
import io.tessilab.oss.openutils.hocr.HocrLine;
import io.tessilab.oss.openutils.hocr.HocrPage;
import io.tessilab.oss.openutils.hocr.HocrParagraph;
import io.tessilab.oss.openutils.hocr.HocrSection;
import io.tessilab.oss.openutils.hocr.HocrXWord;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Andres BEL ALONSO
 */
public class HocrTestUtilities {
    
    private HocrTestUtilities() {
        
    }

    public static HocrPage loadPageFromResource(String resourcePath) {
        HocrPage page = null;
        try {
            String htmlContent = FileUtils.fileToString(resourcePath, true);
            HocrDocument hd = new HocrDocument(htmlContent);
            page = hd.getPageStream().findFirst().orElse(null);
        } catch (Exception ioe) {
            System.err.println("Error: read file error: " + ioe);
        }
        return page;
    }

    public static boolean uniqueAllSections(HocrDocument doc) {
        Set<String> pageSet = new HashSet<>();
        Set<String> areaSet = new HashSet<>();
        Set<String> parSet = new HashSet<>();
        Set<String> lineSet = new HashSet<>();
        Set<String> wordSet = new HashSet<>();

        List<HocrPage> pages = doc.getPageStream().collect(Collectors.toList());
        for (HocrPage page : pages) {
            if (!pageSet.add(page.getId())) {
                return false;
            }
            for (HocrSection hs1 : page.getChilds()) {
                HocrContentArea hca = (HocrContentArea) hs1;
                if (!areaSet.add(hca.getId())) {
                    return false;
                }
                for (HocrSection hs2 : hca.getChilds()) {
                    HocrParagraph hp = (HocrParagraph) hs2;
                    if (!parSet.add(hp.getId())) {
                        return false;
                    }
                    for (HocrSection hs3 : hp.getChilds()) {
                        HocrLine hl = (HocrLine) hs3;
                        if (!lineSet.add(hl.getId())) {
                            return false;
                        }
                        for (HocrSection hs4 : hl.getChilds()) {
                            HocrXWord hw = (HocrXWord) hs4;
                            if (!wordSet.add(hw.getId())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static HocrXWord createWord(String id, String text) {
        return new HocrXWord(id, new Rectangle(0, 0, 100, 200), null, text, 100);
    }

    public static HocrXWord createWord(String id, Rectangle r) {
        return new HocrXWord(id, r, null, "dummyText", 100);
    }
}

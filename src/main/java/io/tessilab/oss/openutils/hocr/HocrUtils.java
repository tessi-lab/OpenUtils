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
import io.tessilab.oss.openutils.bbox.BBoxableUtils;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HocrUtils {

    private HocrUtils() {
    }

    /**
     * fuse multiple hocr pages into one
     * 
     * @param hocrs
     *            an array of hocrpages
     * @return the final hocr page
     */
    public static HocrPage fuseHOCRs(HocrPage[] pages, int pageNumber) {
        HocrDocument hd = new HocrDocument();
        HocrPage fusion = new HocrPage(pageNumber, hd);
        for (HocrPage p : pages) {
            for (HocrSection hs : p.getChilds()) {
                HocrContentArea hca = (HocrContentArea) hs;
                fusion.getChilds().add(hca);
            }
        }
        fusion.rewriteAllIds();
        return fusion;
    }

    /**
     * @param hocrPage
     * @param wordValues
     * @param bboxables
     * @param offsetHocr
     * @return The list of the ids of the added words.
     */
    public static <T extends BBoxable> List<String> addWords(HocrPage hocrPage, List<String> wordValues, List<T> bboxables, AffineTransform offsetHocr) {
        if (wordValues.isEmpty() || (wordValues.size() != bboxables.size()))
            return Collections.emptyList();

        int nbWordsInit = hocrPage.getAllWords().size();

        // STEP 1: adding the words
        List<String> addedIds = new ArrayList<>();
        int nbWordsToAdd = wordValues.size();
        for (int k = 0; k < nbWordsToAdd; k++) {
            Rectangle r = bboxables.get(k).getBBox().getBounds();
            HocrXWord w = new HocrXWord("dummy", r, null, wordValues.get(k).trim(), 100.0);
            String id = hocrPage.addWord(w);
            addedIds.add(id);
        }

        // STEP 2: fix the border
        Rectangle2D rect = BBoxableUtils.bBoxUnion(bboxables);

        double offsetX = (rect.getX() < 0) ? -rect.getX() : 0;
        double offsetY = (rect.getY() < 0) ? -rect.getY() : 0;

        if (nbWordsToAdd == 1) {
            // No words were detected, we set the pageBox to the added
            // words and recompute the offset based on rect
            offsetX = -(rect.getX() + 0.5);
            offsetY = -(rect.getY() + 0.5);
            hocrPage.setBBox(new Rectangle(0, 0, (int) (rect.getWidth() + 0.5), (int) (rect.getHeight() + 0.5)));
        }

        if (nbWordsInit != 0) {
            offsetHocr.concatenate(AffineTransform.getTranslateInstance(offsetX, offsetY));
        }

        return addedIds;
    }

    public static Rectangle readBoundingBox(String str) {
        String[] splits = str.split(";?\\s+");

        int index = 0;
        String cur;
        // go to the definition of the bbox
        cur = splits[0];
        while (!"bbox".equals(cur)) {
            index++;
            cur = splits[index];
        }
        index++; // to get to the first number

        int x = Integer.parseInt(splits[index]);
        int y = Integer.parseInt(splits[index + 1]);
        int w = Integer.parseInt(splits[index + 2]) - x;
        int h = Integer.parseInt(splits[index + 3]) - y;

        return new Rectangle(x, y, w, h);
    }

}

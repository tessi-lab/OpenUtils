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

import io.tessilab.oss.openutils.StringUtils;
import io.tessilab.oss.openutils.bbox.BBoxable;
import io.tessilab.oss.openutils.bbox.BBoxableUtils;

import io.tessilab.oss.openutils.bbox.BBoxer;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HocrUtils {

    private static final Pattern PATTERN_BBOX = Pattern.compile("^.*bbox (.*?);.*$");
    private static final Pattern PATTERN_BBOX_AND_CONF = Pattern.compile("bbox (.*); x_wconf (.*).*$");

    private HocrUtils() {
    }

    /**
     * fuse multiple hocr pages into one
     *
     * @param pages
     * @param pageNumber
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

    public static Document parseToDocument(String htmlContent) {
        return Jsoup.parse(htmlContent);
    }

    public static Rectangle buildRectangleFromHtmlBBox(String title) {
        Matcher matcher = PATTERN_BBOX.matcher(title);
        if (matcher.matches()) {
            return buildRectangle(matcher.group(1));
        }
        return null;
    }

    public static Rectangle buildRectangle(String bboxCoor) {
        String[] splits = bboxCoor.split(";?\\s+");
        int index = 0;
        int x1 = Integer.parseInt(splits[index]);
        int y1 = Integer.parseInt(splits[index + 1]);
        int w = Integer.parseInt(splits[index + 2]) - x1;
        int h = Integer.parseInt(splits[index + 3]) - y1;
        return new Rectangle(x1, y1, w, h);
    }

    public static Document parseHtmlFileToDocument(String htmlFilePath) throws IOException {
        final File file = new File(htmlFilePath);
        final String htmlContent = IOUtils.toString(file.toURI(), Charsets.toCharset("UTF-8"));
        return parseToDocument(htmlContent);
    }

    public static BBoxer getBBoxerForPage(Document document) {
        final Elements ocrPage = getElementsByClass(document,"ocr_page");
        final String title = ocrPage.attr("title");
        return new BBoxer(buildRectangleFromHtmlBBox(title));
    }

    public static List<HocrXWord> buildHocrXWords(Element element) {
        List<HocrXWord> words = new ArrayList<>();
        final Elements ocrxWords = getElementsByClass(element, "ocrx_word");
        ocrxWords.forEach(w -> {
            final String text = w.text().replaceAll("’", "").replaceAll("N‘", "N°").replaceAll("n‘", "n°").replaceAll("N\"", "N°");
            if (StringUtils.isNotEmpty(text)) {
                final String title = w.attr("title");
                Matcher matcher = PATTERN_BBOX_AND_CONF.matcher(title);
                if (matcher.matches()) {
                    final Rectangle rectangle = buildRectangle(matcher.group(1));
                    words.add(new HocrXWord(UUID.randomUUID().toString(), rectangle, null, text, Double.parseDouble(matcher.group(2))));
                }
            }
        });
        return words;
    }

    public static Elements getElementsByClass(Element element, String className) {
        return element.getElementsByClass(className);
    }
}

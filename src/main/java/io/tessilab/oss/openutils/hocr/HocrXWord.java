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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/* Class: Word in Hocr documents */
public class HocrXWord extends HocrSection {

    private static final long serialVersionUID = -2271896126154405888L;

    private static final int MIN_HEIGHT = 6;
    private static final int MIN_WIDTH = 6;

    private static final String REAL_VALUE = "value";
    private static final String NORMALIZED_VALUE = "normalizedValue";

    private Map<String, String> cachedValues;
    private double confidence;

    private boolean isVoid = false;

    // The origin is related to the split method
    private HocrXWord origin;

    // --------

    /**
     * Constructor.</wr> Creates a whole new word. </br> Warning: we remove the
     * white spaces at the beginning or the end.
     * 
     * @param id
     *            The id of the word. Identifies a word. It should be unique.
     * @param bbox
     *            The position of the word in its page.
     * @param parent
     *            The parent of the word (line).
     * @param text
     *            The value of the word.
     * @param conf
     *            The confidence in the value.
     */
    public HocrXWord(String id, Rectangle bbox, HocrSection parent, String text, double conf) {
        this.setId(id);
        this.bbox = bbox;
        this.parent = parent;
        this.cachedValues = new HashMap<>();
        String trimmed = text.trim();
        cachedValues.put(REAL_VALUE, trimmed);
        cachedValues.put(NORMALIZED_VALUE, StringUtils.cleanUpString(trimmed, true, false, true, new String[] { "€" }));
        this.confidence = conf;
        isVoid = "".equals(trimmed);
        origin = this;
    }

    /**
     * Constructor.</br> Parse an html element.
     * 
     * @param parent
     *            The parent of the word (line).
     * @param element
     *            The Html element.
     * @throws HocrSyntaxException
     */
    public HocrXWord(HocrSection parent, Element element) throws HocrSyntaxException {
        super(parent, element);
        try {
            String temp = element.attr(HTMLConstants.CONFIDENCE);
            if (temp == "") // TODO: temp fix to read legacy hocr
                temp = element.attr("confidence");
            confidence = Double.parseDouble(temp); // switch to int ?
        } catch (NumberFormatException e) {
            throw new HocrSyntaxException("Confidence not found");
        }
        this.cachedValues = new HashMap<>();
        // Read the text value
        String text = element.text().trim(); // combined text of its children
        cachedValues.put(REAL_VALUE, text);
        cachedValues.put(NORMALIZED_VALUE, StringUtils.cleanUpString(text, true, false, true, new String[] { "€" }));
        isVoid = "".equals(text);
        origin = this;
    }

    /**
     * Constructor by copy.</br> WARNING: you should probably change the id of
     * the copy.
     * 
     * @param other
     *            another HocrXWord.
     */
    protected HocrXWord(HocrXWord other) {
        this.setId(other.getId());
        this.cachedValues = new HashMap<>(other.cachedValues);
        bbox = other.bbox;
        parent = other.parent;
        isVoid = other.isVoid;
        confidence = other.confidence;
        origin = other.origin;
    }

    @Override
    protected String getElementTag() {
        return HTMLConstants.TAG_SPAN;
    }

    @Override
    protected String getElementClassAttribute() {
        return HTMLConstants.WORD_CLASS;
    }

    @Override
    public List<HocrXWord> getAllWords() {
        ArrayList<HocrXWord> res = new ArrayList<>(1);
        res.add(this);
        return res;
    }

    @Override
    protected boolean canBeRemoved() {
        return isVoid || isTooSmall(MIN_HEIGHT, MIN_WIDTH) || "".equals(getNormalizedValue());
    }

    private boolean isTooSmall(int height, int width) {
        return this.getHeight() < height || this.getWidth() < width;
    }

    @Override
    protected void updateBbox() {
        // Nothing to do
    }

    /**
     * @return The string value of this HocrxWord
     */
    public String getValue() {
        return cachedValues.get(REAL_VALUE);
    }

    /**
     * @return The cached string value of this HocrxWord. </br>Can return null.
     */
    public String get(String key) {
        return cachedValues.get(key);
    }

    /**
     * 
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        cachedValues.put(key, value);
    }

    /**
     * @deprecated
     * @param text
     */
    @Deprecated
    public void setValue(String text) {
        cachedValues.put(REAL_VALUE, text.trim());
    }

    /**
     * @return The string value of this HocrXWord that has been normalized (we
     *         removed the accents and the non ascii characters, but kept the
     *         symbol €).
     */
    public String getNormalizedValue() {
        return cachedValues.get(NORMALIZED_VALUE);
    }

    /**
     * @return The OCR confidence of this HocrXWord.
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * @deprecated
     * @param conf
     */
    @Deprecated
    public void setConfidence(double conf) {
        this.confidence = conf;
    }

    @Override
    public String toString() {
        return getValue();
    }

    protected void setOrigin(HocrXWord w) {
        this.origin = w;
    }

    public HocrXWord getOrigin() {
        return this.origin;
    }

    public double getNonAlphaNumRatio() {
        String s = getValue();
        if (s.length() == 0)
            return 0.0;
        int nbNonAlphaNum = (int) s.chars().filter(c -> !Character.isLetterOrDigit(c)).count();
        return nbNonAlphaNum / (double) s.length();
    }

    /**
     * Split an HocrXWord on several positions of its value.</br> We try to be
     * exact by taking into account the width of the space between the
     * characters (15% of a char width).
     * 
     * @param splitIndexes
     *            An array containing the indexes of the split. (an index
     *            correspond to the beginning of a new word)
     * @param charWidth
     *            The width of a character, in pixels.
     * @return An array containing the new HocrXWord born from the split. null
     *         if no split were made (the input array was empty).
     */
    public List<HocrXWord> split(List<Integer> splitIndexes, int charWidth) {
        if (splitIndexes == null || splitIndexes.isEmpty()) {
            return Collections.emptyList();
        }
        Collections.sort(splitIndexes); // to be careful

        int betweenCharWidth = (int) (charWidth * 0.15);

        ArrayList<HocrXWord> wordsResult = new ArrayList<>();
        int splitBegin, splitEnd;
        for (int k = 0; k < splitIndexes.size() + 1; k++) {
            if (k == 0) {
                splitBegin = 0;
            } else {
                splitBegin = splitIndexes.get(k - 1);
            }
            if (k == splitIndexes.size()) {
                splitEnd = getValue().length();
            } else {
                splitEnd = splitIndexes.get(k);
            }

            HocrXWord w = new HocrXWord(this);
            w.setId(new String(w.getId() + "_" + (k + 1)));
            int x1 = (int) this.getX1() + splitBegin * (charWidth + betweenCharWidth);
            int width;
            if (k == splitIndexes.size()) {
                width = (int) this.getX2() - x1;
            } else { // general case
                width = (splitEnd - splitBegin) * (charWidth + betweenCharWidth);
            }
            w.bbox = new Rectangle(x1, this.bbox.y, width, this.bbox.height);
            String temp = this.getValue().substring(splitBegin, splitEnd);
            w.cachedValues.put(REAL_VALUE, temp);
            w.cachedValues.put(NORMALIZED_VALUE, StringUtils.cleanUpString(temp, true, false, true, new String[] { "€" }));
            wordsResult.add(w);
        }

        return wordsResult;
    }

    /**
     * Reform the origin expression corresponding to a list of words, before
     * they were lexically split.</br> For better results, the words should be
     * ordered by their position on a line.
     * 
     * @param words
     * @param normalized
     * @return
     */
    public static String buildOriginExpression(List<HocrXWord> words, boolean normalized) {
        StringBuilder builder = new StringBuilder();
        HocrXWord lastOrigin = null;
        for (HocrXWord w : words) {
            if (!w.getOrigin().equals(lastOrigin)) {
                builder.append(" ");
                lastOrigin = w.getOrigin();
            }
            if (normalized)
                builder.append(w.getNormalizedValue());
            else
                builder.append(w.getValue());
        }
        return builder.toString().trim();
    }

    /**
     * Returns the origin list of words corresponding to a given list of
     * words.</br> For better results, the words should be ordered by their
     * position on a line.
     * 
     * @param words
     * @return
     */
    public static List<HocrXWord> buildOriginWordList(List<HocrXWord> words) {
        List<HocrXWord> originList = new ArrayList<>();
        HocrXWord lastOrigin = null;
        for (HocrXWord w : words) {
            if (!w.getOrigin().equals(lastOrigin)) {
                lastOrigin = w.getOrigin();
                originList.add(w);
            }
        }
        return originList;
    }

    /**
     * It is faster to build a concatenation of words with this method than by
     * using strem collectors joining.
     * 
     * @param words
     * @param normalized
     * @return
     */
    public static String buildConcatExpression(List<HocrXWord> words, boolean normalized) {
        StringBuilder builder = new StringBuilder();
        for (HocrXWord w : words) {
            if (normalized)
                builder.append(w.getNormalizedValue());
            else
                builder.append(w.getValue());
        }
        return builder.toString();
    }

    /**
     * Compute the mean OCR confidence of a list of HocrXWords, weighted by
     * their lengths.
     * 
     * @param words
     *            A list of HocrXWord.
     * @return The mean weighted confidence.
     */
    public static double computeMeanWeightedConfidence(List<HocrXWord> words) {
        double temp = 0.0;
        int totalLength = 0;
        for (HocrXWord w : words) {
            int length = w.getNormalizedValue().length();
            totalLength += length;
            temp += w.getConfidence() * length;
        }
        if (totalLength == 0)
            return 0;
        return temp / totalLength;
    }

    /**
     * Compute the mean weighted confidence in the list of origin words. This is
     * a combination of
     * {@link io.tessilab.utils.hocr.HocrXWord#buildOriginWordList(list)} and
     * {@link io.tessilab.utils.hocr.HocrXWord#computeMeanWeightedConfidence(list)}
     * 
     * @return
     */
    public static double fasterMeanConfidenceOrigin(List<HocrXWord> words) {
        double temp = 0.0;
        int totalLength = 0;
        HocrXWord lastOrigin = null;
        for (HocrXWord w : words) {
            if (!w.getOrigin().equals(lastOrigin)) {
                lastOrigin = w.getOrigin();
                int length = w.getNormalizedValue().length();
                totalLength += length;
                temp += w.getConfidence() * length;
            }
        }
        if (totalLength == 0)
            return 0;
        return temp / totalLength;
    }

    @Override
    protected Element toHtmlElement() {
        Element elem = new Element(Tag.valueOf(getElementTag()), "");
        elem.attr(HTMLConstants.CLASS, getElementClassAttribute());
        elem.attr(HTMLConstants.ID, getId());
        elem.attr(HTMLConstants.CONFIDENCE, Integer.toString((int) getConfidence()));
        elem.attr(HTMLConstants.TITLE, getBBoxHocrString());
        elem.appendText(getValue());
        return elem;
    }

}

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
package io.tessilab.oss.openutils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringUtils {

    private static final Logger LOGGER = LogManager.getLogger(StringUtils.class);

    private static final Pattern INCOMBININGDIACRITICALMARKSPATTERN = Pattern.compile("[\\p{InCombiningDiacriticalMarks}]");
    private static final Pattern DIGITSPATTERN = Pattern.compile("[0123456789]");
    private static Pattern lastNonAsciiToKeepPattern = null;
    private static String[] lastNonAsciiToKeep = new String[0];

    private StringUtils() {
        // dummy private constructor
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            LOGGER.trace(e);
            return false;
        }
    }

    public static boolean isFloat(String input) {
        try {
            String input2 = input.replaceAll("[,]+", ".");
            Float.parseFloat(input2);
            return true;
        } catch (Exception e) {
            LOGGER.trace(e);
            return false;
        }
    }

    public static boolean isUnit(String input) {
        /* Poids */
        if ("g".equals(input) || "kg".equals(input)) {
            return true;
        }
        /* Volume */
        if ("ml".equals(input) || "l".equals(input) || "cl".equals(input)) {
            return true;
        }
        /* temps */
        if ("min".equals(input)) {
            return true;
        }
        /* Stockage */
        if ("b".equals(input) || "gb".equals(input)) {
            return true;
        }
        /* distance */
        if ("m".equals(input) || "mm".equals(input) || "cm".equals(input) || "km".equals(input)) {
            return true;
        }
        /* percent */
        if ("%".equals(input)) {
            return true;
        }
        return false;
    }

    /**
     * Tests if the string is of the type "25cl" or "300g", for example.
     *
     * @param input
     * @return
     */
    public static boolean isNumberUnit(String input) {
        if (input.length() >= 2) {
            /* The unit is in the end. */
            /* Begin with 1 length unit, next with 2 */
            String unit = Character.toString(input.charAt(input.length() - 1));
            String rest = input.substring(0, input.length() - 1);
            if (StringUtils.isUnit(unit) && (isInteger(rest) || isFloat(rest))) {
                return true;
            } else {
                unit = input.substring(input.length() - 1);
                rest = input.substring(0, input.length() - 2);
                if (StringUtils.isUnit(unit) && (isInteger(rest) || isFloat(rest))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String cleanUpString(String s, boolean removeAccents, boolean removeDigits, boolean toLowerCase, String[] nonAsciiToKeep) {
        String str = Normalizer.normalize(s, Normalizer.Form.NFD);

        if (removeAccents) {
            str = INCOMBININGDIACRITICALMARKSPATTERN.matcher(str).replaceAll("");
        }

        Pattern nonAsciiRegex;
        if (lastNonAsciiToKeepPattern == null || !Arrays.equals(nonAsciiToKeep, lastNonAsciiToKeep)) {
            // Prepare the symbols to save
            StringBuilder regexBuilder = new StringBuilder("^\\p{ASCII}&&[^\\p{InCombiningDiacriticalMarks}]");
            if (nonAsciiToKeep != null && nonAsciiToKeep.length > 0) {
                regexBuilder.append("&&[^");
                for (String nonAscii : nonAsciiToKeep) {
                    regexBuilder.append(nonAscii);
                }
                regexBuilder.append("]");
            }
            regexBuilder.insert(0, "[");
            regexBuilder.append("]");
            lastNonAsciiToKeepPattern = Pattern.compile(regexBuilder.toString());
            lastNonAsciiToKeep = nonAsciiToKeep == null ? nonAsciiToKeep : Arrays.copyOf(nonAsciiToKeep, nonAsciiToKeep.length);
        }
        nonAsciiRegex = lastNonAsciiToKeepPattern;
        str = nonAsciiRegex.matcher(str).replaceAll("");

        if (removeDigits) {
            str = DIGITSPATTERN.matcher(str).replaceAll("");
        }

        if (toLowerCase) {
            str = str.toLowerCase();
        }

        str = Normalizer.normalize(str, Normalizer.Form.NFC);
        return str.trim();
    }

    public static String banCharFromList(String s, String[] charList) {
        String res = new String(s);
        for (String danger : charList) {
            res = res.replaceAll(danger, "");
        }
        return res;
    }

    public static String banCharAtTheEndFromList(String s, char[] charList) {
        String res = new String(s);
        boolean checked = false;
        while (!"".equals(res) && !checked) {
            checked = true;
            char lastChar = res.charAt(res.length() - 1);
            for (char c : charList) {
                if (lastChar == c) {
                    checked = false;
                    res = res.substring(0, res.length() - 1);
                    break;
                }
            }
        }
        return res;
    }

    public static String banCharAtTheBeginningFromList(String s, char[] charList) {
        String res = new String(s);
        boolean checked = false;
        while (!"".equals(res) && !checked) {
            checked = true;
            char firstChar = res.charAt(0);
            for (char c : charList) {
                if (firstChar == c) {
                    checked = false;
                    res = res.substring(1);
                    break;
                }
            }
        }
        return res;
    }

    /**
     * Returns TRUE if s belong to the list.
     * 
     * @param s
     * @param list
     * @return
     */
    public static boolean isFromList(String s, String[] list) {
        for (String str : list) {
            if (s.equals(str))
                return true;
        }
        return false;
    }

    /**
     * Search for other units types that are not conventional and replace it by
     * the conventional ones
     */
    public static String unifyUnits(String s) {
        /* Unit: mass */
        if ("gr".equals(s) || "grammes".equals(s))
            return new String("g");
        /* Unit: volume */
        if ("litre".equals(s) || "litres".equals(s) || "litro".equals(s))
            return new String("l");
        if ("mk".equals(s) || "millilitre".equals(s))
            return new String("ml");
        if ("centilitre".equals(s) || "centilitres".equals(s))
            return new String("cl");

        return s;
    }

    /**
     * returns TRUE if the word is a determinant or a preposition...
     * 
     * @param s
     * @return
     */
    public static boolean isDeterminant(String s) {
        /* Defenis */
        if ("la".equals(s) || "le".equals(s) || "les".equals(s)) {
            return true;
        }
        /* Indéfinis */
        if ("une".equals(s) || "un".equals(s) || "des".equals(s)) {
            return true;
        }
        /* Contracted */
        if ("au".equals(s) || "du".equals(s) || "aux".equals(s)) {
            return true;
        }
        /* With appostrophes */
        if ("l'".equals(s) || "d'".equals(s)) {
            return true;
        }
        /* Prepositions */
        if ("a".equals(s) || "de".equals(s) || "en".equals(s) || "avec".equals(s) || "et".equals(s) || "pour".equals(s) || "sans".equals(s)) {
            return true;
        }
        /* Adjectives possessive */
        if ("se".equals(s) || "ses".equals(s)) {
            return true;
        }
        /* Adjectives demonstrative */
        if ("ce".equals(s) || "cet".equals(s) || "cette".equals(s) || "ces".equals(s)) {
            return true;
        }
        /* Also foreign articles */
        if ("di".equals(s) || "&".equals(s) || "or".equals(s)) {
            return true;
        }
        return false;
    }

    /**
     * returns TRUE if the word is a vitamin name
     * 
     * @param s
     * @return
     */
    public static boolean isVitaminName(String s) {
        /* Letter vitamin */
        if ("h".equals(s) || "d".equals(s) || "e".equals(s) || "c".equals(s)) {
            return true;
        }
        if (s.length() == 2) {
            if ((s.charAt(0) == 'b' && (s.charAt(1) == '1' || s.charAt(1) == '2' || s.charAt(1) == '3' || s.charAt(1) == '5' || s.charAt(1) == '6' || s.charAt(1) == '8' || s.charAt(1) == '9')) || "pp".equals(s)) {
                return true;
            }
            if ((s.charAt(0) == 'k') && (s.charAt(1) == '1' || s.charAt(1) == '2')) {
                return true;
            }
        }
        if (s.length() == 3) {
            if (s.charAt(0) == 'b' && s.charAt(1) == '1' && (s.charAt(2) == '2' || s.charAt(2) == '0' || s.charAt(2) == '1' || s.charAt(2) == '3' || s.charAt(2) == '5' || s.charAt(2) == '6' || s.charAt(2) == '7')) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEAN13(String s) {
        if (s.length() == 13 && StringUtils.isInteger(s.substring(0, 6)) && StringUtils.isInteger(s.substring(6))) {
            return true;
        }
        return false;
    }

}

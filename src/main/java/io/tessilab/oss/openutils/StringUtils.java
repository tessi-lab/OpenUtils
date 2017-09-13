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

    /**
     * 
     * @param input A string to verify if it is an integer
     * @return True if the input string is an Integer
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
//            LOGGER.trace(e);
            return false;
        }
    }

    /**
     * 
     * @param input A string to verify if it is an float
     * @return True if the input string is a float
     */
    public static boolean isFloat(String input) {
        try {
            String input2 = input.replaceAll("[,]+", ".");
            Float.parseFloat(input2);
            return true;
        } catch (Exception e) {
//            LOGGER.trace(e);
            return false;
        }
    }

    /**
     * Verifies g and kg for weights, ml, l and cl for volumes, min for time, b and gb for hardware storage units, 
     * m, mm, cm and km for a distance and a %. 
     * @param input An input string that will be tested in its integrity. 
     * @return True if the input string represents the abbreviation of an unit
     */
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
     * @param input The input string to test
     * @return True if the string is a numbre followed by an unit
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

    /**
     * Applies a global process to clean a string
     * @param s The string to clean 
     * @param removeAccents true if the accents must be removed
     * @param removeDigits true if the digits of must be deleted
     * @param toLowerCase true if all the letters of the string must be switched to lower case letters
     * @param nonAsciiToKeep A table with the non ascii characters to keep. 
     * @return The cleaned string. 
     */
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

    /**
     * Bans all the string specied in stringlist from the given string
     * @param s The string to modify
     * @param stringList The table with all the strings that must be removed
     * @return s without the elements that matchs (in the sense of String.replaceAll(stringList[i],"")) with
     * stringList
     */
    public static String banCharFromList(String s, String[] stringList) {
        String res = s;
        for (String danger : stringList) {
            res = res.replaceAll(danger, "");
        }
        return res;
    }

    /**
     * Deletes the last character from a string if the string is not empty and it is one of the characters of 
     * charList
     * @param s The string to process
     * @param charList The list of posible characters to ban 
     * @return s without his last character if his last character was in charList
     */
    public static String banCharAtTheEndFromList(String s, char[] charList) {
        String res = s;
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

    /**
     * Deletes the first character from a string if the string is not empty and it is one of the characters of 
     * charList
     * @param s The string to modify
     * @param charList The list of posible characters to ban 
     * @return  s without his first character if his last character was in charList
     */
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
     * @param s The string to check 
     * @param list The list of string to verify
     * @return True if s is equal to at least one string of list
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
     * @param s The string representing an unit
     * @return A gramm will be returned as g, a liter as l, a mililiter as ml and a centiliter as cl
     */
    public static String unifyUnits(String s) {
        /* Unit: mass */
        if ("gr".equals(s) || "grammes".equals(s))
            return "g";
        /* Unit: volume */
        if ("litre".equals(s) || "litres".equals(s) || "litro".equals(s))
            return "l";
        if ("mk".equals(s) || "millilitre".equals(s))
            return "ml";
        if ("centilitre".equals(s) || "centilitres".equals(s))
            return "cl";

        return s;
    }

    /**
     * returns TRUE if the word is a french determinant or a french preposition...
     * 
     * @param s The string to analyse
     * @return True is s is a french determinant
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
     * @param s The string to analyse
     * @return True if it represents a vitamine name
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

    /**
     * This function verifies that s is an EAN13. It verifies if it contains only 13 numbers and its checksum. 
     * @param s The string to verify
     * @return true if the string is a coherent ean13 barcode
     */
    public static boolean isEAN13(String s) {
        if (s.length() == 13 && StringUtils.isInteger(s.substring(0, 6)) && StringUtils.isInteger(s.substring(6))) {
            // checksum verification at : http://barcode-coder.com/en/ean-13-specification-102.html
            int[] intRep = new int[13];
            for(int i=0; i<13; i++) {
                // The the numbers are numerated from right to left
                intRep[12 - i] = Integer.valueOf(s.substring(i, i+1));
            }
            // the control digit is in intRep[0]
            int odd = intRep[1] + intRep[3] + intRep[5] + intRep[7] + intRep[9] + intRep[11];
            int even = intRep[2] + intRep[4] + intRep[6] + intRep[8] + intRep[10] + intRep[12];
            int expectedValue = ((10 - (3*odd + even)%10))%10;
            return expectedValue==intRep[0];
        }
        return false;
    }

}

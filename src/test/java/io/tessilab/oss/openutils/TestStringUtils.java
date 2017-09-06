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

import io.tessilab.oss.openutils.StringUtils;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestStringUtils {

    @Test
    public void testCleanUpString() {
        String original = "EÈÉÀÙéçà029a";

        assertEquals("Bad Clean Up", original, StringUtils.cleanUpString(original, false, false, false, new String[] {}));
        assertEquals("Bad Clean Up", "EEEAUeca029a", StringUtils.cleanUpString(original, true, false, false, new String[] {}));
        assertEquals("Bad Clean Up", "EÈÉÀÙéçàa", StringUtils.cleanUpString(original, false, true, false, new String[] {}));
        assertEquals("Bad Clean Up", "eèéàùéçà029a", StringUtils.cleanUpString(original, false, false, true, new String[] {}));
    }

    @Test
    public void testCleanUpStringKeep() {
        String original = "ascii$€@£";

        assertEquals("Bad Clean Up", "ascii$@", StringUtils.cleanUpString(original, false, false, false, new String[] {}));
        assertEquals("Bad Clean Up", "ascii$€@", StringUtils.cleanUpString(original, false, false, false, new String[] { "€" }));
        assertEquals("Bad Clean Up", "ascii$@£", StringUtils.cleanUpString(original, false, false, false, new String[] { "£" }));
        assertEquals("Bad Clean Up", "ascii$€@£", StringUtils.cleanUpString(original, false, false, false, new String[] { "€", "£" }));
    }

    @Test
    public void testCleanUpStringMix() {
        String original = "EÈÉÀÙéçà029a$*@£€";

        assertEquals("Bad Clean Up", "EEEAUecaa$*@", StringUtils.cleanUpString(original, true, true, false, new String[] {}));
        assertEquals("Bad Clean Up", "eeeaueca029a$*@", StringUtils.cleanUpString(original, true, false, true, new String[] {}));
        assertEquals("Bad Clean Up", "eèéàùéçàa$*@", StringUtils.cleanUpString(original, false, true, true, new String[] {}));
        assertEquals("Bad Clean Up", "eeeauecaa$*@", StringUtils.cleanUpString(original, true, true, true, new String[] {}));

        assertEquals("Bad Clean Up", "eeeauecaa$*@£€", StringUtils.cleanUpString(original, true, true, true, new String[] { "€", "£" }));
    }

    @Test
    public void testCleanUpStringMixHard() {
        assertEquals("Bad Clean Up", "ooerdqkfo©p{a√ﬁe", StringUtils.cleanUpString("oOœeR®†dqKFƒÒ©pø2’2{78å·›√ﬁË", true, true, true, new String[] { "©", "√", "ﬁ" }));
    }
}

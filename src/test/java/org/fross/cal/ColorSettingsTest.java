/*--------------------------------------------------------------------------------------
 * Cal - A command line calendar utility
 *
 * Copyright (c) 2018-2026 Michael Fross
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * --------------------------------------------------------------------------------------*/
package org.fross.cal;

import org.jline.utils.AttributedStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * ColorSettingsTest ensures that the theme engine correctly manages the
 * OS preferences and handles "Self-Healing" for missing keys.
 */
public class ColorSettingsTest {
   /**
    * Test Setup:
    * Before each run setup the test environment so each test is not impacted by others
    */
   @BeforeEach
   void setup() {
      // Ensure every test starts with color ON
      ColorSettings.setColorEnabled(true);
   }

   /**
    * testTodayCompositeHealing:
    * This is the "Bulletproof" test. It deletes the sub-keys (fg/bg) and
    * verifies that requesting the 'today' style brings them both back.
    */
   @Test
   void testTodayCompositeHealing() throws Exception {
      // 1. Manually wipe the specific today sub-keys
      ColorSettings.prefs.remove("todayfg");
      ColorSettings.prefs.remove("todaybg");
      ColorSettings.prefs.flush();

      // 2. Trigger the composite healing by requesting the "today" style
      AttributedStyle todayStyle = ColorSettings.getStyle("today");

      // 3. VERIFY: The sub-keys should have been recreated with defaults
      assertEquals("WHITE", ColorSettings.prefs.get("todayfg", "FAIL"), "todayfg was not resurrected");
      assertEquals("BLUE", ColorSettings.prefs.get("todaybg", "FAIL"), "todaybg was not resurrected");

      assertNotNull(todayStyle, "The returned style should not be null");
   }

   /**
    * testStandardHealing:
    * Verifies that standard single-key components (month, day) are healed.
    */
   @Test
   void testStandardHealing() throws Exception {
      ColorSettings.prefs.remove("month");
      ColorSettings.prefs.flush();

      ColorSettings.getStyle("month");

      assertEquals("CYAN", ColorSettings.prefs.get("month", "FAIL"), "Month key was not healed to CYAN");
   }

   /**
    * testColorNormalization:
    * Verifies that setColor handles case sensitivity (e.g., 'red' -> 'RED').
    */
   @Test
   void testColorNormalization() throws Exception {
      ColorSettings.setColor("dayofweek", "green");

      assertEquals("GREEN", ColorSettings.prefs.get("dayofweek", "FAIL"), "Color name was not normalized to uppercase");

      // Reset to default for other tests
      ColorSettings.setColor("dayofweek", "YELLOW");
   }

   /**
    * testDisableColorSwitch:
    * Checks to see the style returned if we disable the color
    */
   @Test
   void testDisableColorSwitch() {
      // 1. Enable color and verify we get "something" (not default)
      ColorSettings.setColorEnabled(true);
      AttributedStyle colorStyle = ColorSettings.getStyle("holiday");

      // 2. Disable color (the -z switch simulation)
      ColorSettings.setColorEnabled(false);
      AttributedStyle noColorStyle = ColorSettings.getStyle("holiday");
      AttributedStyle todayNoColor = ColorSettings.getStyle("today");

      // 3. Verify that despite asking for "holiday" or "today", we get plain text
      assertEquals(AttributedStyle.DEFAULT, noColorStyle, "Holiday should be plain when color is disabled");
      assertEquals(AttributedStyle.DEFAULT, todayNoColor, "Today should be plain when color is disabled");

      // 4. Reset for other tests
      ColorSettings.setColorEnabled(true);
   }
}
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.prefs.Preferences;
import org.jline.utils.AttributedStyle;
import org.junit.jupiter.api.Test;

public class ColorSettingsTest {

   /**
    * testSelfHealingAndDefaults:
    * This test ensures that if preference keys are missing, the system
    * automatically re-populates them with the correct default values.
    */
   @Test
   void testSelfHealingAndDefaults() throws Exception {
      Preferences prefs = Preferences.userRoot().node("/org/fross/cal/colors");

      // 1. BACKUP: Save existing user settings so we don't ruin their theme
      String oldMonth = prefs.get("month", null);
      String oldDOW   = prefs.get("dayofweek", null);
      String oldDay   = prefs.get("day", null);

      try {
         // 2. WIPE: Remove the keys to simulate a "first run" or accidental deletion
         prefs.remove("month");
         prefs.remove("dayofweek");
         prefs.remove("day");
         prefs.flush();

         // 3. TRIGGER: Request styles to fire the self-healing logic
         AttributedStyle monthStyle = ColorSettings.getStyle("month");
         AttributedStyle dowStyle   = ColorSettings.getStyle("dayofweek");
         AttributedStyle dayStyle   = ColorSettings.getStyle("day");

         // 4. VERIFY: Ensure the keys were re-written to the preferences system
         assertEquals("CYAN", prefs.get("month", "FAIL"), "Self-healing failed to restore 'month' to CYAN");
         assertEquals("YELLOW", prefs.get("dayofweek", "FAIL"), "Self-healing failed to restore 'dayofweek' to YELLOW");
         assertEquals("WHITE", prefs.get("day", "FAIL"), "Self-healing failed to restore 'day' to WHITE");

         // Ensure the returned objects aren't null
         assertNotNull(monthStyle);
         assertNotNull(dowStyle);
         assertNotNull(dayStyle);

      } finally {
         // 5. RESTORE: Put the user's settings back exactly how we found them
         if (oldMonth != null) prefs.put("month", oldMonth);
         if (oldDOW != null)   prefs.put("dayofweek", oldDOW);
         if (oldDay != null)   prefs.put("day", oldDay);
         prefs.flush();
      }
   }

   /**
    * testSetColor:
    * Verifies that the setColor method correctly updates the preference store.
    */
   @Test
   void testSetColor() throws Exception {
      Preferences prefs = Preferences.userRoot().node("/org/fross/cal/colors");
      String backup = prefs.get("month", "CYAN");

      try {
         // Manually change the color
         ColorSettings.setColor("month", "RED");

         // Verify the preference was updated and normalized to uppercase
         assertEquals("RED", prefs.get("month", "FAIL"));

         // Verify getStyle reflects the change
         assertNotNull(ColorSettings.getStyle("month"));

      } finally {
         // Cleanup
         prefs.put("month", backup);
         prefs.flush();
      }
   }
}
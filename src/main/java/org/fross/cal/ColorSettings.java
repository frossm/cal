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

import java.util.prefs.Preferences;
import org.jline.utils.AttributedStyle;

public class ColorSettings {
   private static final Preferences prefs = Preferences.userRoot().node("/org/fross/cal/colors");

   /**
    * getStyle: Returns the JLine style and heals missing keys
    */
   public static AttributedStyle getStyle(String key) {
      // Normalize to lowercase so "DayOfWeek" or "dayofweek" both work
      key = key.toLowerCase();

      String defaultValue = switch (key) {
         case "month"      -> "CYAN";
         case "dayofweek"  -> "YELLOW";
         case "day"        -> "WHITE";
         default           -> "WHITE";
      };

      if (prefs.get(key, null) == null) {
         prefs.put(key, defaultValue);
      }

      return lookupStyle(prefs.get(key, defaultValue));
   }

   /**
    * lookupStyle: Internal mapping from String to JLine constant
    */
   private static AttributedStyle lookupStyle(String color) {
      return switch (color.toUpperCase()) {
         case "BLACK"   -> AttributedStyle.DEFAULT.foreground(AttributedStyle.BLACK);
         case "RED"     -> AttributedStyle.DEFAULT.foreground(AttributedStyle.RED);
         case "GREEN"   -> AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN);
         case "YELLOW"  -> AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold();
         case "BLUE"    -> AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE);
         case "MAGENTA" -> AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA);
         case "CYAN"    -> AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN).bold();
         default        -> AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE).bold();
      };
   }

   /**
    * setColor: Manually update a preference
    */
   public static void setColor(String component, String colorName) {
      String key = component.toLowerCase();
      if (key.equals("dow")) key = "dayofweek";
      prefs.put(key, colorName.toUpperCase());
   }
}
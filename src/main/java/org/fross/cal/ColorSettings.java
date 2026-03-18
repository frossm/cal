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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.jline.utils.AttributedStyle;

/**
 * ColorSettings manages the visual theme of the calendar.
 * It uses Java Preferences to persist user choices and provides
 * JLine AttributedStyles for rendering.
 */
public class ColorSettings {
   // Path to the preference node for this user
   // Changed to protected so the Test can access it without a getter
   protected static final Preferences prefs = Preferences.userRoot().node("/org/fross/cal/colors");

   /**
    * getStyle: Returns a JLine AttributedStyle for the requested component.
    * If keys are missing, it "heals" the system by writing defaults.
    *
    * @param component The UI element (month, dayofweek, day, today)
    * @return The styled JLine object
    */
   public static AttributedStyle getStyle(String component) {
      String key = component.toLowerCase();

      // 1. SPECIAL CASE: "today" is a composite of two other keys
      if (key.equals("today")) {
         boolean changed = false;

         if (prefs.get("todayfg", null) == null) {
            prefs.put("todayfg", "WHITE");
            changed = true;
         }
         if (prefs.get("todaybg", null) == null) {
            prefs.put("todaybg", "BLUE");
            changed = true;
         }

         // Flush changes to the OS registry/plist if we healed anything
         if (changed) {
            try { prefs.flush(); } catch (BackingStoreException e) { /* Ignore */ }
         }

         return AttributedStyle.DEFAULT
               .foreground(getRawColor(prefs.get("todayfg", "WHITE")))
               .background(getRawColor(prefs.get("todaybg", "BLUE")))
               .bold();
      }

      // 2. STANDARD CASE: Single-key components
      if (prefs.get(key, null) == null) {
         String defaultValue = switch (key) {
            case "month"     -> "CYAN";
            case "dayofweek" -> "YELLOW";
            case "day"       -> "WHITE";
            case "todayfg"   -> "WHITE";
            case "todaybg"   -> "BLUE";
            default          -> "WHITE";
         };

         prefs.put(key, defaultValue);
         try { prefs.flush(); } catch (BackingStoreException e) { /* Ignore */ }
      }

      return lookupStyle(prefs.get(key, "WHITE"));
   }

   /**
    * setColor: Updates a specific component color in the preferences.
    */
   public static void setColor(String component, String colorName) {
      prefs.put(component.toLowerCase(), colorName.toUpperCase());
      try { 
      	prefs.flush();
      } catch (BackingStoreException e) {
      	 /* Ignore */
      }
   }

   /**
    * lookupStyle: Private helper to map color names to JLine constants.
    */
   private static AttributedStyle lookupStyle(String color) {
      return AttributedStyle.DEFAULT.bold().foreground(getRawColor(color));
   }

   /**
    * getRawColor: Maps a color string to the JLine integer constant.
    */
   private static int getRawColor(String color) {
      return switch (color.toUpperCase()) {
         case "BLACK"   -> AttributedStyle.BLACK;
         case "RED"     -> AttributedStyle.RED;
         case "GREEN"   -> AttributedStyle.GREEN;
         case "YELLOW"  -> AttributedStyle.YELLOW;
         case "BLUE"    -> AttributedStyle.BLUE;
         case "MAGENTA" -> AttributedStyle.MAGENTA;
         case "CYAN"    -> AttributedStyle.CYAN;
         default        -> AttributedStyle.WHITE;
      };
   }
}
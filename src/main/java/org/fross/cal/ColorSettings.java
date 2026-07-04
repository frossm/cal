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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * ColorSettings manages the visual theme of the calendar.
 * It uses Java Preferences to persist user choices and provides
 * JLine AttributedStyles for rendering.
 */
public class ColorSettings {
   // Path to the preference node for this user
   // Changed to protected so the Test can access it without a getter
   protected static final Preferences prefs = Preferences.userRoot().node("/org/fross/cal/colors");
   private static boolean colorEnabled = true;

   // Add a setter for your -z switch to call
   public static void setColorEnabled(boolean value) {
      colorEnabled = value;
   }

   /**
    * getStyle: Returns a JLine AttributedStyle for the requested component.
    * If keys are missing, it "heals" the system by writing defaults.
    *
    * @param key The UI element (month, dayofweek, day, today)
    * @return The styled JLine object
    */
   public static AttributedStyle getStyle(String key) {
      // Normalize the incoming key right away
      key = key.toLowerCase().trim();

      // 1. ABSOLUTE FIRST GATE: Check your internal colorEnabled toggle
      if (!colorEnabled) {
         return AttributedStyle.DEFAULT;
      }

      // Define 'today' defaults
      String defaultTodayFG = "232";
      String defaultTodayBG = "154";

      // 2. SPECIAL CASE: "today" is a composite of two other keys
      if (key.equals("today")) {
         boolean changed = false;

         // Read the preference values safely
         String currentFG = prefs.get("todayfg", null);
         String currentBG = prefs.get("todaybg", null);

         if (currentFG == null) {
            prefs.put("todayfg", defaultTodayFG);
            currentFG = defaultTodayFG;
            changed = true;
         }
         if (currentBG == null) {
            prefs.put("todaybg", defaultTodayBG);
            currentBG = defaultTodayBG;
            changed = true;
         }

         // Flush changes to the OS registry if we healed anything
         if (changed) {
            try {
               prefs.flush();
            } catch (BackingStoreException e) { /* Ignore */ }
         }

         // Build the style directly using our verified variables
         AttributedStyle todayStyle = AttributedStyle.DEFAULT
               .foreground(getRawColor(currentFG))
               .background(getRawColor(currentBG));

         if (org.fross.library.Output.boldOutput) {
            todayStyle = todayStyle.bold();
         }

         return todayStyle;
      }

      // 3. STANDARD CASE: Single-key components
      String colorValue = prefs.get(key, null);

      if (colorValue == null) {
         colorValue = switch (key) {
            case "todayfg"      -> defaultTodayFG;
            case "todaybg"      -> defaultTodayBG;
            case "month"        -> "73";
            case "dayofweek"    -> "229";
            case "day"          -> "231";
            case "holtitle"     -> "73";
            case "holtext"      -> "244";
            case "holhighlight" -> "63";
            default             -> "231"; // This is the defaultColor
         };

         prefs.put(key, colorValue);
         try {
            prefs.flush();
         } catch (BackingStoreException e) { /* Ignore */ }
      }

      // 4. RETURN STYLE: Pass the exact value we resolved above
      return lookupStyle(colorValue);
   }

   /**
    * setColor: Updates a specific component color in the preferences.
    */
   public static void setColor(String component, String colorName) {
      // Force BOTH the key and the value string to lowercase
      prefs.put(component.toLowerCase(), colorName.toLowerCase());
      try {
         prefs.flush();
      } catch (BackingStoreException e) { /* Ignore */ }
   }

   /**
    * lookupStyle: Private helper to map color names to JLine constants.
    */
   private static AttributedStyle lookupStyle(String color) {
      // Dynamic Bold Update: Apply .bold() only if the master gatekeeper preference is true
      AttributedStyle style = AttributedStyle.DEFAULT.foreground(getRawColor(color));

      if (org.fross.library.Output.boldOutput) {
         style = style.bold();
      }

      return style;
   }

   /**
    * getRawColor: Maps a color string to the JLine integer constant.
    */
   private static int getRawColor(String colorName) {
      // Normalize the input string
      String normalized = colorName.toUpperCase().trim();

      // 1. If it's a raw number (e.g., "208"), parse it directly
      if (normalized.matches("\\d+")) {
         try {
            int colorIndex = Integer.parseInt(normalized);
            if (colorIndex >= 0 && colorIndex <= 255) {
               return colorIndex;
            }
         } catch (NumberFormatException e) { /* Fall through to name lookup */ }
      }

      // 2. Otherwise, treat it as a named color and look it up via reflection
      try {
         if (normalized.startsWith("GREY_")) {
            normalized = normalized.replace("GREY_", "GRAY_");
         }

         Class<?> outputClass = org.fross.library.Output.class;
         java.lang.reflect.Field field = outputClass.getField(normalized);
         return field.getInt(null);

      } catch (Exception e) {
         // If it's neither a valid number nor a valid name, default to white
         return org.fross.library.Output.WHITE;
      }
   }
}
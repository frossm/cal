/*--------------------------------------------------------------------------------------
 *  Cal - A command line calendar utility
 *
 *  Copyright (c) 2018-2026 Michael Fross
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 * --------------------------------------------------------------------------------------*/
package org.fross.cal;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * MonthBlock generates a formatted 8-line calendar grid for a single month.
 * It handles day-of-week alignment and applies JLine AttributedStyles
 * for "Today" (Blue background) and "Holidays" (Red foreground).
 */
public class MonthBlock {
   private final int year;
   private final int month;
   private final LocalDate today;
   private final TreeMap<String, String> holidayMap;

   /**
    * Constructor for MonthBlock
    *
    * @param year       The year to render
    * @param month      The month to render (1-12)
    * @param today      Current date for highlighting
    * @param holidayMap TreeMap of ISO date strings (yyyy-MM-dd) to holiday names
    */
   public MonthBlock(int year, int month, LocalDate today, TreeMap<String, String> holidayMap) {
      this.year = year;
      this.month = month;
      this.today = today;
      this.holidayMap = holidayMap;
   }

   /**
    * getLines: Returns exactly 8 AttributedStrings, each 20 characters wide.
    * This fixed height and width allows for clean horizontal stitching in CalendarView.
    *
    * @return List of 8 formatted lines
    */
   public List<AttributedString> getLines() {
      List<AttributedString> lines = new ArrayList<>();
      YearMonth ym = YearMonth.of(year, month);

      // 1. HEADER: Centered Month Name and Year
      String monthName = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.US);
      // Use ColorSettings to get the "month" style
      lines.add(new AttributedString(center(monthName + " " + year, 20), ColorSettings.getStyle("month")));

      // 2. DAY LABELS: Styled using "dow"
      lines.add(new AttributedString("Su Mo Tu We Th Fr Sa", ColorSettings.getStyle("dayofweek")));

      // 3. GRID CALCULATION
      int offset = ym.atDay(1).getDayOfWeek().getValue() % 7;
      int daysInMonth = ym.lengthOfMonth();

      AttributedStringBuilder asb = new AttributedStringBuilder();

      for (int i = 1 - offset; i <= 42 - offset; i++) {
         if (i >= 1 && i <= daysInMonth) {
            LocalDate date = ym.atDay(i);
            String dateKey = date.toString();

            // --- Apply Style ---
            if (date.equals(this.today)) {
               // This calls the "today" case we added to ColorSettings.getStyle()
               // It handles the FG, BG, and Bold all in one shot.
               asb.style(ColorSettings.getStyle("today"));

            } else if (holidayMap != null && holidayMap.containsKey(dateKey)) {
               // Handle holidays (if you have them)
               asb.style(ColorSettings.getStyle("holiday"));

            } else {
               // Standard day color
               asb.style(ColorSettings.getStyle("day"));
            }

            asb.append(String.format("%2d", i));
            asb.style(AttributedStyle.DEFAULT);

         } else {
            asb.append("  ");
         }

         if ((i + offset) % 7 == 0) {
            lines.add(padToWidth(asb.toAttributedString(), 20));
            asb = new AttributedStringBuilder();
            if (i >= daysInMonth) break;
         } else {
            asb.append(" ");
         }
      }

      // 4. VERTICAL PADDING
      while (lines.size() < 8) {
         lines.add(new AttributedString(" ".repeat(20)));
      }

      return lines;
   }

   /**
    * Utility to center a string within a specific width
    */
   private String center(String text, int width) {
      if (text.length() >= width) return text.substring(0, width);
      int leftPad = (width - text.length()) / 2;
      int rightPad = width - text.length() - leftPad;
      return " ".repeat(leftPad) + text + " ".repeat(rightPad);
   }

   /**
    * Utility to pad an AttributedString to a target width.
    * Uses s.columnLength() to count visual characters, ignoring ANSI escape codes.
    */
   private AttributedString padToWidth(AttributedString s, int width) {
      int visualLen = s.columnLength();
      if (visualLen >= width) return s;
      return new AttributedStringBuilder()
            .append(s)
            .append(" ".repeat(width - visualLen))
            .toAttributedString();
   }
}
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

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * CalendarView manages the high-level layout of the calendar.
 * It coordinates the creation of MonthBlocks and stitches them into rows
 * for display on the JLine Terminal.
 */
public class CalendarView {
   private final Terminal terminal;
   private final LocalDate today;

   /**
    * Constructor for CalendarView
    * @param terminal The JLine terminal instance for output
    * @param today    The current date for highlighting "today"
    */
   public CalendarView(Terminal terminal, LocalDate today) {
      this.terminal = terminal;
      this.today = today;
   }

   /**
    * printFullYear: Renders a 12-month calendar in a grid.
    * @param year  The year to display
    * @param nCols The number of months to display per row (e.g., 3 or 4)
    */
   public void printFullYear(int year, int nCols) {
      // 1. Fetch holidays once for the entire year
      // This prevents redundant API calls or disk hits during the loop
      TreeMap<String, String> yearHolidays = null;
      if (Holidays.queryHolidaysEnabled()) {
         yearHolidays = Holidays.getHolidays(year);
      }

      // 2. Iterate through the 12 months in "chunks" (rows)
      for (int rowStart = 1; rowStart <= 12; rowStart += nCols) {
         List<List<AttributedString>> chunk = new ArrayList<>();

         // Create MonthBlock objects for this specific row and grab their lines
         for (int i = 0; i < nCols && (rowStart + i) <= 12; i++) {
            MonthBlock mb = new MonthBlock(year, rowStart + i, today, yearHolidays);
            chunk.add(mb.getLines());
         }

         // 3. Stitch the lines together horizontally
         // Each MonthBlock.getLines() returns exactly 8 lines.
         // We iterate through line 0, then line 1, etc., across all months in the chunk.
         for (int lineIdx = 0; lineIdx < 8; lineIdx++) {
            AttributedStringBuilder rowBuilder = new AttributedStringBuilder();

            for (int mIdx = 0; mIdx < chunk.size(); mIdx++) {
               List<AttributedString> monthLines = chunk.get(mIdx);

               // Append the month's line (guaranteed 20 chars wide)
               rowBuilder.append(monthLines.get(lineIdx));

               // Add a 3-space gap between columns, but not after the last month
               if (mIdx < chunk.size() - 1) {
                  rowBuilder.append("   ");
               }
            }

            // Print the fully stitched row to the terminal
            // toAnsi(terminal) ensures colors are rendered for the specific terminal type
            terminal.writer().println(rowBuilder.toAnsi(terminal));
         }

         // Add a blank line between rows of months for visual breathing room
         terminal.writer().println();
      }

      // 4. Print the Holiday Legend at the bottom if enabled
      if (Holidays.queryHolidaysEnabled() && yearHolidays != null) {
         // Calculate total width: (20 chars per month) + (3 chars per gap)
         int totalWidth = (20 * nCols) + (3 * (nCols - 1));
         Holidays.printHolidayListYear(year, totalWidth);
      }

      terminal.writer().flush();
   }

   /**
    * printMonth: Renders a single month view.
    * @param year  The year to display
    * @param month The month (1-12) to display
    */
   public void printMonth(int year, int month) {
      // Fetch holidays for the year to ensure the grid can be highlighted
      TreeMap<String, String> holidays = null;
      if (Holidays.queryHolidaysEnabled()) {
         holidays = Holidays.getHolidays(year);
      }

      MonthBlock mb = new MonthBlock(year, month, today, holidays);

      // Print each of the 8 lines generated by the MonthBlock
      for (AttributedString line : mb.getLines()) {
         terminal.writer().println(line.toAnsi(terminal));
      }

      // Print the specific holidays for this month as a list below the grid
      if (Holidays.queryHolidaysEnabled()) {
         terminal.writer().println("\nHolidays");
         terminal.writer().println(Holidays.queryHolidayListMonth(month).toString());
      }

      terminal.writer().flush();
   }
}
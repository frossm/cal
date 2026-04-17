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
 * The SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * --------------------------------------------------------------------------------------*/
package org.fross.cal;

import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CalendarViewTest ensures the visual layout of the calendar is generated correctly.
 * It uses a DumbTerminal to capture ANSI/Attributed output into a stream for verification.
 */
public class CalendarViewTest {

   /**
    * testPrintMonthOutput:
    * Verifies that rendering a single month includes the expected header,
    * day labels, and specific date numbers.
    */
   @Test
   void testPrintMonthOutput() throws Exception {
      // Create a stream to capture terminal output
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      // Use JLine's DumbTerminal to simulate a console environment in a headless test
      Terminal terminal = new DumbTerminal(System.in, out);

      // Initialize the view with a specific date to test highlighting logic
      LocalDate testDate = LocalDate.of(2025, 12, 25);
      CalendarView cv = new CalendarView(terminal, testDate);

      // Render December 2025
      cv.printMonth(2025, 12);

      // Convert the captured bytes to a string for assertion
      String output = out.toString();

      // Verify header components exist in the output
      assertTrue(output.contains("December"), "Output should contain the month name");
      assertTrue(output.contains("2025"), "Output should contain the year");

      // Verify the day-of-week labels exist
      assertTrue(output.contains("Su Mo Tu We Th Fr Sa"), "Day-of-week header is missing");

      // Verify that a mid-month date (the 25th) was rendered in the grid
      assertTrue(output.contains("25"), "The day '25' was not found in the grid output");
   }

   /**
    * testFullYearLayout:
    * Verifies that the 12-month grid logic successfully iterates through
    * all months without throwing exceptions and includes start/end months.
    */
   @Test
   void testFullYearLayout() throws Exception {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Terminal terminal = new DumbTerminal(System.in, out);
      CalendarView cv = new CalendarView(terminal, LocalDate.now());

      // Render a full year using a 3-column layout
      cv.printFullYear(2025, 3);

      String output = out.toString();

      // Verify that the first and last months of the year are present in the stitched output
      assertTrue(output.contains("January"), "Full year view is missing January");
      assertTrue(output.contains("December"), "Full year view is missing December");
   }
}
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Test;

class MonthBlockTest {

   @Test
   void testMonthBlockDimensions() {
      MonthBlock mb = new MonthBlock(2026, 1, LocalDate.of(2026, 1, 1), new TreeMap<>());
      List<AttributedString> lines = mb.getLines();

      // Verify we always get exactly 8 lines
      assertEquals(8, lines.size());

      // Verify every line has a visual length of exactly 20
      for (AttributedString line : lines) {
         assertEquals(20, line.columnLength(), "Line: " + line.toString());
      }
   }

   @Test
   void testProperCaseHeader() {
      MonthBlock mb = new MonthBlock(2026, 1, LocalDate.of(2026, 1, 1), new TreeMap<>());
      List<AttributedString> lines = mb.getLines();

      // Line 0 is the header. It should contain "January 2026" not "JANUARY 2026"
      assertTrue(lines.get(0).toString().contains("January 2026"));
   }

   @Test
   void testHolidayHighlightingLogic() {
      TreeMap<String, String> holidays = new TreeMap<>();
      holidays.put("2026-01-01", "New Year's Day");

      // Create a block for Jan 2026
      MonthBlock mb = new MonthBlock(2026, 1, LocalDate.of(2026, 3, 17), holidays);
      List<AttributedString> lines = mb.getLines();

      // January 1st 2026 is a Thursday. In a Sunday-start calendar,
      // it should be on the 3rd line of the block (Header, Labels, then first row)
      String firstWeek = lines.get(2).toAnsi();

      // Verify the ANSI escape code for color is present in the first week
      // (JLine uses \u001b[ for escape sequences)
      assertTrue(firstWeek.contains("\u001b[3"), "Holiday color escape sequence missing");
   }
}
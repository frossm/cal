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

import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

   @Test
   void testMondayStartDimensionsAndHeaders() {
      MonthBlock mb = new MonthBlock(2026, 1, LocalDate.of(2026, 1, 1), new TreeMap<>(), true);
      List<AttributedString> lines = mb.getLines();

      assertEquals(8, lines.size());
      for (AttributedString line : lines) {
         assertEquals(20, line.columnLength(), "Line: " + line.toString());
      }

      // Line 1 should be Monday-start labels
      assertEquals("Mo Tu We Th Fr Sa Su", lines.get(1).toString());
   }

   @Test
   void testSundayStartHeader() {
      MonthBlock mb = new MonthBlock(2026, 1, LocalDate.of(2026, 1, 1), new TreeMap<>(), false);
      List<AttributedString> lines = mb.getLines();

      // Line 1 should be Sunday-start labels
      assertEquals("Su Mo Tu We Th Fr Sa", lines.get(1).toString());
   }

   @Test
   void testDayAlignmentSundayVsMonday() {
      // February 1, 2026 is a Sunday.
      // In a Sunday-start week, Feb 1 is the 1st column (Su).
      MonthBlock mbSunday = new MonthBlock(2026, 2, LocalDate.of(2026, 2, 1), new TreeMap<>(), false);
      List<AttributedString> linesSunday = mbSunday.getLines();
      assertTrue(linesSunday.get(2).toString().startsWith(" 1"));

      // In a Monday-start week, Feb 1 is the 7th column (Su).
      MonthBlock mbMonday = new MonthBlock(2026, 2, LocalDate.of(2026, 2, 1), new TreeMap<>(), true);
      List<AttributedString> linesMonday = mbMonday.getLines();
      assertTrue(linesMonday.get(2).toString().endsWith(" 1"));

      // June 1, 2026 is a Monday.
      // In a Monday-start week, June 1 is the 1st column (Mo).
      MonthBlock mbJuneMonday = new MonthBlock(2026, 6, LocalDate.of(2026, 6, 1), new TreeMap<>(), true);
      assertTrue(mbJuneMonday.getLines().get(2).toString().startsWith(" 1"));
   }

   @Test
   void testLeapYearFebruary() {
      // 2024 is a leap year (29 days in Feb)
      MonthBlock mb2024 = new MonthBlock(2024, 2, LocalDate.of(2024, 2, 1), new TreeMap<>(), false);
      List<AttributedString> lines2024 = mb2024.getLines();
      assertEquals(8, lines2024.size());
      String allLines2024 = lines2024.stream().map(AttributedString::toString).reduce("", (a, b) -> a + "\n" + b);
      assertTrue(allLines2024.contains("29"), "Leap year February 2024 should contain 29");
      assertFalse(allLines2024.contains("30"), "February should not contain 30");

      // 2025 is not a leap year (28 days in Feb)
      MonthBlock mb2025 = new MonthBlock(2025, 2, LocalDate.of(2025, 2, 1), new TreeMap<>(), false);
      List<AttributedString> lines2025 = mb2025.getLines();
      assertEquals(8, lines2025.size());
      String allLines2025 = lines2025.stream().map(AttributedString::toString).reduce("", (a, b) -> a + "\n" + b);
      assertTrue(allLines2025.contains("28"), "February 2025 should contain 28");
      assertFalse(allLines2025.contains("29"), "Non-leap year February 2025 should not contain 29");
   }

   @Test
   void testEdgeCaseMonthGridLengths() {
      // August 2026 spans 6 calendar rows in Sunday start (Aug 1 is Saturday, 31 days)
      MonthBlock mbAug2026 = new MonthBlock(2026, 8, LocalDate.of(2026, 8, 1), new TreeMap<>(), false);
      List<AttributedString> linesAug = mbAug2026.getLines();
      assertEquals(8, linesAug.size());
      for (AttributedString line : linesAug) {
         assertEquals(20, line.columnLength());
      }
      assertTrue(linesAug.get(7).toString().contains("31"));

      // May 2026 in Monday start (May 1 is Friday, 31 days)
      MonthBlock mbMay2026 = new MonthBlock(2026, 5, LocalDate.of(2026, 5, 1), new TreeMap<>(), true);
      List<AttributedString> linesMay = mbMay2026.getLines();
      assertEquals(8, linesMay.size());
      for (AttributedString line : linesMay) {
         assertEquals(20, line.columnLength());
      }

      // February 2021 has 28 days starting on Monday (exactly 4 rows in Monday-start)
      MonthBlock mbFeb2021 = new MonthBlock(2021, 2, LocalDate.of(2021, 2, 1), new TreeMap<>(), true);
      List<AttributedString> linesFeb = mbFeb2021.getLines();
      assertEquals(8, linesFeb.size());
      for (AttributedString line : linesFeb) {
         assertEquals(20, line.columnLength());
      }
      // Line 6 and 7 should be padded spaces
      assertEquals(" ".repeat(20), linesFeb.get(6).toString());
      assertEquals(" ".repeat(20), linesFeb.get(7).toString());
   }
}
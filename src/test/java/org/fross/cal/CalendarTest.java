/******************************************************************************
 *  Cal - A command line calendar utility
 *
 *  Copyright (c) 2019-2024 Michael Fross
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
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 ******************************************************************************/
package org.fross.cal;

import org.fross.library.Output;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Michael Fross (michael@fross.org)
 */
class CalendarTest {
   /**
    * Test method for {@link org.fross.cal.Calendar#setCalsPerRow(int)}.
    */
   @Test
   void testSetCalsPerRow() {
      // Successful
      assertEquals(3, Calendar.calsPerRow);
      Calendar.setCalsPerRow(4);
      assertEquals(4, Calendar.calsPerRow);
      Calendar.setCalsPerRow(6);
      assertEquals(6, Calendar.calsPerRow);

      // Failures
      Calendar.setCalsPerRow(-1);
      assertEquals(6, Calendar.calsPerRow);
      Calendar.setCalsPerRow(7);
      assertEquals(6, Calendar.calsPerRow);
      Calendar.setCalsPerRow(101);
      assertEquals(6, Calendar.calsPerRow);
   }

   /**
    * Test method for {@link org.fross.cal.Calendar#getDayOfWeek(int, int, int)}.
    */
   @Test
   void testGetDayOfWeek() {
      // Test each month in 2022
      assertEquals(6, Calendar.getDayOfWeek(1, 1, 2022));
      assertEquals(2, Calendar.getDayOfWeek(2, 1, 2022));
      assertEquals(2, Calendar.getDayOfWeek(3, 1, 2022));
      assertEquals(5, Calendar.getDayOfWeek(4, 1, 2022));
      assertEquals(0, Calendar.getDayOfWeek(5, 1, 2022));
      assertEquals(3, Calendar.getDayOfWeek(6, 1, 2022));
      assertEquals(5, Calendar.getDayOfWeek(7, 1, 2022));
      assertEquals(1, Calendar.getDayOfWeek(8, 1, 2022));
      assertEquals(4, Calendar.getDayOfWeek(9, 1, 2022));
      assertEquals(6, Calendar.getDayOfWeek(10, 1, 2022));
      assertEquals(2, Calendar.getDayOfWeek(11, 1, 2022));
      assertEquals(4, Calendar.getDayOfWeek(12, 1, 2022));
   }

   /**
    * Test method for {@link org.fross.cal.Calendar#isLeapYear(int)}.
    */
   @Test
   void testIsLeapYear() {
      assertTrue(Calendar.isLeapYear(2020));
      assertFalse(Calendar.isLeapYear(2021));
      assertFalse(Calendar.isLeapYear(2022));
      assertFalse(Calendar.isLeapYear(2023));
      assertTrue(Calendar.isLeapYear(2024));
      assertFalse(Calendar.isLeapYear(2025));
      assertFalse(Calendar.isLeapYear(2026));
      assertFalse(Calendar.isLeapYear(2027));
      assertTrue(Calendar.isLeapYear(2028));
      assertFalse(Calendar.isLeapYear(2029));
   }

   /**
    * Test method for {@link org.fross.cal.Calendar#getCalHeader(int, int)}.
    */
   @Test
   void testGetCalHeader() {
      assertEquals("     August 2022", Calendar.getCalHeader(8, 2022));
      assertEquals("   September 2055", Calendar.getCalHeader(9, 2055));
      assertEquals("      May 2023", Calendar.getCalHeader(5, 2023));
      assertEquals("     December 6", Calendar.getCalHeader(12, 6));
   }

   /**
    * Test method for {@link org.fross.cal.Calendar#getCalDays(int, int)}.
    */
   @Test
   void testGetCalDays() {
      Output.enableColor(false);

      // Lets evaluate a month in the future
      String[] calDays = Calendar.getCalDays(2, 2099);
      assertEquals(" 1  2  3  4  5  6  7 ", calDays[0]);
      assertEquals(" 8  9 10 11 12 13 14 ", calDays[1]);
      assertEquals("15 16 17 18 19 20 21 ", calDays[2]);
      assertEquals("22 23 24 25 26 27 28 ", calDays[3]);
      assertEquals("", calDays[4]);
      assertEquals("                     ", calDays[5]);

      // Test output for a November 2027
      calDays = Calendar.getCalDays(11, 2027);
      assertEquals("    1  2  3  4  5  6 ", calDays[0]);
      assertEquals(" 7  8  9 10 11 12 13 ", calDays[1]);
      assertEquals("14 15 16 17 18 19 20 ", calDays[2]);
      assertEquals("21 22 23 24 25 26 27 ", calDays[3]);
      assertEquals("28 29 30             ", calDays[4]);
      assertEquals("                     ", calDays[5]);

      // And one in the past
      calDays = Calendar.getCalDays(12, 71);
      assertEquals("       1  2  3  4  5 ", calDays[0]);
      assertEquals(" 6  7  8  9 10 11 12 ", calDays[1]);
      assertEquals("13 14 15 16 17 18 19 ", calDays[2]);
      assertEquals("20 21 22 23 24 25 26 ", calDays[3]);
      assertEquals("27 28 29 30 31       ", calDays[4]);
      assertEquals("                     ", calDays[5]);
   }

}

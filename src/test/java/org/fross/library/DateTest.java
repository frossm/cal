/* ------------------------------------------------------------------------------
 * Library Project
 *
 *  Library holds methods and classes frequently used by my programs.
 *
 *  Copyright (c) 2011-2026 Michael Fross
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
 * ------------------------------------------------------------------------------*/
package org.fross.library;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Michael Fross (michael@fross.org)
 *
 */
class DateTest {

   static final String[] monthsLong = {"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
   static final String[] monthsShort = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
   static java.util.Calendar jc = java.util.Calendar.getInstance();

   /**
    * Testing getCurrrenMonth() from the Library.Date class
    */

   @Test
   void GetCurrentMonthTest() {
      assertEquals(jc.get(java.util.Calendar.MONTH) + 1, Date.getCurrentMonth());
   }

   /**
    * Testing getCurrrentDay() from the Library.Date class
    */
   @Test
   void GetCurrentDayTest() {
      assertEquals(jc.get(java.util.Calendar.DAY_OF_MONTH), Date.getCurrentDay());
   }

   /**
    * Testing getCurrrentYear() from the Library.Date class
    */
   @Test
   void GetCurrentYearTest() {
      assertEquals(jc.get(java.util.Calendar.YEAR), Date.getCurrentYear());
   }

   /**
    * Testing getCurrrentHour() from the Library.Date class
    */
   @Test
   void getCurrentHourTest() {
      assertEquals(jc.get(java.util.Calendar.HOUR_OF_DAY), Date.getCurrentHour24());
   }

   /**
    * Testing getCurrrentMinute() from the Library.Date class
    */
   @Test
   void testGetCurrentMinuteTest() {
      assertEquals(jc.get(java.util.Calendar.MINUTE), Date.getCurrentMinute());
   }

   /**
    * Testing getCurrrentSecond() from the Library.Date class
    */
   @Test
   void testGetCurrentSecondTest() {
      assertEquals(jc.get(java.util.Calendar.SECOND), Date.getCurrentSecond());
   }

   /**
    * Test method for {@link org.fross.library.Date#getCurrentMonthNameLong()}.
    */
   @Test
   void testGetCurrentMonthNameLong() {
      assertEquals(monthsLong[jc.get(java.util.Calendar.MONTH) + 1], Date.getCurrentMonthNameLong());
   }

   /**
    * Test method for {@link org.fross.library.Date#getCurrentMonthNameShort()}.
    */
   @Test
   void testGetCurrentMonthNameShort() {
      assertEquals(monthsShort[jc.get(java.util.Calendar.MONTH) + 1], Date.getCurrentMonthNameShort());
   }

   /**
    * Test method for {@link org.fross.library.Date#getMonthNameLong(int)}.
    */
   @Test
   void testGetMonthNameLong() {
      assertEquals("January", Date.getMonthNameLong(1));
      assertEquals("February", Date.getMonthNameLong(2));
      assertEquals("March", Date.getMonthNameLong(3));
      assertEquals("April", Date.getMonthNameLong(4));
      assertEquals("May", Date.getMonthNameLong(5));
      assertEquals("June", Date.getMonthNameLong(6));
      assertEquals("July", Date.getMonthNameLong(7));
      assertEquals("August", Date.getMonthNameLong(8));
      assertEquals("September", Date.getMonthNameLong(9));
      assertEquals("October", Date.getMonthNameLong(10));
      assertEquals("November", Date.getMonthNameLong(11));
      assertEquals("December", Date.getMonthNameLong(12));
   }

   /**
    * Test method for {@link org.fross.library.Date#getMonthNameShort(int)}.
    */
   @Test
   void testGetMonthNameShort() {
      assertEquals("Jan", Date.getMonthNameShort(1));
      assertEquals("Feb", Date.getMonthNameShort(2));
      assertEquals("Mar", Date.getMonthNameShort(3));
      assertEquals("Apr", Date.getMonthNameShort(4));
      assertEquals("May", Date.getMonthNameShort(5));
      assertEquals("Jun", Date.getMonthNameShort(6));
      assertEquals("Jul", Date.getMonthNameShort(7));
      assertEquals("Aug", Date.getMonthNameShort(8));
      assertEquals("Sep", Date.getMonthNameShort(9));
      assertEquals("Oct", Date.getMonthNameShort(10));
      assertEquals("Nov", Date.getMonthNameShort(11));
      assertEquals("Dec", Date.getMonthNameShort(12));
   }


}

/******************************************************************************
 *  Cal - A command line calendar utility
 *
 *  Copyright (c) 2019-2025 Michael Fross
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

import org.fross.library.Date;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;

import java.util.Arrays;
import java.util.TreeMap;

import static org.fusesource.jansi.Ansi.ansi;

public class Calendar {
   // Class Constants
   static protected final int DEFAULT_CALS_PER_ROW = 3;
   static protected final int CALENDARWIDTH = 20;
   static protected final int SPACESBETWEENCALS = 2;
   static protected final String[] MONTHLIST = {"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
         "November", "December"};
   static protected final Color TODAYHIGHLIGHT_FG = Ansi.Color.WHITE;
   static protected final Color TODAYHIGHLIGHT_BG = Ansi.Color.BLUE;

   // Class Variables
   static int calsPerRow = DEFAULT_CALS_PER_ROW;
   static TreeMap<String, String> holidayList = null;

   /**
    * setCalsPerRow(): Sets the number of calendars per row when showing an entire year
    *
    * @param cpr
    */
   public static void setCalsPerRow(int cpr) {
      // Number must be divided into 12
      if (12 % cpr == 0 && cpr > 0) {
         calsPerRow = cpr;
      } else {
         Output.printColorln(Ansi.Color.RED, "Error:  Number of calendars per row given ('" + cpr + "') must be evenly divisable into 12");
      }
   }

   /**
    * queryCalsPerRow(): Return the current number of calendars printed per row
    *
    * @return
    */
   public static int queryCalsPerRow() {
      return calsPerRow;
   }

   /**
    * getDayOfWeek(): Given the month, day, and year, return which day of the week that dates falls
    * <p>
    * Reference: https://www.tondering.dk/claus/cal/chrweek.php#calcdow & http://www.cplusplus.com/forum/general/174165/
    *
    * @param month
    * @param day
    * @param year
    * @return
    */
   public static int getDayOfWeek(int month, int day, int year) {
      int a = (14 - month) / 12;
      int y = year - a;
      int m = month + (12 * a) - 2;
      int d = (day + y + (y / 4) - (y / 100) + (y / 400) + ((31 * m) / 12)) % 7;
      return d;
   }

   /**
    * isLeapYear(): Return true if provided year is a leap year. Calculation:
    * <p>
    * Reference: https://www.wikihow.com/Calculate-Leap-Years
    *
    * @param year
    * @return
    */
   public static boolean isLeapYear(int year) {
      if ((year % 4 == 0) && (year % 100 != 0))
         return true;
      if (year % 400 == 0)
         return true;
      return false;
   }

   /**
    * printMonth(): Print the month given the provided month number and year
    *
    * @param month, year
    */
   public static void printMonth(int month, int year) {
      Output.debugPrintln(ReturnRuler());

      // If Display Holidays is enabled get the information
      if (Holidays.queryHolidaysEnabled() == true) {
         holidayList = Holidays.getHolidays(year);
      }

      // Get the holidays for the month and year provided
      String[] days = getCalDays(month, year);

      Output.printColorln(Ansi.Color.CYAN, getCalHeader(month, year));
      Output.printColorln(Ansi.Color.YELLOW, "Su Mo Tu We Th Fr Sa");

      for (int i = 0; i <= (days.length - 1); i++) {
         Output.printColorln(Ansi.Color.WHITE, days[i]);
      }

      // If display holidays is enabled, display the list after the calendar
      if (Holidays.queryHolidaysEnabled() == true) {
         Output.printColorln(Ansi.Color.YELLOW, "\nHolidays");
         StringBuilder sb = Holidays.queryHolidayListMonth(month);
         Output.printColorln(Ansi.Color.CYAN, sb.toString());
      }

   }

   /**
    * printYear(): Print the entire year
    *
    * @param year
    */
   public static void printYear(int year) {
      String[] days = new String[6];
      String[] dayrows = new String[6];
      int i, j, k;

      Output.debugPrintln(ReturnRuler());

      // If Display Holidays is enabled get the information
      if (Holidays.queryHolidaysEnabled() == true) {
         holidayList = Holidays.getHolidays(year);
      }

      // Loop through the calendar rows
      for (i = 0; i < 12; i = i + calsPerRow) {
         // Initialize the arrays
         Arrays.fill(days, "");
         Arrays.fill(dayrows, "");

         // Print Centered Month & Year
         for (j = 1; j <= calsPerRow; j++) {
            String header = getCalHeader((i + j), year);
            Output.printColor(Ansi.Color.CYAN, header + " ".repeat(CALENDARWIDTH - header.length() + 1) + " ".repeat(SPACESBETWEENCALS));
         }

         // Print The Day Labels
         String labelString = ("Su Mo Tu We Th Fr Sa " + " ".repeat(SPACESBETWEENCALS)).repeat(calsPerRow);
         Output.println("");
         Output.printColor(Ansi.Color.YELLOW, labelString);
         Output.println("");

         // Loop through each calendar in the row and build an output string
         for (j = 1; j <= calsPerRow; j++) {
            days = getCalDays(i + j, year);
            for (k = 0; k < days.length; k++) {
               dayrows[k] += days[k] + " ".repeat(SPACESBETWEENCALS);
            }
         }

         // Print out the result
         for (j = 0; j < dayrows.length; j++) {
            Output.printColorln(Ansi.Color.WHITE, dayrows[j]);
         }

         // Put a new line between calendar rows
         Output.println("");
      }

      // If display holidays is enabled, display the list after the calendar
      if (Holidays.queryHolidaysEnabled() == true) {
         int displayWidth = (CALENDARWIDTH + SPACESBETWEENCALS) * calsPerRow;
         Holidays.printHolidayListYear(year, displayWidth);
      }
   }

   /**
    * getCalHeader(): Return a string array with the month/year name correctly spaced
    *
    * @param month
    * @param year
    * @return
    */
   public static String getCalHeader(int month, int year) {
      String returnString = "";
      String strToCenter = MONTHLIST[month] + " " + year;

      // Add the correct number of spaces to center name
      int numSpaces = ((CALENDARWIDTH / 2) - (strToCenter.length() / 2));
      returnString = " ".repeat(numSpaces);

      // Add the Month & Year to the spaces
      returnString += MONTHLIST[month] + " " + year;

      return returnString;
   }

   /**
    * getCalDays(): Return a string array of calendar days for a single month used for printing
    *
    * @param month
    * @param year
    * @return
    */
   public static String[] getCalDays(int month, int year) {
      String[] returnString = new String[6];
      int[] returnStringLen = {0, 0, 0, 0, 0, 0};
      int row = 0;
      int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

      // Initialize the array
      Arrays.fill(returnString, "");

      // Determine if the year provided is a leap year
      if (month == 2 && isLeapYear(year)) {
         // It's a leap year - set February to have 29 days
         daysInMonth[2] = 29;
      }

      // Determine the which day of the week the 1st fall upon
      int firstDayOfMon = getDayOfWeek(month, 1, year);
      Output.debugPrintln("Firstday for " + month + "/" + year + ": " + firstDayOfMon);

      // Insert spaces until we get to first day of the month in the calendar
      returnString[row] += "   ".repeat(firstDayOfMon);

      // Initialize the length of the each row
      // I can't just use the length of returnString[row] because the ANSI colored characters take up more room and that won't be
      // printed. Therefore I'll keep the length of the returnString in a separate variable
      returnStringLen[row] = returnString[row].length();

      // Create the day strings. After 7 days start a new line.
      for (int day = 1; day <= daysInMonth[month]; day++) {
         // Build the colorized days
         String colorizedDay = "";

         // Colorize Today
         if (month == Date.getCurrentMonth() && year == Date.getCurrentYear() && day == Date.getCurrentDay()) {
            colorizedDay = ColorizeDay(day, TODAYHIGHLIGHT_FG, TODAYHIGHLIGHT_BG);
            returnString[row] += String.format("%s ", colorizedDay);

            // If holiday display is on, and it's not null, check to see if the current day we're processing is one
         } else if (Holidays.queryHolidaysEnabled() == true && holidayList != null
               && holidayList.get(year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day)) != null) {
            colorizedDay = ColorizeDay(day, 179);
            returnString[row] += String.format("%s ", colorizedDay);

            // No colorization needed
         } else {
            returnString[row] += String.format("%2d ", day);
         }

         // Update the length of the row's length with 3 spaces (a day's width)
         returnStringLen[row] += 3;

         // Start a new row after 7 days or if we are at the end of the month after padding
         if (((day + firstDayOfMon) % 7 == 0) || (day == daysInMonth[month])) {
            // Ensure that the array element is padded with space characters
            if (returnStringLen[row] < CALENDARWIDTH) {
               returnString[row] += " ".repeat(CALENDARWIDTH - returnStringLen[row] + 1);
            }

            // Move to the next row in the calendar
            row++;
         }
      }

      // Ensure last row / array element is CALENDARWIDTH characters. Pad with spaces.
      int lastElement = returnString.length - 1;
      if (returnString[lastElement].length() < CALENDARWIDTH) {
         returnString[lastElement] += " ".repeat(CALENDARWIDTH - returnString[lastElement].length() + 1);
      }

      return returnString;
   }

   /**
    * ColorizeDay(): Return colorized day with the provided foreground
    *
    * @param day
    * @param fg
    * @return
    */
   public static String ColorizeDay(int day, Ansi.Color fg) {
      if (Output.queryColorEnabled() == true) {
         return ansi().a(Attribute.INTENSITY_BOLD).fg(fg).a(String.format("%2d", day)).reset().toString();
      } else {
         return String.valueOf(day);
      }
   }

   /**
    * ColorizeDay(): Return colorized day with the provided foreground & background
    *
    * @param day
    * @param fg
    * @param bg
    * @return
    */
   public static String ColorizeDay(int day, Ansi.Color fg, Ansi.Color bg) {
      if (Output.queryColorEnabled() == true) {
         return ansi().a(Attribute.INTENSITY_BOLD).fg(fg).bg(bg).a(String.format("%2d", day)).reset().toString();
      } else {
         return String.valueOf(day);
      }
   }

   /**
    * ColorizeDay(): Returned a colorized day with a 256 color index number provided for the foreground
    *
    * @param day
    * @param colorIndexFG
    * @return
    */
   public static String ColorizeDay(int day, int colorIndexFG) {
      if (Output.queryColorEnabled() == true) {
         return ansi().fg(colorIndexFG).a(String.format("%2d", day)).reset().toString();
      } else {
         return String.valueOf(day);
      }
   }

   /**
    * ColorizeDay(): Returned a colorized day with a 256 color index number provided for both FG and BG
    *
    * @param day
    * @param colorIndexFG
    * @return
    */
   public static String ColorizeDay(int day, int colorIndexFG, int colorIndexBG) {
      if (Output.queryColorEnabled() == true) {
         return ansi().fg(colorIndexFG).bg(colorIndexBG).a(String.format("%2d", day)).reset().toString();
      } else {
         return String.valueOf(day);
      }
   }

   /**
    * ReturnRuler(): Simple return a string with the ruler for debugging purposes. Meant to be printed with DebugPrint()
    *
    * @return
    */
   public static String ReturnRuler() {
      return " 1         2         3         4         5         6         7         8         9         1\n"
            + "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
   }

}
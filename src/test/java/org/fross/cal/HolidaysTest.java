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

import org.fross.library.Output;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HolidaysTest: Validates the holiday data layer, ensuring accurate date
 * retrieval, correct key formatting, and proper cache management.
 */
class HolidaysTest {

   /**
    * Test holiday list for the United States in 2023.
    * This confirms the API/Cache returns the exact expected set of dates.
    */
   @Test
   void holidayListTestUS() {
      // 1. Setup: Save current locale and force US for testing
      Locale originalLocale = Locale.getDefault();
      Locale.setDefault(Locale.US);

      try {
         // 2. Safety Check: Ensure the JVM actually switched to US Locale
         // Using .getCountry() is more reliable than comparing Display Names
         if (!Locale.getDefault().getCountry().equalsIgnoreCase("US")) {
            Output.printColorln(Output.YELLOW, "System Locale is not US. Skipping holidayListTestUS.");
            return;
         }

         // 3. Logic Check: Clear the holiday cache to force a fresh data fetch
         CommandLineArgs.clearCache();
         Output.print("Executing holidayListTestUS...");

         // 4. Data Fetch: Retrieve the 2023 US holiday map
         TreeMap<String, String> holidayListUS = Holidays.getHolidays(2023);

         // 5. Validation: Basic null check and count
         assertNotNull(holidayListUS, "Holiday Map should not be null");
         assertEquals(11, holidayListUS.size(), "US 2023 should have exactly 11 standard holidays");

         // 6. Formatting Check: Ensure the first key follows the yyyy-MM-dd format
         // This is critical for MonthBlock to match dates for highlighting
         String firstKey = holidayListUS.firstKey();
         assertTrue(firstKey.matches("\\d{4}-\\d{2}-\\d{2}"),
               "Holiday keys must be yyyy-MM-dd. Found: " + firstKey);

         // 7. Data Accuracy: Verify the specific dates match the 2023 Federal Calendar
         String[] expectedDates = {
               "2023-01-02", "2023-01-16", "2023-02-20", "2023-05-29",
               "2023-06-19", "2023-07-04", "2023-09-04", "2023-10-09",
               "2023-11-10", "2023-11-23", "2023-12-25"
         };

         // assertArrayEquals checks both content and alphabetical order (TreeMap order)
         assertArrayEquals(expectedDates, holidayListUS.keySet().toArray(),
               "Holiday dates do not match the expected 2023 US sequence.");

         Output.println("...Success!");

      } finally {
         // 8. Cleanup: Always restore the user's original locale
         Locale.setDefault(originalLocale);
      }
   }

   /**
    * Test month-specific holiday queries for December and July 2025.
    * Verifies that the queryHolidayListMonth method returns correctly formatted strings.
    */
   @Test
   void monthHolidayListTestUS() {
      // Use the ISO country code check (US) to avoid display name mismatches
      if (!Locale.getDefault().getCountry().equalsIgnoreCase("US")) {
         Output.printColorln(Output.YELLOW, "System Locale is not US. Skipping monthHolidayListTestUS.");
         return;
      }

      // Check December 2025 (Christmas)
      StringBuilder sbDec = Holidays.queryHolidayListMonth(12, 2025);
      assertEquals("2025-12-25 | Christmas Day", sbDec.toString().trim());

      // Check July 2025 (Independence Day)
      StringBuilder sbJuly = Holidays.queryHolidayListMonth(7, 2025);
      assertEquals("2025-07-04 | Independence Day", sbJuly.toString().trim());
   }

   /**
    * Test the Canada (CA) holiday list for 2024.
    * Ensures the system can switch contexts and retrieve data for other regions.
    */
   @Test
   void holidayListTestCA() {
      CommandLineArgs.clearCache();

      // This now explicitly calls the version that pulls Canada
      java.util.TreeMap<String, String> holidayListCA = Holidays.getHolidays("CA", 2024);

      assertEquals(8, holidayListCA.size(), "Canada 2024 should have 8 global holidays.");
   }
}
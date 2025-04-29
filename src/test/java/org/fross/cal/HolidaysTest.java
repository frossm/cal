/******************************************************************************
 *  Cal - A command line calendar utility
 * <p>
 *  Copyright (c) 2019-2025 Michael Fross
 * <p>
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * <p>
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 * <p>
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p>
 ******************************************************************************/
package org.fross.cal;

import org.fross.library.Output;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HolidaysTest {
//	// Testing clearing the local holiday cache
//	@Test
//	void clearHolidayCacheTest() {
//		int year = org.fross.library.Date.getCurrentYear();
//
//		// Firstly, ensure we have something in the cache
//		Holidays.getHolidays(year);
//
//		// Get the 2 character country code
//		String ISO2 = Holidays.queryISO2CountryCode();
//
//		// Make sure the cached items are present
//		Preferences prefHolidayCache = Preferences.userRoot().node("/org/fross/cal/holidays/" + ISO2 + "/" + year);
//
//		String[] cacheKeys = {};
//		try {
//			// Verify that we have something in the cache
//			cacheKeys = prefHolidayCache.keys();
//			assertTrue(cacheKeys.length > 0);
//
//			// Clear the cache
//			CommandLineArgs.clearCache();
//
//			// Verify the cache is empty
//			cacheKeys = prefHolidayCache.keys();
//			assertFalse(cacheKeys.length > 0);
//
//		} catch (BackingStoreException ex) {
//			// Having issues getting to the holiday cache. Display an error and continue to getting them from the Internet
//			Output.printColorln(Ansi.Color.RED, "Unable to access the holiday cache");
//			fail();
//		}
//
//	}

   // Test holiday list - US 2023
   @Test
   void holidayListTestUS() {
      Locale.setDefault(Locale.US);
      Output.println("Current locale set to: " + Locale.getDefault().getDisplayCountry());

      // ------------------------------------------------------------------------
      // For now, skip this test if the JVM does not report it's in the US
      // ------------------------------------------------------------------------
      if (Locale.getDefault().getDisplayCountry().compareTo("United States") != 0) {
         Output.println("Current locale set to: '" + Locale.getDefault().getDisplayCountry() + "'  --  Skipping Test");
         return;
      }

      // Clear the cache before the test
      CommandLineArgs.clearCache();

      Output.print("Current locale set to: '" + Locale.getDefault().getDisplayCountry() + "'");

      // Get the holiday list for the US in 2023
      TreeMap<String, String> holidayListUS = Holidays.getHolidays(2023);

      // There should be 12 holidays in 2023
      Assertions.assertNotNull(holidayListUS);
      assertEquals(10, holidayListUS.size());

      String[] correctValuesUS = {"2023-01-02", "2023-01-16", "2023-02-20", "2023-05-29", "2023-06-19", "2023-07-04", "2023-09-04", "2023-11-10", "2023-11-23", "2023-12-25"};

      // Loop through the results and verify the keys (dates)
      int i = 0;
      for (String key : holidayListUS.keySet()) {
         assertEquals(correctValuesUS[i], key);
         i = i + 1;
      }
      Output.println("...Holiday Test Complete");
   }

   // Test US month holidays for April 2025
   @Test
   void monthHolidayListTestUS() {
      Output.println("Current locale set to: " + Locale.getDefault().getDisplayCountry());

      // ------------------------------------------------------------------------
      // For this test, skip this test if the JVM does not report it's in the US
      // ------------------------------------------------------------------------
      if (Locale.getDefault().getDisplayCountry().toString().compareTo("United States") != 0) {
         Output.println("Current locale set to: '" + Locale.getDefault().getDisplayCountry() + "'  --  Skipping Test");
         return;
      }

      StringBuilder sb = Holidays.queryHolidayListMonth(12, 2025);
      assertEquals("2025-12-25 | Christmas Day", sb.toString().trim());

      sb = Holidays.queryHolidayListMonth(7, 2025);
      assertEquals("2025-07-04 | Independence Day", sb.toString().trim());

   }

// Test holiday list - CA
//	@Test
//	void holidayListTestCA() {
//      Output.println("\nExecuting holidayListTestCA()");
//		// ------------------------------------------------------------------------
//		// Set the default country to Canada
//		// ------------------------------------------------------------------------
//		Locale.setDefault(Locale.CANADA);
//		Output.println("Current locale set to: " + Locale.getDefault().getDisplayCountry());
//
//      // Clear the cache before the test
//      CommandLineArgs.clearCache();
//
//		// Get the holiday list for the 2023 Canadian holidays
//		TreeMap<String, String> holidayListCA = Holidays.getHolidays(2024);
//
//		// There should be 8 holidays in 2024
//      Assertions.assertNotNull(holidayListCA);
//      assertEquals(8, holidayListCA.size());
//
//		String[] correctValuesCA = { "2024-01-01", "2024-03-29", "2024-05-20", "2024-07-01", "2024-09-02", "2024-09-30", "2024-10-14", "2024-12-25" };
//
//		// Loop through the results and verify the keys (dates)
//		int i = 0;
//		for (String key : holidayListCA.keySet()) {
//			assertEquals(correctValuesCA[i], key);
//			i = i + 1;
//		}
//
//		Output.println("Canada Holiday Test Complete");
//	}
}
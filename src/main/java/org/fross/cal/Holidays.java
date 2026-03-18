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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.fross.library.Output;
import org.fross.library.URLOperations;

import java.util.Locale;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Holidays {
   private static boolean holidaysAreEnabled = false;
   private static final Locale locale = Locale.getDefault();
   protected static TreeMap<String, String> countryMap = new TreeMap<>();
   protected static TreeMap<String, String> holidays = new TreeMap<>();

   /**
    * getHolidays(int year): Wrapper for the main app.
    * Uses the default system locale to determine the country.
    */
   public static TreeMap<String, String> getHolidays(int year) {
      buildCountryCodeMap();
      String countryCode = countryMap.get(locale.getISO3Country());

      // If we can't find a mapping for the current locale, default to US
      if (countryCode == null) countryCode = "US";

      return getHolidays(countryCode, year);
   }

   /**
    * getHolidays(String countryCode, int year): The primary logic.
    * This allows the test to pass "CA" while the app uses the system default.
    */
   public static TreeMap<String, String> getHolidays(String countryCode, int year) {
      String URL = "https://date.nager.at/api/v3/publicholidays/" + year + "/" + countryCode;
      String holidayRawData;

      holidays.clear();
      buildCountryCodeMap();

      // Determine if we have a holiday cache
      Preferences prefHolidayCache = Preferences.userRoot().node("/org/fross/cal/holidays/" + countryCode + "/" + year);
      String[] cacheKeys = {};
      try {
         cacheKeys = prefHolidayCache.keys();
      } catch (BackingStoreException ex) {
         Output.debugPrintln("Unable to access holiday cache: " + ex.getMessage());
      }

      // Load from Cache or Internet
      if (cacheKeys.length > 0) {
         Output.debugPrintln("Holiday cache exists - reading from cache...");
         for (String key : cacheKeys) {
            holidays.put(key, prefHolidayCache.get(key, "Error"));
         }
      } else {
         Output.debugPrintln("Holiday cache does not exist - reading from internet and building cache...");
         try {
            holidayRawData = URLOperations.ReadURL(URL);

            Gson gson = new GsonBuilder().create();
            @SuppressWarnings("unchecked")
            TreeMap<String, Object>[] gsonMap = gson.fromJson(holidayRawData, TreeMap[].class);

            for (TreeMap<String, Object> entry : gsonMap) {
               // Only include global holidays
               if (entry.get("global") != null && entry.get("global").toString().equals("true")) {
                  String date = entry.get("date").toString();
                  String name = entry.get("localName").toString();

                  holidays.put(date, name);
                  prefHolidayCache.put(date, name);
               }
            }
         } catch (Exception ex) {
            Holidays.setDisplayHolidays(false);
            Output.printColorln(Output.RED, "Unable to retrieve holidays: " + ex.getMessage());
            return null;
         }
      }
      return holidays;
   }

   /**
    * printHolidayListYear: Prints a 2-column holiday legend that matches the calendar width
    */
   public static void printHolidayListYear(int year, int displayWidth) {
      String header = year + " holidays for " + Holidays.queryCountry();
      Output.printColorln(Output.YELLOW, "\n" + center(header, displayWidth));

      Object[] keySet = holidays.keySet().toArray();
      int totalHolidays = holidays.size();
      int rowsNeeded = (totalHolidays + 1) / 2;
      int colWidth = displayWidth / 2;

      for (int i = 0; i < rowsNeeded; i++) {
         // Left Column
         String keyLeft = keySet[i].toString();
         String outLeft = keyLeft.substring(5) + "|" + holidays.get(keyLeft);
         if (outLeft.length() > colWidth - 2) outLeft = outLeft.substring(0, colWidth - 5) + "..>";
         Output.printColor(Output.CYAN, outLeft);
         Output.print(" ".repeat(Math.max(0, colWidth - outLeft.length())));

         // Right Column
         int rightIdx = i + rowsNeeded;
         if (rightIdx < totalHolidays) {
            String keyRight = keySet[rightIdx].toString();
            String outRight = keyRight.substring(5) + "|" + holidays.get(keyRight);
            if (outRight.length() > colWidth - 2) outRight = outRight.substring(0, colWidth - 5) + "..>";
            Output.printColorln(Output.CYAN, outRight);
         } else {
            Output.println("");
         }
      }
   }

   private static String center(String text, int width) {
      if (text.length() >= width) return text;
      int leftPadding = (width - text.length()) / 2;
      return " ".repeat(leftPadding) + text;
   }

   public static StringBuilder queryHolidayListMonth(int month, int year) {
      holidays.clear();
      holidays = Holidays.getHolidays(year);
      return queryHolidayListMonth(month);
   }

   public static StringBuilder queryHolidayListMonth(int month) {
      StringBuilder sb = new StringBuilder();
      for (String key : holidays.keySet()) {
         if (key.split("-")[1].compareTo(String.format("%02d", month)) == 0) {
            sb.append(key).append(" | ").append(holidays.get(key)).append("\n");
         }
      }
      return sb;
   }

   public static String queryCountry() {
      return locale.getDisplayCountry();
   }

   public static void setDisplayHolidays(boolean state) {
      holidaysAreEnabled = state;
   }

   public static boolean queryHolidaysEnabled() {
      return holidaysAreEnabled;
   }

   public static String queryISO2CountryCode() {
      buildCountryCodeMap();
      return countryMap.get(locale.getISO3Country());
   }

   private static void buildCountryCodeMap() {
      if (!countryMap.isEmpty()) return;

      String[] ISO3 = {"ABW", "AFG", "AGO", "AIA", "ALA", "ALB", "AND", "ARE", "ARG", "ARM", "ASM", "ATA", "ATF", "ATG", "AUS", "AUT", "AZE", "BDI", "BEL", "BEN", "BES", "BFA", "BGD", "BGR", "BHR", "BHS", "BIH", "BLM", "BLR", "BLZ", "BMU", "BOL", "BRA", "BRB", "BRN", "BTN", "BVT", "BWA", "CAF", "CAN", "CCK", "CHE", "CHL", "CHN", "CIV", "CMR", "COD", "COG", "COK", "COL", "COM", "CPV", "CRI", "CUB", "CUW", "CXR", "CYM", "CYP", "CZE", "DEU", "DJI", "DMA", "DNK", "DOM", "DZA", "ECU", "EGY", "ERI", "ESH", "ESP", "EST", "ETH", "FIN", "FJI", "FLK", "FRA", "FRO", "FSM", "GAB", "GBR", "GEO", "GGY", "GHA", "GIB", "GIN", "GLP", "GMB", "GNB", "GNQ", "GRC", "GRD", "GRL", "GTM", "GUF", "GUM", "GUY", "HKG", "HMD", "HND", "HRV", "HTI", "HUN", "IDN", "IMN", "IND", "IOT", "IRL", "IRN", "IRQ", "ISL", "ISR", "ITA", "JAM", "JEY", "JOR", "JPN", "KAZ", "KEN", "KGZ", "KHM", "KIR", "KNA", "KOR", "KWT", "LAO", "LBN", "LBR", "LBY", "LCA", "LIE", "LKA", "LSO", "LTU", "LUX", "LVA", "MAC", "MAF", "MAR", "MCO", "MDA", "MDG", "MDV", "MEX", "MHL", "MKD", "MLI", "MLT", "MMR", "MNE", "MNG", "MNP", "MOZ", "MRT", "MSR", "MTQ", "MUS", "MWI", "MYS", "MYT", "NAM", "NCL", "NER", "NFK", "NGA", "NIC", "NIU", "NLD", "NOR", "NPL", "NRU", "NZL", "OMN", "PAK", "PAN", "PCN", "PER", "PHL", "PLW", "PNG", "POL", "PRI", "PRK", "PRT", "PRY", "PSE", "PYF", "QAT", "REU", "ROU", "RUS", "RWA", "SAU", "SDN", "SEN", "SGP", "SGS", "SHN", "SJM", "SLB", "SLE", "SLV", "SMR", "SOM", "SPM", "SRB", "SSD", "STP", "SUR", "SVK", "SVN", "SWE", "SWZ", "SXM", "SYC", "SYR", "TCA", "TCD", "TGO", "THA", "TJK", "TKL", "TKM", "TLS", "TON", "TTO", "TUN", "TUR", "TUV", "TWN", "TZA", "UGA", "UKR", "UMI", "URY", "USA", "UZB", "VAT", "VCT", "VEN", "VGB", "VIR", "VNM", "VUT", "WLF", "WSM", "YEM", "ZAF", "ZMB", "ZWE"};
      String[] ISO2 = {"AW", "AF", "AO", "AI", "AX", "AL", "AD", "AE", "AR", "AM", "AS", "AQ", "TF", "AG", "AU", "AT", "AZ", "BI", "BE", "BJ", "BQ", "BF", "BD", "BG", "BH", "BS", "BA", "BL", "BY", "BZ", "BM", "BO", "BR", "BB", "BN", "BT", "BV", "BW", "CF", "CA", "CC", "CH", "CL", "CN", "CI", "CM", "CD", "CG", "CK", "CO", "KM", "CV", "CR", "CU", "CW", "CX", "KY", "CY", "CZ", "DE", "DJ", "DM", "DK", "DO", "DZ", "EC", "EG", "ER", "EH", "ES", "EE", "ET", "FI", "FJ", "FK", "FR", "FO", "FM", "GA", "GB", "GE", "GG", "GH", "GI", "GN", "GP", "GM", "GW", "GQ", "GR", "GD", "GL", "GT", "GF", "GU", "GY", "HK", "HM", "HN", "HR", "HT", "HU", "ID", "IM", "IN", "IO", "IE", "IR", "IQ", "IS", "IL", "IT", "JM", "JE", "JO", "JP", "KZ", "KE", "KG", "KH", "KI", "KN", "KR", "KW", "LA", "LB", "LR", "LY", "LC", "LI", "LK", "LS", "LT", "LU", "LV", "MO", "MF", "MA", "MC", "MD", "MG", "MV", "MX", "MH", "MK", "ML", "MT", "MM", "ME", "MN", "MP", "MZ", "MR", "MS", "MQ", "MU", "MW", "MY", "YT", "NA", "NC", "NE", "NF", "NG", "NI", "NU", "NL", "NO", "NP", "NR", "NZ", "OM", "PK", "PA", "PN", "PE", "PH", "PW", "PG", "PL", "PR", "KP", "PT", "PY", "PS", "PF", "QA", "RE", "RO", "RU", "RW", "SA", "SD", "SN", "SG", "GS", "SH", "SJ", "SB", "SL", "SV", "SM", "SO", "PM", "RS", "SS", "ST", "SR", "SK", "SI", "SE", "SZ", "SX", "SC", "SY", "TC", "TD", "TG", "TH", "TJ", "TK", "TM", "TL", "TO", "TT", "TN", "TR", "TV", "TW", "TZ", "UG", "UA", "UM", "UY", "US", "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE", "ZA", "ZM", "ZW"};
      for (int i = 0; i < ISO3.length; i++) {
         countryMap.put(ISO3[i], ISO2[i]);
      }
   }
}
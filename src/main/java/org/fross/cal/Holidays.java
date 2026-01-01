/******************************************************************************
 *  Cal - A command line calendar utility
 *
 *  Copyright (c) 2019-2026 Michael Fross
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.fross.library.Format;
import org.fross.library.Output;
import org.fross.library.URLOperations;
import org.fusesource.jansi.Ansi;

import java.util.Locale;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Holidays {
   private static boolean holidaysAreEnabled = false;
   private static Locale locale = Locale.getDefault();
   protected static TreeMap<String, String> countryMap = new TreeMap<>();
   protected static TreeMap<String, String> holidays = new TreeMap<>();

   /**
    * getHolidays(): Return a TreeMap<String,String> containing the 'date' and 'localName'
    * <p>
    * Date is in the format 'yyyy-mm-dd'
    * <p>
    * Can test via command line with "java -Duser.country=MX -jar target\cal.jar -d" or adding -Duser.country=MX to the
    * debug/run configuration
    *
    * @param year
    * @return
    */
   public static TreeMap<String, String> getHolidays(int year) {
      String URL = "https://date.nager.at/api/v3/publicholidays/";
      String holidayRawData = "";

      // Build the ISO3 to ISO2 Country Map
      buildCountryCodeMap();

      try {
         // Debug
         Output.debugPrintln("Locale Information: ");
         Output.debugPrintln("  - ISO3 Code: " + locale.getISO3Country());
         Output.debugPrintln("  - ISO2 Code: " + queryISO2CountryCode());
         Output.debugPrintln("  - Name: " + locale.getDisplayCountry());

         // Build the URL and fetch the data
         URL = URL + year + "/" + countryMap.get(locale.getISO3Country());

      } catch (Exception Ex) {
         // Couldn't retrieve the holidays - turn off holiday display
         Holidays.setDisplayHolidays(false);
         Output.printColorln(Ansi.Color.RED, "It doesn't look like the following country's holidays are supported: '" + locale.getDisplayCountry() + "'\n");
         return null;
      }

      Output.debugPrintln("URL to use: " + URL);

      // Determine if we have a holiday cache in the preferences system for the year and country
      Preferences prefHolidayCache = Preferences.userRoot().node("/org/fross/cal/holidays/" + countryMap.get(locale.getISO3Country()) + "/" + year);

      // Loop through the cached dates (keys), if they exist and build the holidays map
      String[] cacheKeys = {};
      try {
         cacheKeys = prefHolidayCache.keys();
      } catch (BackingStoreException ex) {
         // Having issues getting to the holiday cache. Display an error and continue to getting them from the Internet
         Output.printColorln(Ansi.Color.RED, "Unable to access the holiday cache - getting holidays from internet" + "\n");
      }

      // If we have cached holidays, process them into the holidays map
      if (cacheKeys.length > 0) {
         Output.debugPrintln("Holiday cache exists - reading from cache...");
         for (int i = 0; i < cacheKeys.length; i++) {
            holidays.put(cacheKeys[i], prefHolidayCache.get(cacheKeys[i], "Error"));
         }

      } else {
         // We don't have a cache, so get holidays from the Internet (and build the cache)
         // Pull the JSON holidays from the website
         Output.debugPrintln("Holiday cache does not exist - reading from internet and building cache...");
         try {
            holidayRawData = URLOperations.ReadURL(URL);

         } catch (Exception ex) {
            // Couldn't retrieve the holidays - turn off holiday display
            Holidays.setDisplayHolidays(false);
            Output.printColorln(Ansi.Color.RED, "Unable to retrieve holidays for the year '" + year + "' in " + locale.getDisplayCountry() + "\n");
            return null;
         }

         try {
            // Convert the JSON into a TreeMap
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            // In Gson, convert the JSON into a map
            @SuppressWarnings("unchecked")
            TreeMap<String, Object>[] gsonMap = gson.fromJson(holidayRawData, TreeMap[].class);

            // Loop through the <String,Object> map and convert it to a <String, String> TreeMap
            // Note, only use holidays that are listed as "global: true" as others are not national holidays
            for (int holidayEntry = 0; holidayEntry < gsonMap.length; holidayEntry++) {
               if (gsonMap[holidayEntry].get("global").toString().equals("true")) {
                  holidays.put(gsonMap[holidayEntry].get("date").toString(), gsonMap[holidayEntry].get("localName").toString());

                  // Save the holiday information into the preferences cache
                  prefHolidayCache.put(gsonMap[holidayEntry].get("date").toString(), gsonMap[holidayEntry].get("localName").toString());
               } else {
                  Output.debugPrintln("Skipping non-global holiday: " + gsonMap[holidayEntry].get("localName"));
               }
            }

         } catch (Exception ex) {
            // Couldn't retrieve the holidays - turn off holiday display
            Holidays.setDisplayHolidays(false);
            Output.printColorln(Ansi.Color.RED, "Unable to process the " + year + " holidays for " + locale.getDisplayCountry() + "\n");
            return null;
         }
      }

      return holidays;

   }

   /**
    * printHolidayListYear(): Print a 2 column list of holidays for the given year
    *
    * @param year
    * @param displayWidth
    */
   public static void printHolidayListYear(int year, int displayWidth) {
      Output.debugPrintln("Width of calendar display for centering: " + displayWidth);

      // Display the holiday display header
      String header = year + " holidays for " + Holidays.queryCountry();
      try {
         Output.printColorln(Ansi.Color.YELLOW, Format.CenterText(displayWidth, header));
      } catch (Exception ex) {
         Output.printColorln(Ansi.Color.YELLOW, header);
      }

      // List the holidays
      Object[] keySet = holidays.keySet().toArray();
      String keyLeft = "";
      String keyRight = "";
      for (int l = 0; l < ((holidays.size() + 1) / 2); l++) {
         try {
            keyLeft = keySet[l].toString();

            // Process odd numbers and even numbers differently
            if ((holidays.size() % 2) == 0) {
               keyRight = keySet[keySet.length / 2 + l].toString();
            } else {
               keyRight = keySet[(keySet.length + 1) / 2 + l].toString();
            }

            // Build output data removing the year as it's redundant
            String outputLeft = keyLeft.substring(5) + "|" + holidays.get(keyLeft);
            String outputRight = keyRight.substring(5) + "|" + holidays.get(keyRight);

            // Shorten the holiday name if it's longer than 1/2 the display width
            if (outputLeft.length() > (displayWidth / 2 - 1)) {
               outputLeft = outputLeft.substring(0, displayWidth / 2 - 5);
               outputLeft = outputLeft + "..>";
            }

            // Display the left item and the spacer
            Output.printColor(Ansi.Color.CYAN, outputLeft);
            Output.print(" ".repeat((displayWidth / 2) - outputLeft.length()));

            // Display the Right column item
            Output.printColorln(Ansi.Color.CYAN, outputRight);

         } catch (ArrayIndexOutOfBoundsException ex) {
            // Display the left side and nothing on the right for odd number of holidays
            Output.printColor(Ansi.Color.CYAN, keyLeft.substring(5) + "|" + holidays.get(keyLeft));

         } catch (IllegalArgumentException ex) {
            Output.printColorln(Ansi.Color.RED, "ERROR: Could not display holiday list correctly");
         }
      }
   }

   /**
    * queryHolidayListMonth(): If a year is provided, pull those holidays before returning the monthly list. Mostly used for
    * testing I would assume
    *
    * @param month
    * @param year
    * @return
    */
   public static StringBuilder queryHolidayListMonth(int month, int year) {
      // Empty the current holiday list
      holidays.clear();

      // Pull the holidays for the year provided
      holidays = Holidays.getHolidays(year);

      // Return the holidays for that month and year
      return queryHolidayListMonth(month);
   }

   /**
    * queryHolidayListMonth(): Return the holidays for the month provided
    *
    * @param month
    */
   public static StringBuilder queryHolidayListMonth(int month) {
      StringBuilder sb = new StringBuilder();

      // Loop through the holidays for the current year, printing those in the given month
      for (String key : holidays.keySet()) {
         if (key.split("-")[1].compareTo(String.format("%02d", month)) == 0) {
            sb.append(key + " | " + holidays.get(key));
            sb.append("\n");
         }
      }
      return sb;
   }

   /**
    * queryCountry(): Return the current locale country name as defined by the JVM
    *
    * @return
    */
   public static String queryCountry() {
      return locale.getDisplayCountry();
   }

   /**
    * setDisplayHolidays(): Sets the display holiday flag to true or false
    *
    * @param state
    */
   public static void setDisplayHolidays(boolean state) {
      holidaysAreEnabled = state;
   }

   /**
    * queryHolidaysEnabled(): Return true/false if display holidays are enabled
    *
    * @return
    */
   public static boolean queryHolidaysEnabled() {
      return holidaysAreEnabled;
   }

   /**
    * queryISO2CountryCode(): Return the 2 character local country code
    *
    * @return
    */
   public static String queryISO2CountryCode() {
      return countryMap.get(locale.getISO3Country());
   }

   /**
    * buildCountryCodedMap(): Build a simple map with the ISO3 county code mapped to it's ISO2 value
    */
   private static void buildCountryCodeMap() {
      // Obtained from: https://www.iban.com/country-codes
      String[] ISO3 = {"ABW", "AFG", "AGO", "AIA", "ALA", "ALB", "AND", "ARE", "ARG", "ARM", "ASM", "ATA", "ATF", "ATG", "AUS", "AUT", "AZE", "BDI", "BEL",
            "BEN", "BES", "BFA", "BGD", "BGR", "BHR", "BHS", "BIH", "BLM", "BLR", "BLZ", "BMU", "BOL", "BRA", "BRB", "BRN", "BTN", "BVT", "BWA", "CAF",
            "CAN", "CCK", "CHE", "CHL", "CHN", "CIV", "CMR", "COD", "COG", "COK", "COL", "COM", "CPV", "CRI", "CUB", "CUW", "CXR", "CYM", "CYP", "CZE",
            "DEU", "DJI", "DMA", "DNK", "DOM", "DZA", "ECU", "EGY", "ERI", "ESH", "ESP", "EST", "ETH", "FIN", "FJI", "FLK", "FRA", "FRO", "FSM", "GAB",
            "GBR", "GEO", "GGY", "GHA", "GIB", "GIN", "GLP", "GMB", "GNB", "GNQ", "GRC", "GRD", "GRL", "GTM", "GUF", "GUM", "GUY", "HKG", "HMD", "HND",
            "HRV", "HTI", "HUN", "IDN", "IMN", "IND", "IOT", "IRL", "IRN", "IRQ", "ISL", "ISR", "ITA", "JAM", "JEY", "JOR", "JPN", "KAZ", "KEN", "KGZ",
            "KHM", "KIR", "KNA", "KOR", "KWT", "LAO", "LBN", "LBR", "LBY", "LCA", "LIE", "LKA", "LSO", "LTU", "LUX", "LVA", "MAC", "MAF", "MAR", "MCO",
            "MDA", "MDG", "MDV", "MEX", "MHL", "MKD", "MLI", "MLT", "MMR", "MNE", "MNG", "MNP", "MOZ", "MRT", "MSR", "MTQ", "MUS", "MWI", "MYS", "MYT",
            "NAM", "NCL", "NER", "NFK", "NGA", "NIC", "NIU", "NLD", "NOR", "NPL", "NRU", "NZL", "OMN", "PAK", "PAN", "PCN", "PER", "PHL", "PLW", "PNG",
            "POL", "PRI", "PRK", "PRT", "PRY", "PSE", "PYF", "QAT", "REU", "ROU", "RUS", "RWA", "SAU", "SDN", "SEN", "SGP", "SGS", "SHN", "SJM", "SLB",
            "SLE", "SLV", "SMR", "SOM", "SPM", "SRB", "SSD", "STP", "SUR", "SVK", "SVN", "SWE", "SWZ", "SXM", "SYC", "SYR", "TCA", "TCD", "TGO", "THA",
            "TJK", "TKL", "TKM", "TLS", "TON", "TTO", "TUN", "TUR", "TUV", "TWN", "TZA", "UGA", "UKR", "UMI", "URY", "USA", "UZB", "VAT", "VCT", "VEN",
            "VGB", "VIR", "VNM", "VUT", "WLF", "WSM", "YEM", "ZAF", "ZMB", "ZWE"};

      String[] ISO2 = {"AW", "AF", "AO", "AI", "AX", "AL", "AD", "AE", "AR", "AM", "AS", "AQ", "TF", "AG", "AU", "AT", "AZ", "BI", "BE", "BJ", "BQ", "BF",
            "BD", "BG", "BH", "BS", "BA", "BL", "BY", "BZ", "BM", "BO", "BR", "BB", "BN", "BT", "BV", "BW", "CF", "CA", "CC", "CH", "CL", "CN", "CI", "CM",
            "CD", "CG", "CK", "CO", "KM", "CV", "CR", "CU", "CW", "CX", "KY", "CY", "CZ", "DE", "DJ", "DM", "DK", "DO", "DZ", "EC", "EG", "ER", "EH", "ES",
            "EE", "ET", "FI", "FJ", "FK", "FR", "FO", "FM", "GA", "GB", "GE", "GG", "GH", "GI", "GN", "GP", "GM", "GW", "GQ", "GR", "GD", "GL", "GT", "GF",
            "GU", "GY", "HK", "HM", "HN", "HR", "HT", "HU", "ID", "IM", "IN", "IO", "IE", "IR", "IQ", "IS", "IL", "IT", "JM", "JE", "JO", "JP", "KZ", "KE",
            "KG", "KH", "KI", "KN", "KR", "KW", "LA", "LB", "LR", "LY", "LC", "LI", "LK", "LS", "LT", "LU", "LV", "MO", "MF", "MA", "MC", "MD", "MG", "MV",
            "MX", "MH", "MK", "ML", "MT", "MM", "ME", "MN", "MP", "MZ", "MR", "MS", "MQ", "MU", "MW", "MY", "YT", "NA", "NC", "NE", "NF", "NG", "NI", "NU",
            "NL", "NO", "NP", "NR", "NZ", "OM", "PK", "PA", "PN", "PE", "PH", "PW", "PG", "PL", "PR", "KP", "PT", "PY", "PS", "PF", "QA", "RE", "RO", "RU",
            "RW", "SA", "SD", "SN", "SG", "GS", "SH", "SJ", "SB", "SL", "SV", "SM", "SO", "PM", "RS", "SS", "ST", "SR", "SK", "SI", "SE", "SZ", "SX", "SC",
            "SY", "TC", "TD", "TG", "TH", "TJ", "TK", "TM", "TL", "TO", "TT", "TN", "TR", "TV", "TW", "TZ", "UG", "UA", "UM", "UY", "US", "UZ", "VA", "VC",
            "VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE", "ZA", "ZM", "ZW"};

      // Loop through and build the ISO3 to ISO2 hashmap
      for (int i = 0; i < ISO3.length; i++) {
         countryMap.put(ISO3[i], ISO2[i]);
      }
   }
}

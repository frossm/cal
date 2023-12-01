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

import java.util.Locale;
import java.util.TreeMap;

import org.fross.library.Output;
import org.fross.library.URLOperations;
import org.fusesource.jansi.Ansi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Holidays {
	private static boolean holidaysAreEnabled = false;
	private static Locale locale = Locale.getDefault();
	private static TreeMap<String, String> countryMap = new TreeMap<>();
	protected static TreeMap<String, String> holidays = new TreeMap<>();

	/**
	 * getHolidays(): Return a TreeMap<String,String> containing the 'date' and 'localName'
	 * 
	 * Date is in the format 'yyyy-mm-dd'
	 * 
	 * Can test via command line with "java -Duser.country=MX -jar target\cal.jar -d" or adding -Duser.country=MX to the
	 * debug/run configuration
	 * 
	 * @param year
	 * @return
	 */
	public static TreeMap<String, String> getHolidays(int year) {
		String URL = "https://date.nager.at/api/v3/publicholidays/";

		// Build the ISO3 to ISO2 Country Map
		buildCountryCodeMap();

		try {
			// Debug
			Output.debugPrintln("Locale Information: ");
			Output.debugPrintln("  - ISO3 Code: " + locale.getISO3Country());
			Output.debugPrintln("  - ISO2 Code: " + countryMap.get(locale.getISO3Country()));
			Output.debugPrintln("  - Name: " + locale.getDisplayCountry());

			// Build the URL and fetch the data
			// https://date.nager.at/api/v3/publicholidays/2023/US
			URL = URL + year + "/" + countryMap.get(locale.getISO3Country());
		
		} catch (Exception Ex) {
			// Couldn't retrieve the holidays - turn off holiday display
			Holidays.setDisplayHolidays(false);
			Output.printColorln(Ansi.Color.RED, "It doesn't look like the following country is supported: '" + locale.getDisplayCountry() + "'\n");
			return null;
		}

		Output.debugPrintln("URL to use: " + URL);

		// Pull the JSON holidays from the website
		String holidayRawData = "";
		try {
			holidayRawData = URLOperations.ReadURL(URL);

		} catch (Exception ex) {
			// Couldn't retrieve the holidays - turn off holiday display
			Holidays.setDisplayHolidays(false);
			Output.printColorln(Ansi.Color.RED, "Unable to retrieve holidays for " + locale.getDisplayCountry() + "\n");
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
			for (int holidayEntry = 0; holidayEntry < gsonMap.length; holidayEntry++) {
				holidays.put(gsonMap[holidayEntry].get("date").toString(), gsonMap[holidayEntry].get("localName").toString());
			}
			
		} catch (Exception ex) {
			// Couldn't retrieve the holidays - turn off holiday display
			Holidays.setDisplayHolidays(false);
			Output.printColorln(Ansi.Color.RED, "Unable to process holidays for " + locale.getDisplayCountry() + "\n");
			return null;
		}

		return holidays;

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

	public static int queryLengthOfLongestHolidayName() {
		int longestHolidayLength = 0;
		try {
			for (String key : holidays.keySet()) {
				if (holidays.get(key).length() > longestHolidayLength) {
					longestHolidayLength = holidays.get(key).length();
				}
			}
		} catch (Exception ex) {

		}

		return longestHolidayLength;
	}

	/**
	 * buildCountryCodedMap(): Build a simple map with the ISO3 county code mapped to it's ISO2 value
	 * 
	 */
	private static void buildCountryCodeMap() {
		// Obtained from: https://www.iban.com/country-codes
		String[] ISO3 = { "ABW", "AFG", "AGO", "AIA", "ALA", "ALB", "AND", "ARE", "ARG", "ARM", "ASM", "ATA", "ATF", "ATG", "AUS", "AUT", "AZE", "BDI", "BEL",
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
				"VGB", "VIR", "VNM", "VUT", "WLF", "WSM", "YEM", "ZAF", "ZMB", "ZWE" };

		String[] ISO2 = { "AW", "AF", "AO", "AI", "AX", "AL", "AD", "AE", "AR", "AM", "AS", "AQ", "TF", "AG", "AU", "AT", "AZ", "BI", "BE", "BJ", "BQ", "BF",
				"BD", "BG", "BH", "BS", "BA", "BL", "BY", "BZ", "BM", "BO", "BR", "BB", "BN", "BT", "BV", "BW", "CF", "CA", "CC", "CH", "CL", "CN", "CI", "CM",
				"CD", "CG", "CK", "CO", "KM", "CV", "CR", "CU", "CW", "CX", "KY", "CY", "CZ", "DE", "DJ", "DM", "DK", "DO", "DZ", "EC", "EG", "ER", "EH", "ES",
				"EE", "ET", "FI", "FJ", "FK", "FR", "FO", "FM", "GA", "GB", "GE", "GG", "GH", "GI", "GN", "GP", "GM", "GW", "GQ", "GR", "GD", "GL", "GT", "GF",
				"GU", "GY", "HK", "HM", "HN", "HR", "HT", "HU", "ID", "IM", "IN", "IO", "IE", "IR", "IQ", "IS", "IL", "IT", "JM", "JE", "JO", "JP", "KZ", "KE",
				"KG", "KH", "KI", "KN", "KR", "KW", "LA", "LB", "LR", "LY", "LC", "LI", "LK", "LS", "LT", "LU", "LV", "MO", "MF", "MA", "MC", "MD", "MG", "MV",
				"MX", "MH", "MK", "ML", "MT", "MM", "ME", "MN", "MP", "MZ", "MR", "MS", "MQ", "MU", "MW", "MY", "YT", "NA", "NC", "NE", "NF", "NG", "NI", "NU",
				"NL", "NO", "NP", "NR", "NZ", "OM", "PK", "PA", "PN", "PE", "PH", "PW", "PG", "PL", "PR", "KP", "PT", "PY", "PS", "PF", "QA", "RE", "RO", "RU",
				"RW", "SA", "SD", "SN", "SG", "GS", "SH", "SJ", "SB", "SL", "SV", "SM", "SO", "PM", "RS", "SS", "ST", "SR", "SK", "SI", "SE", "SZ", "SX", "SC",
				"SY", "TC", "TD", "TG", "TH", "TJ", "TK", "TM", "TL", "TO", "TT", "TN", "TR", "TV", "TW", "TZ", "UG", "UA", "UM", "UY", "US", "UZ", "VA", "VC",
				"VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE", "ZA", "ZM", "ZW" };

		// Loop through and build the ISO3 to ISO2 hashmap
		for (int i = 0; i < ISO3.length; i++) {
			countryMap.put(ISO3[i], ISO2[i]);
		}
	}
}

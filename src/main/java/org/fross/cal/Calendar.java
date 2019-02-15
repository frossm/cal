/******************************************************************************
 * cal.java
 * 
 * A simple console based calculator generator
 * 
 *  Written by Michael Fross.  Copyright 2011-2019.  All rights reserved.
 *  
 *  License: GNU General Public License v3.
 *           http://www.gnu.org/licenses/gpl-3.0.html
 *           
 ******************************************************************************/
package org.fross.cal;

import java.util.Arrays;

import com.diogonunes.jcdp.color.api.Ansi.FColor;

public class Calendar {
	// Class Constants
	static final int CALENDARWIDTH = 20;
	static final int SPACESBETWEENCALS = 3;
	static final String[] MONTHLIST = { "none", "January", "February", "March", "April", "May", "June", "July",
			"August", "September", "October", "November", "December" };

	// Class Variables
	static int calsPerRow = 3;

	/**
	 * setCalsPerRow(): Sets the number of calendars per row when showing an entire
	 * year
	 * 
	 * @param cpr
	 */
	public static void setCalsPerRow(int cpr) {
		// Number must be divided into 12
		if (12 % cpr == 0) {
			calsPerRow = cpr;
		} else {
			Output.printcolorln(FColor.RED, "Error.  Number of calendars per row must evenly divisable into 12\n");
		}
	}

	/**
	 * firstDay(): Given the month, day, and year, return which day of the week it
	 * falls
	 * 
	 * Reference: https://www.tondering.dk/claus/cal/chrweek.php#calcdow
	 * http://www.cplusplus.com/forum/general/174165/
	 * 
	 * @param month
	 * @param day
	 * @param year
	 * @return
	 */
	public static int firstDay(int month, int day, int year) {
		int a = (14 - month) / 12;
		int y = year - a;
		int m = month + (12 * a) - 2;
		int d = (day + y + (y / 4) - (y / 100) + (y / 400) + ((31 * m) / 12)) % 7;
		return d;
	}

	/**
	 * isLeapYear(): Return true if provided year is a leap year. Calculation:
	 * 
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
	 * @param args
	 */
	public static void printMonth(int month, int year) {
		String[] days = getCalDays(month, year);

		Output.printcolorln(FColor.CYAN, getCalHeader(month, year));
		Output.printcolorln(FColor.YELLOW, "Su Mo Tu We Th Fr Sa");

		for (int i = 0; i <= (days.length - 1); i++) {
			Output.printcolorln(FColor.WHITE, days[i]);
		}
	}

	/**
	 * printYear(): Print the entire year
	 * 
	 * @param month
	 * @param year
	 */
	public static void printYear(int month, int year) {
		String[] days = new String[6];
		String[] dayrows = new String[6];
		int i, j, k;

		// Loop through the calendar rows
		for (i = 0; i < 12; i = i + calsPerRow) {
			// Initialize the arrays
			Arrays.fill(days, "");
			Arrays.fill(dayrows, "");

			// Print Centered Month & Year
			for (j = 1; j <= calsPerRow; j++) {
				String header = getCalHeader((i + j), year);
				Output.printcolor(FColor.CYAN,
						header + " ".repeat(CALENDARWIDTH - header.length() + 1) + " ".repeat(SPACESBETWEENCALS));
			}
			Output.println("");

			// Print The Day Labels
			String labelString = ("Su Mo Tu We Th Fr Sa " + " ".repeat(SPACESBETWEENCALS)).repeat(calsPerRow);
			Output.printcolor(FColor.YELLOW, labelString);
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
				Output.printcolorln(FColor.WHITE, dayrows[j]);
			}

			// Put a new line between calendar rows
			Output.println("");
		}
	}

	/**
	 * getCalHeader(): Return a string array with the month/year name correctly
	 * spaced
	 * 
	 * @param month
	 * @param year
	 * @return
	 */
	public static String getCalHeader(int month, int year) {
		String returnString = "";
		String strToCenter = MONTHLIST[month] + " " + year;

		int numSpaces = ((CALENDARWIDTH / 2) - (strToCenter.length() / 2));
		for (int i = 0; i < numSpaces; i++) {
			returnString += " ";
		}
		returnString += strToCenter;

		return returnString;
	}

	/**
	 * getCalDays(): Return a string array of calendar days for printing
	 * 
	 * @param month
	 * @param year
	 * @return
	 */
	public static String[] getCalDays(int month, int year) {
		String[] returnString = new String[6];
		int counter = 0;
		int[] dayArray = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

		// Initialize the array
		Arrays.fill(returnString, "");

		// Lets see if the year provided is a leap year
		if (month == 2 && isLeapYear(year)) {
			dayArray[month] = 29;
		}

		// Determine the which day of the week the 1st fall upon
		int fDay = firstDay(month, 1, year);
		Debug.println("Firstday for " + month + "/" + year + ": " + fDay);

		// Insert spaces until we get to first day of the month in the calendar
		for (int i = 0; i < fDay; i++) {
			returnString[counter] += ("   ");
		}

		// Print the days. After 7 start a new line.
		for (int i = 1; i <= dayArray[month]; i++) {
			returnString[counter] += String.format("%2d ", i);

			// Start over if we've printed 7 days
			if (((i + fDay) % 7 == 0) || (i == dayArray[month])) {
				// Ensure that the array element is padded with space characters
				if (returnString[counter].length() < CALENDARWIDTH) {
					returnString[counter] += " ".repeat(CALENDARWIDTH - returnString[counter].length() + 1);
				}
				counter++;
			}
		}

		// Ensure last array element is 20 characters. Pad with spaces.
		int lastElement = returnString.length - 1;
		if (returnString[lastElement].length() < CALENDARWIDTH) {
			returnString[lastElement] += " ".repeat(CALENDARWIDTH - returnString[lastElement].length() + 1);
		}

		return returnString;
	}

}

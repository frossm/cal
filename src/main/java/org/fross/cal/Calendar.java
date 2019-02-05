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

public class Calendar {

	/**
	 * firstDay(): Given the month, day, and year, return which day of the week it
	 * falls https://www.tondering.dk/claus/cal/chrweek.php#calcdow
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
	 * https://www.wikihow.com/Calculate-Leap-Years
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
	public static void printMonth(String[] args) {
		int monthEntered = Integer.parseInt(args[0]); // month (Jan = 1, Dec = 12)
		int yearEntered = Integer.parseInt(args[1]); // year
		int calWidth = 20;

		// Build arrays
		String[] monthArray = { "none", "January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December" };
		int[] dayArray = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

		// Lets determine if the year selected is a leap year
		if (monthEntered == 2 && isLeapYear(yearEntered))
			dayArray[monthEntered] = 29;

		// Display the month header centered in the calendar display
		String headerString = monthArray[monthEntered] + " " + yearEntered;
		int numSpaces = (calWidth / 2) - (headerString.length() / 2) - 1;
		for (int i = 0; i <= numSpaces; i++) {
			Output.print(" ");
		}
		Output.println(headerString);
		Output.println(" S  M Tu  W Th  F  S");

		// Determine the which day of the week the 1st fall upon
		int dow = firstDay(monthEntered, 1, yearEntered);

		// Print first line of calendar
		for (int i = 0; i < dow; i++)
			Output.print("   ");
		// Print remaining days
		for (int i = 1; i <= dayArray[monthEntered]; i++) {
			System.out.printf("%2d ", i);
			if (((i + dow) % 7 == 0) || (i == dayArray[monthEntered]))
				Output.println("");
		}

	}
}

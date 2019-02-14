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

import com.diogonunes.jcdp.color.api.Ansi.FColor;

/**
 * Help(): Display the help page when users enters 'h' or '?' command.
 * 
 * @author michael.d.fross
 *
 */
public class Help {
	/**
	 * display(): Prints help in color using the JCDP library in the output module.
	 */
	public static void display() {
		Output.printcolorln(FColor.YELLOW, "\n+----------------------------------------------------------------------+");
		Output.printcolorln(FColor.YELLOW, "-                   CAL - Console Calendar Generator                   -");
		Output.printcolorln(FColor.YELLOW, "-                          Version "+Main.VERSION+"                          -");
		Output.printcolorln(FColor.YELLOW, "-                             Michael Fross                            -");
		Output.printcolorln(FColor.YELLOW, "+----------------------------------------------------------------------+");
		Output.printcolorln(FColor.CYAN, "               https://bitbucket.org/frossm/cal/src/default\n");

		Output.printcolorln(FColor.YELLOW, "Command Line Options:");
		Output.printcolorln(FColor.WHITE, " -n #        Sets the number of calendars per row when displaying a year.  Default: 3");
		Output.printcolorln(FColor.WHITE, " -D          Start in debug mode");
		Output.printcolorln(FColor.WHITE, " -h or -?    Display this help information\n");

		Output.printcolorln(FColor.YELLOW, "Parameters:");
		Output.printcolorln(FColor.WHITE, "NONE         - Display the current year");
		Output.printcolorln(FColor.WHITE, "YEAR         - Display the entire year");
		Output.printcolorln(FColor.WHITE, "MONTH        - Display the current month in the current year");
		Output.printcolorln(FColor.WHITE, "MONTH YEAR   - Display the current month in the provided year\n");

		Output.printcolorln(FColor.YELLOW, "Examples:");
		Output.printcolorln(FColor.WHITE, "  java -jar cal.jar         Display the current year");
		Output.printcolorln(FColor.WHITE, "  java -jar cal.jar -n 4    Display the current year - 4 months per row");
		Output.printcolorln(FColor.WHITE, "  java -jar cal.jar 9       Display September of the current year");
		Output.printcolorln(FColor.WHITE, "  java -jar cal.jar 2022    Display the entire year 2022");
		Output.printcolorln(FColor.WHITE, "  java -jar cal.jar 9 2022  Display September of 2022");
		Output.printcolorln(FColor.WHITE, "  java -jar cal.jar -D 6    Display June of current year in debug mode");
		Output.printcolorln(FColor.WHITE, "  java -jar cal.jar -h      Show this help information");
	}
}

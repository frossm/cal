/******************************************************************************
 * cal.java
 * 
 * A simple console based calculator generator
 * 
 *  Written by Michael Fross.  Copyright 2011-2019.  All rights reserved.
 *  
 *  License: MIT License / https://opensource.org/licenses/MIT
 *  Please see included LICENSE.txt file for additional details
 *           
 ******************************************************************************/
package org.fross.cal;

import org.fusesource.jansi.Ansi;

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
		Output.printcolorln(Ansi.Color.YELLOW, "\n+----------------------------------------------------------------------+");
		Output.printcolorln(Ansi.Color.YELLOW, "+                   CAL - Console Calendar Generator                   +");
		Output.printcolorln(Ansi.Color.YELLOW, "+                          Version " + Main.VERSION + "                          +");
		Output.printcolorln(Ansi.Color.YELLOW, "+                             Michael Fross                            +");
		Output.printcolorln(Ansi.Color.YELLOW, "+----------------------------------------------------------------------+");
		Output.printcolorln(Ansi.Color.CYAN, "                        https://github.com/frossm/cal\n");

		Output.printcolorln(Ansi.Color.YELLOW, "Command Line Options:");
		Output.printcolorln(Ansi.Color.WHITE, " -n #        Sets the number of calendars per row when displaying a year.  Default: 3");
		Output.printcolorln(Ansi.Color.WHITE, " -D          Start in debug mode");
		Output.printcolorln(Ansi.Color.WHITE, " -h or -?    Display this help information\n");

		Output.printcolorln(Ansi.Color.YELLOW, "Parameters:");
		Output.printcolorln(Ansi.Color.WHITE, "NONE         - Display the current year");
		Output.printcolorln(Ansi.Color.WHITE, "YEAR         - Display the entire year");
		Output.printcolorln(Ansi.Color.WHITE, "MONTH        - Display the current month in the current year");
		Output.printcolorln(Ansi.Color.WHITE, "MONTH YEAR   - Display the current month in the provided year\n");

		Output.printcolorln(Ansi.Color.YELLOW, "Examples:");
		Output.printcolorln(Ansi.Color.WHITE, "  java -jar cal.jar         Display the current year");
		Output.printcolorln(Ansi.Color.WHITE, "  java -jar cal.jar -n 4    Display the current year - 4 months per row");
		Output.printcolorln(Ansi.Color.WHITE, "  java -jar cal.jar 9       Display September of the current year");
		Output.printcolorln(Ansi.Color.WHITE, "  java -jar cal.jar 2022    Display the entire year 2022");
		Output.printcolorln(Ansi.Color.WHITE, "  java -jar cal.jar 9 2022  Display September of 2022");
		Output.printcolorln(Ansi.Color.WHITE, "  java -jar cal.jar -D 6    Display June of current year in debug mode");
		Output.printcolorln(Ansi.Color.WHITE, "  java -jar cal.jar -h      Show this help information");
	}
}

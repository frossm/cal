/******************************************************************************
 *  Cal - A command line calendar utility
 *  
 *  Copyright (c) 2019 Michael Fross
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

import org.fross.library.Output;
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
		Output.printColorln(Ansi.Color.CYAN, "\n+----------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.WHITE, "+                   CAL - Console Calendar Generator                   +");
		Output.printColorln(Ansi.Color.WHITE, "+                          Version " + Main.VERSION + "                          +");
		Output.printColorln(Ansi.Color.WHITE, "+      " + Main.COPYRIGHT + "      +");
		Output.printColorln(Ansi.Color.CYAN, "+----------------------------------------------------------------------+");
		Output.printColorln(Ansi.Color.CYAN, "                        https://github.com/frossm/cal\n");

		Output.printColorln(Ansi.Color.YELLOW, "Command Line Options:");
		Output.printColorln(Ansi.Color.WHITE, " -n #        Number of calendars per row in Year view.  Default: " + Calendar.DEFAULT_CALS_PER_ROW);
		Output.printColorln(Ansi.Color.WHITE, " -D          Start in debug mode");
		Output.printColorln(Ansi.Color.WHITE, " -v          Display program version and exit");
		Output.printColorln(Ansi.Color.WHITE, " -z          Display colorized output");
		Output.printColorln(Ansi.Color.WHITE, " -h or -?    Display this help information\n");

		Output.printColorln(Ansi.Color.YELLOW, "Parameters:");
		Output.printColorln(Ansi.Color.WHITE, "<None>       - Display the current year");
		Output.printColorln(Ansi.Color.WHITE, "YEAR         - Display the entire year");
		Output.printColorln(Ansi.Color.WHITE, "MONTH        - Display the current month in the current year");
		Output.printColorln(Ansi.Color.WHITE, "MONTH YEAR   - Display the current month in the provided year\n");

		Output.printColorln(Ansi.Color.YELLOW, "Examples:");
		Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar         Display the current year");
		Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar -n 4    Display the current year - 4 months per row");
		Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar 9       Display September of the current year");
		Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar 2022    Display the entire year 2022");
		Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar 9 2022  Display September of 2022");
		Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar -D 6    Display June of current year in debug mode");
		Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar -h      Show this help information");
	}
}
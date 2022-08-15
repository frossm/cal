/******************************************************************************
 *  Cal - A command line calendar utility
 *  
 *  Copyright (c) 2019-2022 Michael Fross
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.fross.library.Debug;
import org.fross.library.GitHub;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

import gnu.getopt.Getopt;

/**
 * Main - Main program execution class
 * 
 * @author michael.d.fross
 *
 */
public class Main {

	// Class Constants
	public static String VERSION;
	public static String COPYRIGHT;
	public static final String PROPERTIES_FILE = "app.properties";

	/**
	 * Main(): Start of program and holds main command loop
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int optionEntry;
		int month, year;
		int numCalPerRow = 0;

		// Process application level properties file
		// Update properties from Maven at build time:
		// https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
		try {
			InputStream iStream = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			Properties prop = new Properties();
			prop.load(iStream);
			VERSION = prop.getProperty("Application.version");
			COPYRIGHT = "Copyright " + prop.getProperty("Application.inceptionYear") + "-" + org.fross.library.Date.getCurrentYear() + " by Michael Fross";
		} catch (IOException ex) {
			Output.fatalError("Unable to read property file '" + PROPERTIES_FILE + "'", 3);
		}

		// Populate the month and year with todays values as a default
		month = org.fross.library.Date.getCurrentMonth();
		year = org.fross.library.Date.getCurrentYear();

		// Process Command Line Options and set flags where needed
		Getopt optG = new Getopt("cal", args, "n:Dvzh?");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			case 'n': // Set Number of Calendars per Row
				try {
					numCalPerRow = Integer.parseInt(optG.getOptarg());
					if (numCalPerRow <= 0) {
						throw new UnsupportedOperationException();
					}
				} catch (Exception Ex) {
					Output.fatalError("Invalid option for -n switch: '" + optG.getOptarg() + "'", 0);
				}
				Calendar.setCalsPerRow(numCalPerRow);
				break;

			case 'D': // Debug Mode
				Debug.enable();
				break;

			case 'v': // Version
				Output.printColorln(Ansi.Color.WHITE, "Cal Version: v" + VERSION);
				Output.printColorln(Ansi.Color.CYAN, COPYRIGHT);
				Output.printColorln(Ansi.Color.WHITE, "\nLatest Release on GitHub: " + GitHub.updateCheck("cal"));
				Output.printColorln(Ansi.Color.CYAN, "HomePage: https://github.com/frossm/cal");
				System.exit(0);
				break;

			case 'z': // Disable Colorized Output
				Output.enableColor(false);
				break;

			case '?': // Help
			case 'h':
				Help.display();
				System.exit(0);
				break;

			default:
				Output.fatalError("ERROR: Unknown Command Line Option -" + optG.getOptarg() + "'", 0);
				Help.display();
				break;
			}
		}

		// Display some useful information about the environment if in Debug Mode
		Debug.displaySysInfo();
		Output.debugPrint("Command Line Options");
		Output.debugPrint("  -D:  " + Debug.query());
		Output.debugPrint("  -n:  " + numCalPerRow);

		// Process the command line parameters (non-options). Update month and year as needed
		Output.debugPrint("Number of command line arguments:  " + args.length);
		int clParameters = args.length - optG.getOptind();
		Output.debugPrint("Number of command line parameters: " + clParameters);
		Output.debugPrint("Current Date: Month: " + month + "   Year: " + year);

		// Ensure the month and year are greater than zero
		for (int i = 0; i < clParameters; i++) {
			if (args[i].compareTo("0") == 0) {
				Output.fatalError("Month & Year must be greater than zero", 6);
			}
		}

		// Process options and display the calendar
		try {
			Output.println("");

			switch (clParameters) {
			case 0:
				// Process no dates provided
				Output.debugPrint("No Month or Year provided on command line. Using Year: " + year);
				Calendar.printYear(month, year);
				break;

			case 1:
				// Just a date or month provided
				int d = Integer.parseInt(args[optG.getOptind()]);

				// Assume the number provided is a year
				if (d > 12) {
					year = d;
					Output.debugPrint("Commandline Year provided. Using Month: " + month + "  Year: " + year);
					Calendar.printYear(month, year);

					// Assume the number provided is a month
				} else {
					month = d;
					Output.debugPrint("Commandline Month provided. Using Month: " + month + "  Year:" + year);
					Calendar.printMonth(month, year);
				}
				break;
			case 2:
				// Month & year provided
				month = Integer.parseInt(args[optG.getOptind()]);
				year = Integer.parseInt(args[optG.getOptind() + 1]);
				Output.debugPrint("Commandline Month & Year provided. Month: " + month + "  Year: " + year);
				Calendar.printMonth(month, year);
				break;

			default:
				// Ignore anything beyond the first two parameters
				break;
			}

		} catch (NumberFormatException ex) {
			Output.fatalError("Parameters can only be numbers.  Usage '-h' for options", 0);
		} catch (Exception ex) {
			ex.getMessage();
			ex.printStackTrace();
			Output.fatalError("Something went very wrong.  You shouldn't really see this.  Eeek!", 0);
		}

	}
}
/******************************************************************************
 * cal.java
 * 
 * A simple console based calculator generator
 * 
 *  Written by Michael Fross.  Copyright 2011-2020.  All rights reserved.
 *  
 *  License: MIT License / https://opensource.org/licenses/MIT
 *  Please see included LICENSE.txt file for additional details
 *           
 ******************************************************************************/
package org.fross.cal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.fusesource.jansi.Ansi;
import gnu.getopt.Getopt;
import org.fross.library.Output;
import org.fross.library.Debug;

/**
 * Main - Main program execution class
 * 
 * @author michael.d.fross
 *
 */
public class Main {

	// Class Constants
	public static String VERSION;
	public static String INCEPTIONYEAR;
	public static final String PROPERTIES_FILE = "app.properties";

	/**
	 * Main(): Start of program and holds main command loop
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int optionEntry;
		int month, year;

		// Process application level properties file
		// Update properties from Maven at build time:
		// https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
		try {
			InputStream iStream = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			Properties prop = new Properties();
			prop.load(iStream);
			VERSION = prop.getProperty("Application.version");
			INCEPTIONYEAR = prop.getProperty("Application.inceptionYear");
		} catch (IOException ex) {
			Output.fatalError("Unable to read property file '" + PROPERTIES_FILE + "'", 3);
		}

		// Populate the month and year with todays values as a default
		month = org.fross.library.Date.getCurrentMonth();
		year = org.fross.library.Date.getCurrentYear();

		// Process Command Line Options and set flags where needed
		Getopt optG = new Getopt("cal", args, "n:Dh?");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			case 'n': // Set Number of Calendars per Row
				int newNum = 0;
				try {
					newNum = Integer.parseInt(optG.getOptarg());
				} catch (Exception Ex) {
					Output.fatalError("Invalid option for -n switch: '" + optG.getOptarg() + "'", 0);
				}
				Calendar.setCalsPerRow(newNum);
				break;

			case 'D': // Debug Mode
				Debug.enable();
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
		Output.debugPrint("System Information:");
		Output.debugPrint(" - class.path:     " + System.getProperty("java.class.path"));
		Output.debugPrint("  - java.home:      " + System.getProperty("java.home"));
		Output.debugPrint("  - java.vendor:    " + System.getProperty("java.vendor"));
		Output.debugPrint("  - java.version:   " + System.getProperty("java.version"));
		Output.debugPrint("  - os.name:        " + System.getProperty("os.name"));
		Output.debugPrint("  - os.version:     " + System.getProperty("os.version"));
		Output.debugPrint("  - os.arch:        " + System.getProperty("os.arch"));
		Output.debugPrint("  - user.name:      " + System.getProperty("user.name"));
		Output.debugPrint("  - user.home:      " + System.getProperty("user.home"));
		Output.debugPrint("  - user.dir:       " + System.getProperty("user.dir"));
		Output.debugPrint("  - file.separator: " + System.getProperty("file.separator"));
		Output.debugPrint("  - library.path:   " + System.getProperty("java.library.path"));
		Output.debugPrint("\nCommand Line Options");
		Output.debugPrint("  -D:  " + Debug.query() + "\n");
		Output.debugPrint("Current Date: Month = " + month + " Year =" + year);

		// Process the command line parameters (non-options). Update month and year as
		// needed
		Output.debugPrint("Number of command line arguments:  " + args.length);
		int clParameters = args.length - optG.getOptind();
		Output.debugPrint("Number of command line parameters: " + clParameters);

		// Display header information
		int headerWidth = (Calendar.CALENDARWIDTH * Calendar.calsPerRow) + (Calendar.calsPerRow * Calendar.SPACESBETWEENCALS) - 2;
		String headerText = "Cal v" + VERSION + "  by Michael Fross";
		int headerSpaces = headerWidth / 2 - headerText.length() / 2;

		// Ensure we have enough room if user selects -n1
		if (headerSpaces < 0) {
			headerSpaces = 0;
			headerWidth = headerText.length();
		}

		Output.debugPrint("headerWidth = " + headerWidth);
		Output.debugPrint("headerText = " + "'" + headerText + "'  (Len = " + headerText.length() + ")");
		Output.debugPrint("headerSpaces = " + headerSpaces);

		Output.printColorln(Ansi.Color.CYAN, "\n+" + "-".repeat(headerWidth) + "+");
		Output.printColorln(Ansi.Color.YELLOW, " ".repeat(headerSpaces) + headerText);
		Output.printColorln(Ansi.Color.CYAN, "+" + "-".repeat(headerWidth) + "+");

		// Process options and display the calendar
		try {
			Output.println("");
			switch (clParameters) {
			case 0:
				// Process no dates provided
				Output.debugPrint("No Month or Year provided on command line. Using Year:" + year);
				Calendar.printYear(month, year);
				break;
			case 1:
				// Just a date or month provided
				int d = Integer.parseInt(args[optG.getOptind()]);
				if (d > 12) {
					year = d;
					Output.debugPrint("Commandline Year provided. Using Month: " + month + " Year:" + year);
					Output.debugPrint(" 1         2         3         4         5         6         7");
					Output.debugPrint("90123456789012345678901234567890123456789012345678901234567890");
					Calendar.printYear(month, year);
				} else {
					month = d;
					Output.debugPrint("Commandline Month provided. Using Month: " + month + " Year:" + year);
					Calendar.printMonth(month, year);
				}
				break;
			case 2:
				// Month & year provided
				month = Integer.parseInt(args[optG.getOptind()]);
				year = Integer.parseInt(args[optG.getOptind() + 1]);
				Output.debugPrint("Commandline Month & Year provided. Month:" + month + " Year:" + year);
				Calendar.printMonth(month, year);
				break;
			default:
				// Ignore anything beyond the first two parameters
			}
		} catch (NumberFormatException ex) {
			Output.fatalError("Parameters can only be numbers.  Usage '-h' for options", 0);
		} catch (Exception ex) {
			ex.getMessage();
			ex.printStackTrace();
			Output.fatalError("Something went wrong.  You shouldn't really see this.  Eeek!", 0);
		}

		// Program End
	}
}
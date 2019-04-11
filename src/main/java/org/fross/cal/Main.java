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

import gnu.getopt.Getopt;

/**
 * Main - Main program execution class
 * 
 * @author michael.d.fross
 *
 */
public class Main {

	// Class Constants
	public static final String VERSION = "2019.04.11";

	/**
	 * Main(): Start of program and holds main command loop
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int optionEntry;
		int month, year;

		// Populate the month and year with todays values as a default
		java.util.Calendar jc = java.util.Calendar.getInstance();
		month = jc.get(java.util.Calendar.MONTH) + 1;
		year = jc.get(java.util.Calendar.YEAR);

		// Process Command Line Options and set flags where needed
		Getopt optG = new Getopt("DirSize", args, "n:Dh?");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			case 'n': // Set Number of Calendars per Row
				int newNum = 0;
				try {
					newNum = Integer.parseInt(optG.getOptarg());
				} catch (Exception Ex) {
					Output.fatalerror("Invalid option for -n switch: '" + optG.getOptarg() + "'", 0);
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
				Output.fatalerror("ERROR: Unknown Command Line Option -" + optG.getOptarg() + "'", 0);
				Help.display();

				break;
			}
		}

		// Display some useful information about the environment if in Debug Mode
		Debug.println("System Information:");
		Debug.println(" - class.path:     " + System.getProperty("java.class.path"));
		Debug.println("  - java.home:      " + System.getProperty("java.home"));
		Debug.println("  - java.vendor:    " + System.getProperty("java.vendor"));
		Debug.println("  - java.version:   " + System.getProperty("java.version"));
		Debug.println("  - os.name:        " + System.getProperty("os.name"));
		Debug.println("  - os.version:     " + System.getProperty("os.version"));
		Debug.println("  - os.arch:        " + System.getProperty("os.arch"));
		Debug.println("  - user.name:      " + System.getProperty("user.name"));
		Debug.println("  - user.home:      " + System.getProperty("user.home"));
		Debug.println("  - user.dir:       " + System.getProperty("user.dir"));
		Debug.println("  - file.separator: " + System.getProperty("file.separator"));
		Debug.println("  - library.path:   " + System.getProperty("java.library.path"));
		Debug.println("\nCommand Line Options");
		Debug.println("  -D:  " + Debug.query() + "\n");
		Debug.println("Current Date: Month = " + month + " Year =" + year);

		// Process the command line parameters (non-options). Update month and year as
		// needed
		Debug.println("Number of command line arguments:  " + args.length);
		int clParameters = args.length - optG.getOptind();
		Debug.println("Number of command line parameters: " + clParameters);

		// Display header information
		int headerWidth = (Calendar.CALENDARWIDTH * Calendar.calsPerRow)
				+ (Calendar.calsPerRow * Calendar.SPACESBETWEENCALS) - 2;
		String headerText = "Cal v" + VERSION + "  by Michael Fross";
		int headerSpaces = headerWidth / 2 - headerText.length() / 2;

		// Ensure we have enough room if user selects -n1
		if (headerSpaces < 0) {
			headerSpaces = 0;
			headerWidth = headerText.length();
		}

		Debug.println("headerWidth = " + headerWidth);
		Debug.println("headerText = " + "'" + headerText + "'  (Len = " + headerText.length() + ")");
		Debug.println("headerSpaces = " + headerSpaces);

		Output.printcolorln(FColor.CYAN, "\n+" + "-".repeat(headerWidth) + "+");
		Output.printcolorln(FColor.YELLOW, " ".repeat(headerSpaces) + headerText);
		Output.printcolorln(FColor.CYAN, "+" + "-".repeat(headerWidth) + "+");

		// Process options and display the calendar
		try {
			Output.println("");
			switch (clParameters) {
			case 0:
				// Process no dates provided
				Debug.println("No Month or Year provided on command line. Using Year:" + year);
				Calendar.printYear(month, year);
				break;
			case 1:
				// Just a date or month provided
				int d = Integer.parseInt(args[optG.getOptind()]);
				if (d > 12) {
					year = d;
					Debug.println("Commandline Year provided. Using Month: " + month + " Year:" + year);
					Debug.println(" 1         2         3         4         5         6         7");
					Debug.println("90123456789012345678901234567890123456789012345678901234567890");
					Calendar.printYear(month, year);
				} else {
					month = d;
					Debug.println("Commandline Month provided. Using Month: " + month + " Year:" + year);
					Calendar.printMonth(month, year);
				}
				break;
			case 2:
				// Month & year provided
				month = Integer.parseInt(args[optG.getOptind()]);
				year = Integer.parseInt(args[optG.getOptind() + 1]);
				Debug.println("Commandline Month & Year provided. Month:" + month + " Year:" + year);
				Calendar.printMonth(month, year);
				break;
			default:
				// Ignore anything beyond the first two parameters
			}
		} catch (NumberFormatException ex) {
			Output.fatalerror("Parameters can only be numbers.  Usage '-h' for options", 0);
		} catch (Exception ex) {
			ex.getMessage();
			ex.printStackTrace();
			Output.fatalerror("Something went wrong.  You shouldn't really see this.  Eeek!", 0);
		}

		// Program End
	}
}
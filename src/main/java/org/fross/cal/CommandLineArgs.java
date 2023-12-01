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

import java.util.ArrayList;
import java.util.List;

import org.fross.library.Debug;
import org.fross.library.GitHub;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class CommandLineArgs {
	static CommandLineArgs cli = new CommandLineArgs();
	static JCommander jc = new JCommander();
	static int monthToUse = org.fross.library.Date.getCurrentMonth();
	static int yearToUse = org.fross.library.Date.getCurrentYear();

	// ---------------------------------------------------------------------------------------------
	// Define command line options that can be used
	// ---------------------------------------------------------------------------------------------

	@Parameter(names = { "-h", "-?", "--help" }, help = true, description = "Display cal help and exit")
	protected boolean clHelp = false;

	@Parameter(names = { "-z", "--no-color" }, description = "Disable colorized output")
	protected boolean clNoColor = false;

	@Parameter(names = { "-v", "--version" }, description = "Show current program version and latest release on GitHub")
	protected boolean clVersion = false;

	@Parameter(names = { "-D", "--debug" }, description = "Turn on Debug mode to display extra program information")
	protected boolean clDebug = false;

	@Parameter(names = { "-d", "--display-holidays" }, description = "Display local country holidays in the calendar")
	protected boolean clDisplayHolidays = false;

	@Parameter(names = { "-n", "--num" }, description = "Number of calendar months to display per row")
	protected int clNum = Calendar.DEFAULT_CALS_PER_ROW;

	@Parameter(description = "Month and/or Year")
	protected List<String> clMonthAndOrYear = new ArrayList<>();

	// ---------------------------------------------------------------------------------------------
	// Process command line parameters with the following methods
	// ---------------------------------------------------------------------------------------------
	public static void ProcessCommandLine(String[] argv) {
		// JCommander parses the command line
		try {
			jc.setProgramName("cal");
			jc = JCommander.newBuilder().addObject(cli).build();
			jc.parse(argv);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			jc.usage();
			System.exit(0);
		}

		// ---------------------------------------------------------------------------------------------
		// Process the parsed command line options
		// ---------------------------------------------------------------------------------------------
		// Debug Switch
		if (cli.clDebug == true) {
			Debug.enable();
		}

		// Set the stack name and restore stack from Preferences
		if (cli.clNum != Calendar.DEFAULT_CALS_PER_ROW) {
			try {
				if (cli.clNum <= 0) {
					throw new UnsupportedOperationException();
				}
			} catch (Exception Ex) {
				Output.fatalError("Invalid option for -n switch: '" + cli.clNum + "'", 0);
			}
			Calendar.setCalsPerRow(cli.clNum);
		}

		// Version Switch
		if (cli.clVersion == true) {
			Output.printColorln(Ansi.Color.WHITE, "Cal Version: v" + Main.VERSION);
			Output.printColorln(Ansi.Color.CYAN, Main.COPYRIGHT);
			Output.printColorln(Ansi.Color.WHITE, "\nLatest Release on GitHub: " + GitHub.updateCheck("cal"));
			Output.printColorln(Ansi.Color.CYAN, "HomePage: https://github.com/frossm/cal");
			System.exit(0);
		}

		// Disable Colorized Output Switch
		if (cli.clNoColor == true) {
			Output.enableColor(false);
		}

		// Display local county holidays in the calendar
		if (cli.clDisplayHolidays == true) {
			Holidays.setDisplayHolidays(true);
		}

		// Show Help and Exit
		if (cli.clHelp == true) {
			Help.display();
			System.exit(0);
		}

		// Process any month/year parameters that are given and set monthToUse and yeareToUse
		try {
			switch (cli.clMonthAndOrYear.size()) {

			// Process no dates provided
			case 0:
				Output.debugPrintln("No Month or Year provided on command line. Showing current year: " + yearToUse);
				break;

			// Just a date or month provided
			case 1:
				int d = Integer.parseInt(cli.clMonthAndOrYear.get(0));

				// Number must be a year if it's greater than 12
				if (d > 12) {
					yearToUse = d;
					Output.debugPrintln("Commandline Year provided. Showing Year: " + yearToUse);

					// If number is <= 12, assume it's a month
				} else {
					monthToUse = d;
					Output.debugPrintln("Commandline Month provided. Using Month: " + monthToUse + "  Year:" + yearToUse);
				}
				break;

			// Month & year provided
			case 2:
				monthToUse = Integer.parseInt(cli.clMonthAndOrYear.get(0));
				yearToUse = Integer.parseInt(cli.clMonthAndOrYear.get(1));
				Output.debugPrintln("Commandline Month & Year provided. Month: " + monthToUse + "  Year: " + yearToUse);
				break;
			}

		} catch (NumberFormatException ex) {
			Output.fatalError("Parameters can only be numbers.  Usage '-h' for options", 99);

		} catch (Exception ex) {
			ex.getMessage();
			ex.printStackTrace();
			Output.fatalError("Something went very wrong.  You shouldn't really see this.  Eeek!", 99);
		}

	}

	/**
	 * Return the month to use after processing the command line
	 * 
	 * @return
	 */
	public static int queryMonthToUse() {
		return monthToUse;
	}

	/**
	 * Return the year to use after processing the command line
	 * 
	 * @return
	 */
	public static int queryYearToUse() {
		return yearToUse;
	}
}

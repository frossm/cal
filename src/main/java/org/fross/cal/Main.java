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

import org.fross.library.Debug;
import org.fross.library.Output;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * Main - Main program execution class
 *
 * @author michael.d.fross
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

      // Process the command line arguments and switches
      CommandLineArgs.ProcessCommandLine(args);

      // Display some useful information about the environment if in Debug Mode
      Debug.displaySysInfo();
      Output.debugPrintln("Current locale set to: " + Locale.getDefault().getDisplayCountry());
      Output.debugPrintln("Command Line Options");
      Output.debugPrintln("  -D:  " + Debug.query());
      Output.debugPrintln("  -n:  " + Calendar.queryCalsPerRow());
      Output.debugPrintln("  -z:  " + Output.queryColorEnabled());
      Output.debugPrintln("  -d:  " + Holidays.queryHolidaysEnabled());
      Output.debugPrintln("Number of command line arguments:  " + args.length);

      // Ensure there are not more than 2 parameters given
      if (CommandLineArgs.cli.clMonthAndOrYear.size() > 2) {
         Output.fatalError("There can not be more than 2 dates given on the commandline.\nPlease see Help (-h)", 6);
      }

      // Ensure the month / year is a valid integer
      for (int i = 0; i < CommandLineArgs.cli.clMonthAndOrYear.size(); i++) {
         int monthAndOrYear = 0;
         try {
            monthAndOrYear = Integer.parseInt(CommandLineArgs.cli.clMonthAndOrYear.get(i));

            // Ensure no negative value is provided for the month and/or year
            if (monthAndOrYear <= 0) {
               Output.fatalError("Month & Year values must be greater than zero", 6);

               // Validate the month is between 1 and 12
            } else if (CommandLineArgs.queryMonthToUse() < 1 || CommandLineArgs.queryMonthToUse() > 12) {
               Output.fatalError(String.format("'%d' is not a valid month number", CommandLineArgs.queryMonthToUse()), 7);
            }

         } catch (Exception ex) {
            Output.fatalError("Invalid Month and/or Year: '" + monthAndOrYear + "'", 6);
         }
      }

      // Display the calendars
      Output.println("");
      switch (CommandLineArgs.cli.clMonthAndOrYear.size()) {

         // No month or year provided
         case 0:
            Calendar.printYear(CommandLineArgs.queryYearToUse());
            break;

         // A month or year was given. Assume it's a month if it's 1-12
         case 1:
            if (Integer.parseInt(CommandLineArgs.cli.clMonthAndOrYear.get(0)) > 12) {
               Calendar.printYear(CommandLineArgs.queryYearToUse());
            } else {
               Calendar.printMonth(CommandLineArgs.queryMonthToUse(), CommandLineArgs.queryYearToUse());
            }
            break;

         // A month and a year was provided
         case 2:
            Calendar.printMonth(CommandLineArgs.queryMonthToUse(), CommandLineArgs.queryYearToUse());
            break;
      }
   }
}
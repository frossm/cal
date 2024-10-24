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

import org.fross.library.Format;
import org.fross.library.Output;
import org.fusesource.jansi.Ansi;

/**
 * Help(): Display the help page when users enters 'h' or '?' command.
 *
 * @author michael.d.fross
 */
public class Help {
   static final int HELPWIDTH = 80;

   /**
    * display(): Prints help in color using the JCDP library in the output module.
    */
   public static void display() {
      Output.printColorln(Ansi.Color.CYAN, "\n+" + "-".repeat(HELPWIDTH) + "+");
      Output.printColorln(Ansi.Color.WHITE, "+" + Format.CenterText(HELPWIDTH, "Cal - The Console Calendar Generator") + "+");
      Output.printColorln(Ansi.Color.WHITE, "+" + Format.CenterText(HELPWIDTH, "Version v" + Main.VERSION) + "+");
      Output.printColorln(Ansi.Color.WHITE, "+" + Format.CenterText(HELPWIDTH, Main.COPYRIGHT) + "+");
      Output.printColorln(Ansi.Color.CYAN, "+" + "-".repeat(HELPWIDTH) + "+");
      Output.printColorln(Ansi.Color.CYAN, Format.CenterText(HELPWIDTH, "https://github.com/frossm/cal"));

      Output.printColorln(Ansi.Color.YELLOW, "\nCommand Line Options:");
      Output.printColorln(Ansi.Color.WHITE, " -n #        Number of calendars per row in Year view.  Default: " + Calendar.DEFAULT_CALS_PER_ROW);
      Output.printColorln(Ansi.Color.WHITE, " -d          Display local county holidays in the calendar");
      Output.printColorln(Ansi.Color.WHITE, " -c          Clear the holiday cache on the local computer and exit");
      Output.printColorln(Ansi.Color.WHITE, " -D          Start in debug mode");
      Output.printColorln(Ansi.Color.WHITE, " -v          Display the program version and latest GitHub Cal release");
      Output.printColorln(Ansi.Color.WHITE, " -z          Disable colorized output");
      Output.printColorln(Ansi.Color.WHITE, " -h or -?    Display this help information");

      Output.printColorln(Ansi.Color.YELLOW, "\nParameters:");
      Output.printColorln(Ansi.Color.WHITE, "<None>       Display the current year");
      Output.printColorln(Ansi.Color.WHITE, "YEAR         Display the entire YEAR");
      Output.printColorln(Ansi.Color.WHITE, "MONTH        Display the MONTH in the current year");
      Output.printColorln(Ansi.Color.WHITE, "MONTH YEAR   Display the MONTH and YEAR provided");

      Output.printColorln(Ansi.Color.YELLOW, "\nExamples:");
      Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar         Display the current year");
      Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar -n 4 -d Display the current year with 4 months per row with holidays");
      Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar 9       Display September of the current year");
      Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar 2022    Display the entire year 2022");
      Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar 9 2022  Display only September of 2022");
      Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar -D 6    Display June of current year in debug mode");
      Output.printColorln(Ansi.Color.WHITE, "  java -jar cal.jar -h      Show this help information");

      Output.printColorln(Ansi.Color.YELLOW, "\nNotes:");
      Output.printColorln(Ansi.Color.WHITE, "  For a list of supported countries for use with 'Display Holidays' option see:");
      Output.printColorln(Ansi.Color.WHITE, "     - https://date.nager.at/Country");

   }
}
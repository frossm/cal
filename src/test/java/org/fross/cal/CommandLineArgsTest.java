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

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Michael Fross (michael@fross.org)
 */
class CommandLineArgsTest {

   /**
    * Test short command line arguments
    */
   @Test
   void testShortCommandLineArgs() {
      String[] argv1 = {"-D", "-z", "-h", "-v", "-n", "2"};

      CommandLineArgs cli = new CommandLineArgs();
      JCommander jc = new JCommander();
      jc.setProgramName("cal");

      jc = JCommander.newBuilder().addObject(cli).build();
      jc.parse(argv1);

      assertTrue(cli.clDebug);
      assertTrue(cli.clNoColor);
      assertTrue(cli.clVersion);
      assertTrue(cli.clHelp);
      assertEquals(2, cli.clNum);
   }

   /**
    * Test long command line arguments
    */
   @Test
   void testLongCommandLineArgs() {
      String[] argv2 = {"--debug", "--no-color", "--help", "--version", "--num", "6"};

      CommandLineArgs cli = new CommandLineArgs();
      JCommander jc = new JCommander();
      jc.setProgramName("cal");

      jc = JCommander.newBuilder().addObject(cli).build();
      jc.parse(argv2);

      assertTrue(cli.clDebug);
      assertTrue(cli.clNoColor);
      assertTrue(cli.clVersion);
      assertTrue(cli.clHelp);
      assertEquals(6, cli.clNum);
   }

   /**
    * Test mixed long and short options
    */
   @Test
   void testMixedCommandLineArgs() {
      // Test Mix of Options
      String[] argv3 = {"--no-color", "-?", "--num", "12"};

      CommandLineArgs cli = new CommandLineArgs();
      JCommander jc = new JCommander();
      jc.setProgramName("cal");

      jc = JCommander.newBuilder().addObject(cli).build();
      jc.parse(argv3);

      assertFalse(cli.clDebug);
      assertTrue(cli.clNoColor);
      assertFalse(cli.clVersion);
      assertTrue(cli.clHelp);
      assertEquals(12, cli.clNum);
   }

   /**
    * Test Month / Year parameter combinations - No Arguments
    */
   @Test
   void testParametersNoArgs() {
      // Determine the current month and year at the time of execution of the test
      int currentMonth = CommandLineArgs.queryMonthToUse();
      int currentYear = CommandLineArgs.queryYearToUse();

      // Testing no Arguments. Result: Month=CurrentMonth | Year: CurrentYear
      String[] args0 = {};
      CommandLineArgs.ProcessCommandLine(args0);
      assertEquals(currentMonth, CommandLineArgs.queryMonthToUse());
      assertEquals(currentYear, CommandLineArgs.queryYearToUse());
   }

   /**
    * Test Month / Year parameter combinations - One Arguments
    */
   @Test
   void testParametersOneArg() {
      // Determine the current month and year at the time of execution of the test
      int currentMonth = CommandLineArgs.queryMonthToUse();
      int currentYear = CommandLineArgs.queryYearToUse();

      // Desired Result: Month=1 | Year: CurrentYear
      String[] args1 = {"1"};
      CommandLineArgs.ProcessCommandLine(args1);
      assertEquals(1, CommandLineArgs.queryMonthToUse());
      assertEquals(currentYear, CommandLineArgs.queryYearToUse());

      // Desired Result: Month=12 | Year: CurrentYear
      String[] args12 = {"12"};
      CommandLineArgs.ProcessCommandLine(args12);
      assertEquals(12, CommandLineArgs.queryMonthToUse());
      assertEquals(currentYear, CommandLineArgs.queryYearToUse());

      // Desired Result: Month=CurrentMonth | Year: 13
      String[] args13 = {"13"};
      CommandLineArgs.ProcessCommandLine(args13);
      assertEquals(currentMonth, CommandLineArgs.queryMonthToUse());
      assertEquals(13, CommandLineArgs.queryYearToUse());

      // Desired Result: Month=CurrentMonth | Year: 2300
      String[] args2300 = {"2300"};
      CommandLineArgs.ProcessCommandLine(args2300);
      assertEquals(currentMonth, CommandLineArgs.queryMonthToUse());
      assertEquals(2300, CommandLineArgs.queryYearToUse());
   }

   /**
    * Test Month / Year parameter combinations - Two Arguments
    */
   @Test
   void testParametersTwoArgs() {
      // Desired Result: Month=5 | Year: 2023
      String[] args1 = {"5", "2023"};
      CommandLineArgs.ProcessCommandLine(args1);
      assertEquals(5, CommandLineArgs.queryMonthToUse());
      assertEquals(2023, CommandLineArgs.queryYearToUse());

      // Desired Result: Month=12 | Year: 3000
      String[] args2 = {"12", "3000"};
      CommandLineArgs.ProcessCommandLine(args2);
      assertEquals(12, CommandLineArgs.queryMonthToUse());
      assertEquals(3000, CommandLineArgs.queryYearToUse());
   }

}

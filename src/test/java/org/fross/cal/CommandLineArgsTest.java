/*--------------------------------------------------------------------------------------
 * Cal - A command line calendar utility
 *
 * Copyright (c) 2018-2026 Michael Fross
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * --------------------------------------------------------------------------------------*/
package org.fross.cal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;

/**
 * CommandLineArgsTest: Comprehensive testing of command line parsing,
 * auto-fit terminal logic, and date parameter processing.
 */
class CommandLineArgsTest {
   private Terminal testTerminal;


   /**
    * setUp: Initialize a 'Dumb' terminal before each test.
    * This provides a predictable 80-character width for testing the auto-fit math.
    */
   @BeforeEach
   void setUp() throws IOException {
      CommandLineArgs.reset(); // Reset statics before every test!
      testTerminal = TerminalBuilder.builder()
            .dumb(true)
            .size(new org.jline.terminal.Size(80, 24))
            .build();
   }

   /**
    * testAutoFitLogic: Verifies that when -n is omitted, the program calculates
    * the number of months based on the terminal width (80 + 3) / 23 = 3.
    */
   @Test
   void testAutoFitLogic() {
      // Input: Just a display holidays flag, no -n provided
      String[] args = {"-d"};

      CommandLineArgs.ProcessCommandLine(args, testTerminal);

      // We expect 3 months per row for an 80-char wide terminal
      assertEquals(3, CommandLineArgs.queryNumToUse(), "Auto-fit should default to 3 months for 80-char width.");
   }

   /**
    * testUserOverrideNum: Verifies that if the user provides -n, it overrides
    * the auto-fit calculation.
    */
   @Test
   void testUserOverrideNum() {
      // Input: Specifically request 2 months per row
      String[] args = {"-n", "2"};

      CommandLineArgs.ProcessCommandLine(args, testTerminal);

      assertEquals(2, CommandLineArgs.queryNumToUse(), "User-provided -n should override auto-fit.");
   }

   /**
    * testNumBoundaries: Ensures that the number of months is capped at 12 (max)
    * and 1 (min) regardless of user input or terminal width.
    */
   @Test
   void testNumBoundaries() {
      // Test High Cap
      CommandLineArgs.ProcessCommandLine(new String[]{"-n", "99"}, testTerminal);
      assertEquals(12, CommandLineArgs.queryNumToUse(), "Months per row should be capped at 12.");

      // Test Low Cap
      CommandLineArgs.ProcessCommandLine(new String[]{"-n", "0"}, testTerminal);
      assertEquals(3, CommandLineArgs.queryNumToUse(), "Providing 0 should trigger auto-fit (3).");

      CommandLineArgs.ProcessCommandLine(new String[]{"-n", "-5"}, testTerminal);
      assertEquals(1, CommandLineArgs.queryNumToUse(), "Negative numbers should default to 1.");
   }

   /**
    * testDateParameters: Validates the logic for processing Month and Year arguments.
    */
   @Test
   void testDateParameters() {
      int currentMonth = org.fross.library.Date.getCurrentMonth();
      int currentYear = org.fross.library.Date.getCurrentYear();

      // Case 1: Single argument <= 12 is a Month
      CommandLineArgs.reset();
      CommandLineArgs.ProcessCommandLine(new String[]{"6"}, testTerminal);
      assertEquals(6, CommandLineArgs.queryMonthToUse());
      assertEquals(currentYear, CommandLineArgs.queryYearToUse());

      // Case 2: Single argument > 12 is a Year
      CommandLineArgs.reset();
      CommandLineArgs.ProcessCommandLine(new String[]{"2028"}, testTerminal);
      assertEquals(currentMonth, CommandLineArgs.queryMonthToUse());
      assertEquals(2028, CommandLineArgs.queryYearToUse());

      // Case 3: Two arguments are Month and Year
      CommandLineArgs.reset();
      CommandLineArgs.ProcessCommandLine(new String[]{"12", "2030"}, testTerminal);
      assertEquals(12, CommandLineArgs.queryMonthToUse());
      assertEquals(2030, CommandLineArgs.queryYearToUse());
   }

   /**
    * testFlagParsing: Ensures that standard boolean flags are parsed correctly
    * by the JCommander annotations.
    */
   @Test
   void testFlagParsing() {
      // We parse into a local instance to check internal state without triggering System.exit
      CommandLineArgs testCli = new CommandLineArgs();
      String[] args = {"--debug", "--no-color", "--display-holidays"};

      com.beust.jcommander.JCommander.newBuilder().addObject(testCli).build().parse(args);

      assertTrue(testCli.clDebug, "Debug flag should be true.");
      assertTrue(testCli.clNoColor, "No-Color flag should be true.");
      assertTrue(testCli.clDisplayHolidays, "Display Holidays flag should be true.");
      assertFalse(testCli.clHelp, "Help flag should remain false.");
   }
}
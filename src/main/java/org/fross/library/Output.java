/* ------------------------------------------------------------------------------
 * Library Project
 *
 *  Library holds methods and classes frequently used by my programs.
 *
 *  Copyright (c) 2011-2026 Michael Fross
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
 * ------------------------------------------------------------------------------*/
package org.fross.library;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp.Capability;

public class Output {
   // -------------------------------------------------------------------------
   // Standard 3-Bit Shorthand Color Constants (0 - 7)
   // -------------------------------------------------------------------------
   public static final int BLACK = AttributedStyle.BLACK;     // 0
   public static final int RED = AttributedStyle.RED;         // 1
   public static final int GREEN = AttributedStyle.GREEN;     // 2
   public static final int YELLOW = AttributedStyle.YELLOW;   // 3
   public static final int BLUE = AttributedStyle.BLUE;       // 4
   public static final int MAGENTA = AttributedStyle.MAGENTA; // 5
   public static final int CYAN = AttributedStyle.CYAN;       // 6
   public static final int WHITE = AttributedStyle.WHITE;     // 7

   // -------------------------------------------------------------------------
   // Extended 8-Bit (256-Color) Shorthand Constants (8 - 255)
   // These are standard Xterm-256 color indexes recognized natively by JLine.
   // -------------------------------------------------------------------------
   public static final int DARK_GRAY = 8;
   public static final int LIGHT_RED = 9;
   public static final int LIGHT_GREEN = 10;
   public static final int LIGHT_YELLOW = 11;
   public static final int LIGHT_BLUE = 12;
   public static final int LIGHT_MAGENTA = 13;
   public static final int LIGHT_CYAN = 14;
   public static final int BRIGHT_WHITE = 15;

   // Oranges & Yellows
   public static final int ORANGE = 208;
   public static final int LIGHT_ORANGE = 214;
   public static final int BRIGHT_GOLD = 220;
   public static final int MUSTARD = 142;
   public static final int GOLD = 220;

   // Reds & Pinks
   public static final int PINK = 205;
   public static final int NEON_PINK = 207;
   public static final int CORAL = 203;
   public static final int CRIMSON = 160;

   // Greens & Teals
   public static final int TEAL = 30;
   public static final int ELECTRIC_TEAL = 86;
   public static final int MINT_GREEN = 121;
   public static final int LIME = 118;
   public static final int OLIVE = 100;

   // Blues & Purples
   public static final int PURPLE = 93;
   public static final int AMETHYST = 129;
   public static final int SKY_BLUE = 117;
   public static final int ELECTRIC_BLUE = 39;
   public static final int DEEP_BLUE = 21;

   // Grayscale Ramp Options
   public static final int GRAY_LEVEL_1 = 235; // Very Dark Gray
   public static final int GRAY_LEVEL_2 = 240; // Medium Dark Gray
   public static final int GRAY_LEVEL_3 = 245; // Medium Light Gray
   public static final int GRAY_LEVEL_4 = 250; // Very Light Gray
   public static final int SILK_WHITE = 254;   // Soft Off-White

   static boolean colorizedOutput = true;      // By default, color is enabled
   public static boolean boldOutput = false;   // By default, the output is not bold
   private static Terminal terminal;

   public static void setTerminal(Terminal t) {
      terminal = t;
   }

   /**
    * enableColor(): Enable or disable colorized output
    *
    * @param value TRUE or FALSE to enable colorized output
    */
   public static void enableColor(boolean value) {
      colorizedOutput = value;
   }

   /**
    * queryColorEnabled(): Return true if colorized output is configured. False if not.
    *
    * @return Return TRUE or FALSE if colorized output is enabled
    */
   public static boolean queryColorEnabled() {
      return colorizedOutput;
   }

   /**
    * setBoldOutput(): Set the output to be bold (true) or not (false)
    * Default is false
    *
    * @param value Set to true if bolded output is desired
    */
   public static void setBoldOutput(boolean value) {
      boldOutput = value;
   }

   /**
    * returnColorString(): Return the colorized string
    * <p>
    *
    * @param fgColor Foreground Color
    * @param bgColor Background Color
    * @param msg     Message to display
    */
   public static String returnColorString(int fgColor, int bgColor, String msg) {
      AttributedStyle style;

      if (colorizedOutput) {
         // Initialize the style
         if (boldOutput) {
            style = AttributedStyle.DEFAULT.foreground(fgColor).bold();
         } else {
            // Initialize style with Foreground
            style = AttributedStyle.DEFAULT.foreground(fgColor);
         }
         // Apply background if it's not -1 (transparent/default)
         if (bgColor != -1) {
            style = style.background(bgColor);
         }

         // Build the string
         String styledMsg = new AttributedStringBuilder().style(style).append(msg).toAnsi();

         // Return the result
         return styledMsg;

      } else {
         return msg;
      }
   }

   /**
    * printColor(): Print to the console with the provided foreground & background color
    * <p>
    *
    * @param fgColor Foreground Color
    * @param bgColor Background Color
    * @param msg     Message to display
    */
   public static void printColor(int fgColor, int bgColor, String msg) {
      if (terminal != null && colorizedOutput) {
         String styledMsg = returnColorString(fgColor, bgColor, msg);
         terminal.writer().print(styledMsg);
         terminal.flush();

      } else {
         print(msg);
      }
   }

   /**
    * printColor(): Overloaded. Just provide a foreground color
    *
    * @param fgColor Foreground Color
    * @param msg     Message to display
    */
   public static void printColor(int fgColor, String msg) {
      printColor(fgColor, -1, msg);
   }

   /**
    * printColorln(): Print to the console with the provided foreground color
    * <p>
    *
    * @param fgColor Foreground Color
    * @param bgColor Background Color
    * @param msg     Message to display
    */
   public static void printColorln(int fgColor, int bgColor, String msg) {
      printColor(fgColor, bgColor, msg + "\n");
   }

   /**
    * printColorln(): Overloaded. Added background parameter
    *
    * @param fgColor Foreground Color
    * @param msg     Message to display
    */
   public static void printColorln(int fgColor, String msg) {
      printColor(fgColor, -1, msg + "\n");
   }

   /**
    * println(): Prints text to the terminal. Strips ANSI color codes if colorizedOutput is false.
    *
    * @param msg Message to display (can contain pre-existing ANSI rows)
    */
   public static void println(String msg) {
      String finalMsg = msg;

      // Strip ANSI escape characters if colors are globally toggled off
      if (!colorizedOutput && msg != null) {
         finalMsg = msg.replaceAll("\u001B\\[[;\\d]*m", "");
      }

      if (terminal != null) {
         terminal.writer().println(finalMsg);
         terminal.flush();
      } else {
         System.out.println(finalMsg);
      }
   }

   /**
    * print(): Prints text to the terminal stream. Strips ANSI color codes if colorizedOutput is false.
    *
    * @param msg Message to display (can contain pre-existing ANSI rows)
    */
   public static void print(String msg) {
      String finalMsg = msg;

      // Strip ANSI escape characters if colors are globally toggled off
      if (!colorizedOutput && msg != null) {
         finalMsg = msg.replaceAll("\u001B\\[[;\\d]*m", "");
      }

      if (terminal != null) {
         terminal.writer().print(finalMsg);
         terminal.flush();
      } else {
         System.out.print(finalMsg);
      }
   }

   /**
    * fatalError(): Print the provided string in RED and exit the program with the error code given
    *
    * @param msg       Message to display
    * @param errorCode Error code to return
    */
   public static void fatalError(String msg, int errorCode) {
      Output.printColorln(RED, "\nFATAL ERROR: " + msg);
      System.exit(errorCode);
   }

   /**
    * debugPrintln(): Print the provided text in RED with the preface of DEBUG: with a newline
    *
    * @param msg Message to display
    */
   public static void debugPrintln(String msg) {
      debugPrint(msg + "\n");
   }

   /**
    * debugPrint(): Print the provided text in RED with the preface of DEBUG: and no new line at the end
    *
    * @param msg Message to display
    */
   public static void debugPrint(String msg) {
      if (Debug.query()) {
         Output.printColor(RED, "DEBUG:  " + msg);
      }
   }

   /**
    * clearScreen(): Clears the screen
    */
   public static void clearScreen() {
      if (terminal == null) return;

      // This looks up the OS-specific "clear" command (like 'cls' or 'clear')
      terminal.puts(Capability.clear_screen);
      terminal.puts(Capability.cursor_home);

      // I had issues with clearing the screen in Linux. This is a failsafe.
      // \033[2J = Clear entire screen
      // \033[H  = Move cursor to home (0,0)
      terminal.writer().print("\033[H\033[2J");

      // Always flush to ensure the command is sent to the screen immediately
      terminal.flush();
   }

   /**
    * Ansi256Test(): Simple printout of colors to test jAnsi 256 on terminals and exit
    */
   public static void Ansi256Test() {
      // Local flag to track if we had to create a temporary terminal instance
      boolean createdTemporaryTerminal = false;

      // If a terminal hasn't been configured yet, spin one up right now
      if (terminal == null) {
         try {
            terminal = TerminalBuilder.builder().system(true).build();
            createdTemporaryTerminal = true;
         } catch (Exception e) {
            // If we completely fail to initialize a terminal, fallback to plain text printing
            System.out.println("Could not initialize a terminal for the color test: " + e.getMessage());
         }
      }

      // Test Foregrounds
      for (int index = 0; index < 256; index++) {
         Output.printColor(index, String.format("%4d", index));
         if ((index + 1) % 16 == 0) Output.println("");
      }

      Output.println("\n");

      // Test Backgrounds
      for (int index = 0; index < 256; index++) {
         Output.printColor(BLACK, index, String.format("%4d", index));
         if ((index + 1) % 16 == 0) Output.println("");
      }

      // Clean up after ourselves if we created a temporary terminal instance
      if (createdTemporaryTerminal) {
         try {
            terminal.close();
         } catch (Exception ignored) {
            // Ignore closure issues on a test run exit
         }
         terminal = null;
      }
   }
}
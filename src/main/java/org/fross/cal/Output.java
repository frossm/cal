/******************************************************************************
 * cal.java
 * 
 * A simple console based calculator generator
 * 
 *  Written by Michael Fross.  Copyright 2011-2019.  All rights reserved.
 *  
 *  License: MIT License / https://opensource.org/licenses/MIT
 *  Please see included LICENSE.txt file for additional details
 *           
 ******************************************************************************/
/* Leverages the JCDP Color library:  
 *   https://github.com/dialex/JCDP
 *   http://dialex.github.io/JCDP/javadoc/
 *   <!-- https://mvnrepository.com/artifact/com.diogonunes/JCDP -->
*/

package org.fross.cal;

import static org.fusesource.jansi.Ansi.*;
import org.fusesource.jansi.Ansi;

public class Output {
	/**
	 * printcolorln(): Print to the console with the provided foreground color
	 * 
	 * Allowable colors are:
	 * - Ansi.Color.BLACK
	 * - Ansi.Color.RED
	 * - Ansi.Color.GREEN
	 * - Ansi.Color.YELLOW
	 * - Ansi.Color.BLUE
	 * - Ansi.Color.MAGENTA
	 * - Ansi.Color.CYAN
	 * - Ansi.Color.WHITE
	 * - Ansi.Color.DEFAULT
	 * 
	 * @param Color
	 * @param msg
	 */
	public static void printcolorln(Ansi.Color clr, String msg) {
		System.out.println(ansi().a(Attribute.INTENSITY_BOLD).fg(clr).a(msg).reset());
	}

	/**
	 * printcolor(): Print to the console without a newline
	 * 
	 * @param Color
	 * @param msg
	 */
	public static void printcolor(Ansi.Color clr, String msg) {
		System.out.print(ansi().a(Attribute.INTENSITY_BOLD).fg(clr).a(msg).reset());
	}

	/**
	 * println(): Basic System.out.println call. It's here so out text output can go
	 * through this function.
	 * 
	 * @param msg
	 */
	public static void println(String msg) {
		System.out.println(msg);
	}

	/**
	 * print(): Basic System.out.println call. It's here so out text output can go
	 * through this function.
	 * 
	 * @param msg
	 */
	public static void print(String msg) {
		System.out.print(msg);
	}

	/**
	 * fatalerror(): Print the provided string in RED and exit the program with the
	 * error code given
	 * 
	 * @param msg
	 * @param errorcode
	 */
	public static void fatalerror(String msg, int errorcode) {
		Output.printcolorln(Ansi.Color.RED, "\nFATAL ERROR: " + msg);
		System.exit(errorcode);
	}

}
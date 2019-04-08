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
/* Leverages the JCDP Color library:  
 *   https://github.com/dialex/JCDP
 *   http://dialex.github.io/JCDP/javadoc/
 *   <!-- https://mvnrepository.com/artifact/com.diogonunes/JCDP -->
*/

package org.fross.cal;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.FColor;

public class Output {
	/**
	 * printcolorln(): Print to the console with the provided foreground color
	 * Acceptable ColorNames: FColor.BLUE, FColor.CYAN, FColor.GREEN,
	 * FColor.MAGENTA, FColor.NONE, FColor.RED, FColor.WHITE, FColor.YELLOW
	 * 
	 * @param Color
	 * @param msg
	 */
	public static void printcolorln(FColor clr, String msg) {
		ColoredPrinter cp = new ColoredPrinter.Builder(1, false).foreground(clr).build();
		cp.setAttribute(Attribute.LIGHT);
		cp.println(msg);
		cp.clear();
	}

	/**
	 * printcolor(): Print to the console with NoNewLine. The provided foreground
	 * color Acceptable ColorNames: FColor.BLUE, FColor.CYAN, FColor.GREEN,
	 * FColor.MAGENTA, FColor.NONE, FColor.RED, FColor.WHITE, FColor.YELLOW
	 * 
	 * @param Color
	 * @param msg
	 */
	public static void printcolor(FColor clr, String msg) {
		ColoredPrinter cp = new ColoredPrinter.Builder(1, false).foreground(clr).build();
		cp.setAttribute(Attribute.LIGHT);
		cp.print(msg);
		cp.clear();
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
		Output.printcolorln(FColor.RED, "\nFATAL ERROR: " + msg);
		System.exit(errorcode);
	}

}
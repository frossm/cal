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
package org.fross.cal;

import com.diogonunes.jcdp.color.api.Ansi.FColor;

/**
 * Debug contains static methods to maintain the debug state and display
 * messages when enabled.
 * 
 * @author michael.d.fross
 *
 */
public class Debug {
	// Class Variables
	private static boolean clDebug = false;

	/**
	 * Query(): Query current state of this object's debug setting
	 * 
	 * @return
	 */
	public static boolean query() {
		return clDebug;
	}

	/**
	 * Enable(): Turn debugging on for this object
	 */
	public static void enable() {
		clDebug = true;
	}

	/**
	 * Disable(): Disable debugging for this object
	 */
	public static void disable() {
		clDebug = false;
	}

	/**
	 * Print(): Print the output of the String if debugging is enabled. It displays
	 * in RED using the output module.
	 * 
	 * @param msg
	 */
	public static void println(String msg) {
		if (clDebug == true) {
			Output.printcolorln(FColor.RED, "DEBUG:  " + msg);
		}
	}
}
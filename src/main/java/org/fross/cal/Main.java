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
	public static final String VERSION = "2019-02.01";

	/**
	 * Main(): Start of program and holds main command loop
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int optionEntry;

		// Process Command Line Options and set flags where needed
		Getopt optG = new Getopt("DirSize", args, "Dh?");
		while ((optionEntry = optG.getopt()) != -1) {
			switch (optionEntry) {
			case 'D': // Debug Mode
				Debug.enable();
				break;

			case '?': // Help
			case 'h':
				Help.display();
				System.exit(0);
				break;

			default:
				Output.printcolorln(FColor.RED, "ERROR: Unknown Command Line Option -" + optG.getOptarg() + "'");
				Help.display();
				System.exit(0);
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

		String[] x = { args[optG.getOptind()], args[optG.getOptind() + 1] };
		Calendar.printMonth(x);
	}
}
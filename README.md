<p align="center"> 
    <img width="70%" src ="https://github.com/frossm/cal/blob/master/graphics/cal-Year-4wide.jpg">
</p> 

# cal - The Console Calendar Generator

<img align="right" width="200" src="https://github.com/frossm/cal/blob/master/graphics/PostIt.jpg">Cal (or fCal on SNAP) is a command line utility that will output a calendar for the desired month/year.  It's simple and accurate with a few useful features.

By default, Cal will display the current year.  You can provide a month and year, a month, or just a year.  You can also change the number of calendars per row in your display with the `-n` switch.  See the examples below for a more through description.

## Command Line Options

Option   | Description
---------| -----------
-n #     |Set the number of calendars per row when displaying a year.  The default is 3.
-D       |Start in Debug Mode which will display additional debugging data. Normally not used.
-v       |Display the current version and copyright of the program and exit
-h or -? |Display the help page


## Parameters

Parameters are the arguments provided to the program.  They do not include the options above.

Parameter |Description
----------|-----------
|Year      |Display the provided year|
|Month     |Display the current month in the current year|
|Month Year|Display the current month in the provided year|
|          |If no parameter is given, display the current year|


## Execution

This is a java program that is all bundled into a single jar file.  To run a jar file on the command line, simply type:

    java -jar cal.jar
  
 Please note that cal.jar must be in your path.


## Examples

Command|Result
---|---|
`java -jar cal.jar`        | Display the current year
`java -jar cal.jar -n 4`   | Display the current year - 4 months per row
`java -jar cal.jar 9`      | Display September of the current year
`java -jar cal.jar 2022`   | Display the entire year 2022
`java -jar cal.jar 9 2022` | Display September of 2022
`java -jar cal.jar -D 6`   | Display June of current year in debug mode
`java -jar cal.jar -h`     | Show this help information

## SNAP

[![fcal](https://snapcraft.io//fcal/badge.svg)](https://snapcraft.io/fcal)

I would encourage anyone with a supported Linux platform to use snap.  See [Snapcraft Homepage](https://snapcraft.io) for more information. You can download, install, and keep Cal up to date automatically by installing the snap via :

`sudo snap install fcal`  (Assuming snap is installed)

I had to call it `fcal` as `cal` was already taken.

This will install the application into a sandbox where it is separate from other applications.  I do want to look at packaging it via Flatpak as well, but my understanding is that Maven is not well supported.  However, I need to do more investigation.

[![Get it from the Snap Store](https://snapcraft.io/static/images/badges/en/snap-store-black.svg)](https://snapcraft.io/fcal)

## References

I was able to use several web pages for sources of formulas for the program.  These were very helpful and I'd like to thank their authors for taking the time to record them publicly.

Determine how to calculate the first day of a month given the month and year:
* https://www.tondering.dk/claus/cal/chrweek.php#calcdow
* http://www.cplusplus.com/forum/general/174165/

Calculate if the year provided is a leap year:
* https://www.wikihow.com/Calculate-Leap-Years

## License 
[The MIT License](https://opensource.org/licenses/MIT)

Copyright (C) 2019 by Michael Fross

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

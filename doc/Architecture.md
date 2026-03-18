# Technical Architecture: Cal (v4.0.0)

## 1. System Overview
**Cal** is a modular CLI calendar utility built on **Java 21** and **JLine 3**. The architecture follows a "Separation of Concerns" model, where data calculation, visual styling, and terminal rendering are decoupled.



---

## 2. Core Components

### **A. ColorSettings.java (The Theme Engine)**
This component handles the "Look and Feel" of the application.
* **Persistence:** It wraps `java.util.prefs.Preferences` to store user-defined colors (e.g., `month=CYAN`) in the OS-native registry or plist.
* **Self-Healing:** It is designed to be "zero-config." If the preference keys are missing (first run or accidental deletion), the `getStyle()` method detects the null state and automatically populates the registry with hardcoded defaults.
* **Abstraction:** It hides JLine's complex `AttributedStyle` logic from the rest of the app. Other classes simply ask for a "month" style and receive a ready-to-use object.

### **B. MonthBlock.java (The Data Factory)**
This class is responsible for the mathematical layout of a single 31-day grid.
* **Grid Calculation:** It uses `java.time.YearMonth` to determine the starting day of the week and the total days in the month.
* **The 20-Character Constraint:** To ensure horizontal alignment in the year view, every `MonthBlock` is guaranteed to return exactly 8 lines of text, each exactly 20 characters wide.
* **Styling:** It iterates through the days, applying different `AttributedStyles` for "Today," "Holidays," and "Regular Days" by querying `ColorSettings`.



### **C. CalendarView.java (The Layout Engine)**
This is the "Stitching" engine that handles the multi-column year view.
* **Horizontal Stitching:** Since terminal output is sequential (top-to-bottom), printing months side-by-side requires interleaving. `CalendarView` takes the $N^{th}$ line of multiple `MonthBlocks` and joins them into a single "Visual Row."
* **Terminal Abstraction:** It uses the JLine `Terminal` and `PrintWriter` to ensure that ANSI escape codes are handled correctly across different operating systems (Windows, macOS, Linux).

---

## 3. The Rendering Pipeline
When a user requests a full year view (`cal -y`), the data flows through the following pipeline:

1.  **Request:** `Main` identifies the year and column count (default 3).
2.  **Creation:** `CalendarView` instantiates 12 `MonthBlocks`.
3.  **Coloring:** Each `MonthBlock` fetches `AttributedStyles` from `ColorSettings`.
4.  **Buffering:** `MonthBlock` returns 8 `AttributedStrings` (text with embedded style metadata).
5.  **Stitching:** `CalendarView` joins these strings: `[Jan.line(n)] + [Gap] + [Feb.line(n)] + [Gap] + [Mar.line(n)]`.
6.  **Emission:** The joined string is sent to the JLine `Terminal`, which translates the `AttributedStyle` into the specific ANSI escape codes required by the user's terminal emulator.



---

## 4. Key Design Patterns
* **Singleton/Static Utility:** `ColorSettings` acts as a global provider for styles.
* **Factory Pattern:** `MonthBlock` acts as a factory for formatted calendar rows.
* **Strategy Pattern:** The use of JLine's `AttributedStyle` allows the app to change colors without changing the underlying calendar logic.

---

### Study Exercise: Visualizing the "Seams"
To see the **Stitching** logic in action:
1.  Open `CalendarView.java`.
2.  Find the loop where it joins the lines from the `chunk`.
3.  Change the `rowBuilder.append("   ")` (the three spaces) to `rowBuilder.append(" | ")`.
4.  Run the app. You will see how the 3 separate "Blocks" are physically aligned next to each other.
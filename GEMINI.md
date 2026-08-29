# Gemini CLI Developer Context & Instructions: Cal (v4.0.0)

This document provides foundational context, architectural patterns, and development conventions for **Cal**, a modern command-line calendar utility. Refer to this document during research, implementation, and verification phases.

---

## 1. Project Overview & Purpose

**Cal** (distributed as `fcal` on Snapcraft) is a cross-platform command-line calendar generator written in **Java 21**. It offers dynamic formatting, automatic column layout adaptation, local holidays lookup, and customizable color themes.

### Key Capabilities
- **Flexible Grid Rendering:** Displays custom months, complete years, or multiple columns adjusted to terminal width.
- **Dynamic Color Engine:** ANSI coloring via **JLine 4**, backed by user preference persistence in native OS registries.
- **Localized Holiday Integration:** Downloads and caches global and regional public holidays using the **Nager.Date API**.
- **Self-Healing Preferences:** Automatically initializes and restores default styles/configurations if they are modified or missing in the target registry.

---

## 2. Core Architecture & Components

The application adheres to a decoupled architecture separating calculation, representation, and styling.

```
       [ CommandLineArgs ] / [ Main ]
                   │
                   ▼
           [ CalendarView ] (Stitcher / Layout)
             /          \
            ▼            ▼
     [ MonthBlock ]   [ Holidays ] (Nager.Date API Client)
            │
            ▼
     [ ColorSettings ] (Theme Engine & Preferences Persistence)
```

### A. Main Entry & Command-Line Interface (`Main.java` & `CommandLineArgs.java`)
- **Responsibility:** Configures JLine settings, reads localized resource tokens (like version and inception year from `app.properties`), parses parameters via **JCommander**, and delegates control to the visual engine.
- **Auto-Fitting Columns:** If column count `-n` is omitted, the CLI dynamically queries JLine terminal width (falling back to a default of 80 characters if JLine returns a <= 0 value) to calculate optimal side-by-side columns:
  $$\text{Cols} = \frac{\text{Width} + 3}{23}$$
  (Bounded within a safe range of 1 to 6 columns).

### B. Theme Engine & Registry Persistence (`ColorSettings.java`)
- **Registry Node:** `/org/fross/cal/colors` via `java.util.prefs.Preferences` (Registry on Windows, Plist on macOS, XML file on Linux).
- **Themes & Styling:** Translates raw preference strings/numbers to JLine `AttributedStyle` objects.
- **Self-Healing Mechanics:** Detects missing style keys upon querying and automatically writes the default mapping followed by `prefs.flush()`.
- **Primary Color Keys:**
  - `day`: Color of regular calendar grid days.
  - `dayofweek`: Top header row listing the days of the week.
  - `holhighlight`: Foreground highlight color of holiday numbers.
  - `holtext`: Holiday listing details printed at the footer.
  - `holtitle`: Text/title for the holiday footer.
  - `month`: Title of months.
  - `todayfg` & `todaybg`: Foreground and background color combinations for the current day.

### C. Month Grid Layout Factory (`MonthBlock.java`)
- **Dimension Constraint:** To ensure seamless horizontal year stitching, every `MonthBlock` is mathematically guaranteed to construct exactly **8 lines** of styled output, each exactly **20 characters wide** (padded using JLine's visual string utility `AttributedString.columnLength()`).
- **Highlights:** Applies `today` and `holiday` highlights to specific grid slots during week-by-week calculation.

### D. Horizontal Stitcher (`CalendarView.java`)
- **Stitching Logic:** Since command-line stdout is sequential (top-to-bottom), horizontal grids cannot be printed natively in sequence. `CalendarView` fetches the `8-line` array of styled strings from adjacent month blocks and concatenates the $N^{th}$ line of each into a single unified visual line, spaced with a 3-space buffer gap.

### E. Holiday Integration Client (`Holidays.java`)
- **Endpoint:** `https://date.nager.at/api/v3/publicholidays/{Year}/{CountryCode}`
- **Cache Persistence:** Caches parsed holiday listings under `/org/fross/cal/holidays/{CountryCode}/{Year}` to prevent redundant internet calls.
- **Cache Invalidation:** Cleared via `-c` / `--clear-cache` switch, forcing next runs to fetch fresh listings.

---

## 3. Technology Stack & Environment

- **JDK Version:** Java 21
- **Terminal Handling:** JLine 4.4.0 (requires Foreign Function & Memory (FFM) access for native execution)
- **Argument Parsing:** JCommander 1.82
- **JSON Engine:** Gson 2.14.0
- **Testing Framework:** JUnit Jupiter (JUnit 5)
- **Dependency Analytics:** ben-manes Version Plugin (`io.github.ben-manes.versions`)
- **Build Engine:** Gradle Kotlin DSL

---

## 4. Building, Running, and Testing

Always use the Gradle wrapper (`gradlew` or `gradlew.bat`) from the project root to ensure build consistency.

### A. Clean and Build Shadow JAR (Executable Fat JAR)
Compiles, runs unit tests, and outputs a complete, minified, executable JAR under `build/libs/cal.jar`:
```bash
./gradlew shadowJar
```

### B. Launching the App
Execute the built JAR with standard Java tools.
> **Note:** To prevent JLine 4 native access warnings on JVM 21, run with native access enabled:
```bash
java --enable-native-access=ALL-UNNAMED -jar build/libs/cal.jar
```

### C. Running Unit Tests
Verifies the complete suite of tests in the `src/test` directory:
```bash
./gradlew test
```

### D. Finding Outdated Dependencies
Checks for available updates to Gradle plugins and dependencies:
```bash
./gradlew dependencyUpdates
```

---

## 5. Development & Engineering Conventions

All subsequent modifications, enhancements, or bug fixes must adhere strictly to these principles:

1. **Strict File Headers:**
   All new or modified Java files and build configurations must maintain the standardized multi-line copyright header block:
   ```java
   /*--------------------------------------------------------------------------------------
    * Cal - A command line calendar utility
    *
    * Copyright (c) 2018-2026 Michael Fross
    * ...
    * --------------------------------------------------------------------------------------*/
   ```
2. **Terminal Abstraction Guardrails:**
   Do **not** output raw ANSI escape codes using standard `System.out.println()`. Always route styled visual strings through the `Output` utility and leverage `ColorSettings` styles. Ensure that JLine `AttributedStringBuilder` or `AttributedString` is used to count column characters accurately (`columnLength()`).
3. **Robust Safe Preferences Manipulation:**
   Any preference modification or reading must be accompanied by fallback checks to ensure configurations remain self-healing and zero-config on first launch.
4. **Unit Test Continuity:**
   Any code updates must maintain 100% test coverage. Write corresponding test cases in `src/test/java` for any modified or introduced features. Tests targeting Preference actions must clean up after themselves or isolate nodes so they do not taint the user's permanent preferences.
5. **Sandboxed Compatibility Guard:**
   Always handle environment restrictions. In sandboxed systems like snaps, JLine might fail to query `/dev/tty`. Main must continue to run gracefully under a degraded "dumb terminal" standard with colors outputted via fallback codes. Mute verbose JLine logging under snap contexts.

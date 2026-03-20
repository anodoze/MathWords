# MathWords - Project Handoff Doc

## Concept
SRS-based mental arithmetic trainer. Models common operations as "vocabulary" to compensate for dyscalculia. Designed for flip phone / T9 use (Kyocera 902KC target hardware).

## Tech Stack
- Language: Kotlin
- UI: Jetpack Compose
- DB: Room 2.8.4 (SQLite)
- Architecture: MVVM
- Build: AGP 9.1.0, KSP 2.2.10-2.0.2
- Package: io.github.anodoze.mathwords

## App Structure
- Home Screen
  - choose from available operations to practice, or go to Settings
  - "back" button returns to home screen from any sub-screen

## Core Features
- 4 operations currently tracked: Addition, Subtraction, Multiplication, Division
- Ordered pairs tracked separately (8+13 ≠ 13+8)
- 0-99 for both operands
- Wrong answers inject `passingThresholdMs * 5` as synthetic response time into rolling average - this was a placeholder, we should put more thought into the exact interval
- Weighted rolling average with decay factor 0.9, window of last 20 answers - possibly shorten window?
- New cards introduced when weak card count drops below maxWeakCards
- Review interval scales with speed: ratio of (passingThreshold / rollingAvg) in days
- Review interval clamped: floor 4hrs, ceiling 180 days
- Review interval fuzzed ±20% (capped at ±1 day) to prevent clustering of related cards
- Division answers use sig-fig-based tolerance matching
- D-pad down inserts decimal point for division input (once only)
- UserSettings persistent via SharedPreferences
- Centered around T12 keyboard navigation

## User Settings
- Passing threshold (seconds)
- Max weak cards
- Significant figures (controls division answer tolerance)
- Confirm key (# or *)

## Known Issues
- KSP/AGP warning suppressed via `android.disallowKotlinSourceSets=false` in gradle.properties
- Live Edit error on launch (cosmetic, AGP 9.x bug)

## TODO
- Cleanup pass (dead code, unused functions)
- Wrong answer penalty multiplier needs more thought (currently a placeholder)
- Handle extended pauses (max answer length / passingThreshold multiplier)
- APK signing and release build setup
- Import/export (JSON serialization of cards+answers tables for data preservation across updates)


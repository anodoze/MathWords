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

## Current State
- Data layer complete (Card, Answer, Converters, Database, DAOs)
- Scheduler written
- HomeScreen and QuizScreen composables written
- Seeder written (excludes DIVIDE for now)

## Known Issues / TODOs
- KSP/AGP warning suppressed via `android.disallowKotlinSourceSets=false` in gradle.properties
- Live Edit error on launch (cosmetic, AGP 9.x bug)
- Division cards excluded pending design decision on non-integer results
- Schedule "fuzz" not yet implemented (small random multiplier on reviewIntervalMs)
- UserSettings hardcoded, needs persistent storage
- No settings screen yet
- Progress matrix screen not yet built

## Core Design Decisions
- Ordered pairs tracked separately (8+13 ≠ 13+8)
- 0-99 for both operands, 4 operations (DIVIDE excluded for now)
- Wrong answers inject `passingThresholdMs * 5` as synthetic response time into rolling average
- Weighted rolling average with decay factor 0.9, window of last 20 answers
- New cards introduced when weak card count drops below maxWeakCards
- Review interval scales with speed: ratio of (passingThreshold / rollingAvg) in days
- Review interval clamped: floor 6hrs, ceiling 30 days - we should change this to 6 month max
- Confirm key (* or #) user-configurable, other key = backspace

## User Settings (hardcoded defaults for now)
- passingThresholdMs: 3000f
- maxWeakCards: 10
- confirmKey: '#'
- wrongAnswerPenaltyMultiplier: 5f
- decayFactor: 0.9f

## Next Up
- Persistent UserSettings
- Handle extended pauses (max answer length)
  - user configurable
  - do we need special handling for exiting the app and returning?
- Settings screen
- Progress matrix screen
- Schedule fuzz
- Classy Styling 
- Design decisions for decimals
  - division and other operations will need decimal storage/input
  - user-configurable precision level
  - decide how to handle repeating (3.3333...) type answers - round or cut off?
# Chords & Lyrics TV

Android TV app (Kotlin + Jetpack Compose for TV) for viewing song lyrics with
chords in Hebrew (RTL) and English (LTR). Built automatically into an APK by
GitHub Actions – no Android Studio needed.

## Features
- 10-foot dashboard: sidebar (חיפוש / השירים שלי / הגדרות / עזרה) + featured cards
- Full D-pad support with glowing cyan focus highlights
- Song viewer: chords anchored exactly above lyrics (RTL & LTR), auto-scroll with
  adjustable speed, key transposition (±11 semitones, sharps/flats), font size
- Favorites + settings persisted with DataStore
- MVVM Clean Architecture: `domain` (models, chord engine, use cases) →
  `data` (repositories) → `ui` (Compose screens + ViewModels)

## Getting the APK
1. Push to `main` (or run the workflow manually from the **Actions** tab).
2. Open the finished run → **Artifacts** → download `ChordsTV-debug-apk`
   (or `ChordsTV-release-apk`).
3. Unzip and sideload the `.apk` onto your streamer (e.g. with *Send Files to TV*,
   *Downloader*, or `adb install`).

## Adding songs
Songs live in `app/src/main/java/com/aviad/chordstv/data/source/SampleSongs.kt`
in ChordPro-style inline format:

```
#Chorus
[C]Sail on, [G]sail on, little [Am]paper boat
```

Lines starting with `#` are section headers. To use a real backend, implement
`SongRepository` and swap it in `di/AppContainer.kt`.

## Remote mapping (song screen)
| Key | Action |
|-----|--------|
| OK / Play-Pause | toggle auto-scroll |
| ◀ ▶ (or RW / FF) | scroll speed − / + |
| Prev / Next (or CH− / CH+) | transpose − / + |
| ▲ ▼ | manual scroll |

## Gradle wrapper
The workflow installs Gradle 8.7 and generates `gradlew` on the fly, so the
repository does not need the binary `gradle-wrapper.jar`. If you later clone
the repo on a machine with Gradle installed, run `gradle wrapper --gradle-version 8.7`
and commit the generated `gradlew`, `gradlew.bat` and `gradle/wrapper/*`.

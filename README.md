# Chords & Lyrics TV

Android TV app (Kotlin + Jetpack Compose for TV) for viewing song lyrics with
chords in Hebrew (RTL) and English (LTR). Built automatically into an APK by
GitHub Actions – no Android Studio needed.

## Features
- 10-foot dashboard: sidebar (חיפוש / השירים שלי / הגדרות / עזרה) + featured cards
- Full D-pad support with glowing cyan focus highlights
- **Catalogue search** – searches the built-in samples plus your own online
  catalogue (JSON file you host, e.g. in this repo)
- **Web search** – opens Tab4U / Tab4U EN / Ultimate Guitar / Google in a
  TV-optimised browser: D-pad pointer, OK to click, auto-scroll with speed
  control, text zoom, and ★ to bookmark a page into "My Songs"
- Song viewer: chords anchored exactly above lyrics (RTL & LTR), auto-scroll with
  adjustable speed, key transposition (±11 semitones, sharps/flats), font size
- Favorites, bookmarks and settings persisted with DataStore
- MVVM Clean Architecture: `domain` (models, chord engine, use cases) →
  `data` (repositories) → `ui` (Compose screens + ViewModels)

## Getting the APK
1. Push to `main` (or run the workflow manually from the **Actions** tab).
2. Open the finished run → **Artifacts** → download `ChordsTV-release-apk`.
3. Unzip and sideload the `.apk` onto your streamer.

## Your own song catalogue
1. Edit `catalog/songs.json` in this repo (add songs in ChordPro format – see the
   two examples). `body` can be an array of lines or one string with `\n`.
2. Set the URL once in `app/src/main/java/com/aviad/chordstv/data/source/CatalogConfig.kt`
   (replace `REPO_NAME` with this repository's name) – or type any URL in
   Settings → "קטלוג שירים משלי". Raw GitHub URL format:
   `https://raw.githubusercontent.com/<user>/<repo>/main/catalog/songs.json`
3. The app downloads the catalogue on start (and on "רענן קטלוג") and caches it
   for offline use. Catalogue songs show up in search, featured cards and favorites.

Song entry:
```json
{ "id": "mashina-001", "title": "…", "artist": "משינה", "language": "he",
  "key": "Am", "tags": ["רוק"], "body": ["#בית", "[Am]שורה ראשונה", "[G]שורה שנייה"] }
```
Lines starting with `#` are section headers.

## Remote mapping
| Screen | Key | Action |
|--------|-----|--------|
| Song | OK / Play-Pause | toggle auto-scroll |
| Song | ◀ ▶ (or RW / FF) | scroll speed − / + |
| Song | Prev / Next (or CH− / CH+) | transpose − / + |
| Song | ▲ ▼ | manual scroll |
| Web | ▲ ▼ ◀ ▶ | move pointer (page scrolls at the edges) |
| Web | OK | click |
| Web | Play-Pause, RW / FF | auto-scroll, speed |
| Web | CH+ / CH− | page up / down |
| Web | Back | previous page, then exit |

## Gradle wrapper
The workflow installs Gradle 8.7 and generates `gradlew` on the fly, so the
repository does not need the binary `gradle-wrapper.jar`.

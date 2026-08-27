package com.aviad.chordstv.domain.model

enum class Language { HEBREW, ENGLISH }

/**
 * A song stored in ChordPro-style inline format:
 *
 *   [Am]Some lyrics [G]go here
 *   #Chorus                   <- lines starting with '#' are section headers
 *
 * Chords are embedded inside square brackets directly before the syllable
 * they should sit above. This keeps chord alignment exact for both LTR and
 * RTL text because the position is anchored to a character, not a column.
 */
data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val language: Language,
    val originalKey: String,
    val body: String,
    val tags: List<String> = emptyList()
) {
    val isRtl: Boolean get() = language == Language.HEBREW
}

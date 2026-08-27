package com.aviad.chordstv.domain.model

/** A piece of a lyric line with an optional chord anchored above its first character. */
data class Segment(
    val chord: String?,
    val text: String
)

sealed class SongLine {
    /** Section header such as "Chorus" / "פזמון". */
    data class Header(val text: String) : SongLine()

    /** A lyric line split into chord-anchored segments. */
    data class Lyric(val segments: List<Segment>) : SongLine() {
        val hasChords: Boolean get() = segments.any { it.chord != null }
    }

    /** Blank spacer line. */
    data object Blank : SongLine()
}

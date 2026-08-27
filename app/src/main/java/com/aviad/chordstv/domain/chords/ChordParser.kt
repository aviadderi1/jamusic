package com.aviad.chordstv.domain.chords

import com.aviad.chordstv.domain.model.Segment
import com.aviad.chordstv.domain.model.SongLine

/**
 * Parses ChordPro-style inline text ("[Am]lyrics [G]lyrics") into structured lines.
 */
object ChordParser {

    private val chordToken = Regex("""\[([^\[\]]+)]""")

    fun parse(body: String): List<SongLine> =
        body.lines().map { raw -> parseLine(raw.trimEnd()) }

    fun parseLine(line: String): SongLine {
        if (line.isBlank()) return SongLine.Blank
        if (line.startsWith("#")) return SongLine.Header(line.removePrefix("#").trim())

        val segments = mutableListOf<Segment>()
        var cursor = 0
        var pendingChord: String? = null

        for (match in chordToken.findAll(line)) {
            val textBefore = line.substring(cursor, match.range.first)
            if (textBefore.isNotEmpty() || pendingChord != null) {
                segments += Segment(pendingChord, textBefore)
            }
            pendingChord = match.groupValues[1].trim()
            cursor = match.range.last + 1
        }
        val tail = line.substring(cursor)
        if (tail.isNotEmpty() || pendingChord != null) {
            segments += Segment(pendingChord, tail)
        }
        if (segments.isEmpty()) segments += Segment(null, line)
        return SongLine.Lyric(segments)
    }
}

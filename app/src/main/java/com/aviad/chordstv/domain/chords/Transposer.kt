package com.aviad.chordstv.domain.chords

/**
 * Transposition engine. Handles roots, accidentals, qualities and slash bass
 * notes, e.g.  Am7 -> Bm7,  F#m/A -> Gm/Bb,  Bbmaj7 -> Cmaj7.
 */
object Transposer {

    private val sharps = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val flats  = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")

    private val noteIndex: Map<String, Int> = buildMap {
        sharps.forEachIndexed { i, n -> put(n, i) }
        flats.forEachIndexed { i, n -> put(n, i) }
        // enharmonic oddities
        put("E#", 5); put("B#", 0); put("Fb", 4); put("Cb", 11)
    }

    private val chordRegex = Regex("""^([A-G](?:#|b)?)([^/]*)(?:/([A-G](?:#|b)?))?$""")

    fun isChord(token: String): Boolean = chordRegex.matches(token.trim())

    /**
     * @param semitones  positive = up, negative = down
     * @param preferFlats choose flat spelling for the transposed notes
     */
    fun transpose(chord: String, semitones: Int, preferFlats: Boolean = false): String {
        val trimmed = chord.trim()
        val m = chordRegex.find(trimmed) ?: return chord
        val (root, quality, bass) = m.destructured
        val newRoot = shift(root, semitones, preferFlats) ?: return chord
        val newBass = if (bass.isNotEmpty()) shift(bass, semitones, preferFlats) ?: bass else ""
        return buildString {
            append(newRoot)
            append(quality)
            if (newBass.isNotEmpty()) append('/').append(newBass)
        }
    }

    fun transposeKey(key: String, semitones: Int, preferFlats: Boolean = false): String =
        transpose(key, semitones, preferFlats)

    private fun shift(note: String, semitones: Int, preferFlats: Boolean): String? {
        val idx = noteIndex[note] ?: return null
        val shifted = Math.floorMod(idx + semitones, 12)
        return if (preferFlats) flats[shifted] else sharps[shifted]
    }
}

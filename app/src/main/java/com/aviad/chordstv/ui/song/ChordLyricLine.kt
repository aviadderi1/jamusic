package com.aviad.chordstv.ui.song

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.aviad.chordstv.domain.model.SongLine
import com.aviad.chordstv.ui.theme.ChordColor
import com.aviad.chordstv.ui.theme.HeaderColor
import com.aviad.chordstv.ui.theme.MonoFamily
import com.aviad.chordstv.ui.theme.TextPrimary

/**
 * Renders one lyric line with chords anchored EXACTLY above the character they
 * were attached to. Each segment is a two-row column (chord / text); the row of
 * columns flows RTL for Hebrew and LTR for English, so alignment is exact in
 * both directions without relying on column counting or bidi reordering.
 */
@Composable
fun ChordLyricLine(
    line: SongLine.Lyric,
    isRtl: Boolean,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    val direction = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val lyricStyle = TextStyle(
        fontFamily = MonoFamily,
        fontSize = fontSize,
        color = TextPrimary,
        textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr
    )
    val chordStyle = TextStyle(
        fontFamily = MonoFamily,
        fontSize = fontSize * 0.92f,
        fontWeight = FontWeight.Bold,
        color = ChordColor,
        textDirection = TextDirection.Ltr   // chord names are always Latin
    )

    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        FlowRow(modifier = modifier.fillMaxWidth()) {
            line.segments.forEach { seg ->
                Column(horizontalAlignment = Alignment.Start) {
                    if (line.hasChords) {
                        Text(
                            text = seg.chord ?: "",
                            style = chordStyle,
                            softWrap = false,
                            maxLines = 1,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                    Text(
                        text = seg.text.ifEmpty { " " },
                        style = lyricStyle,
                        softWrap = false,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeaderLine(text: String, isRtl: Boolean, fontSize: TextUnit, modifier: Modifier = Modifier) {
    val direction = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Column(modifier = modifier.fillMaxWidth()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "[$text]",
                style = TextStyle(
                    fontSize = fontSize * 0.85f,
                    fontWeight = FontWeight.Bold,
                    color = HeaderColor,
                    textDirection = TextDirection.Content
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun BlankLine(fontSize: TextUnit) {
    Spacer(Modifier.height((fontSize.value * 0.8f).dp))
}

/** Convenience so callers can pass an Int sp size. */
fun Int.spUnit(): TextUnit = this.sp

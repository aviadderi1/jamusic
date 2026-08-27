package com.aviad.chordstv.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.aviad.chordstv.domain.model.Language
import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.ui.theme.AccentCyan
import com.aviad.chordstv.ui.theme.BgSurface
import com.aviad.chordstv.ui.theme.FavoriteColor
import com.aviad.chordstv.ui.theme.TextSecondary

/** Featured / search-result card. Generates a colourful cover from the title so no artwork is needed. */
@Composable
fun SongCard(
    song: Song,
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        onClick = onClick,
        modifier = modifier.width(210.dp),
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = BgSurface,
            focusedContainerColor = BgSurface,
            pressedContainerColor = BgSurface
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(border = BorderStroke(3.dp, AccentCyan), shape = shape)
        ),
        glow = ClickableSurfaceDefaults.glow(
            focusedGlow = Glow(elevationColor = AccentCyan, elevation = 18.dp)
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.08f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            CoverArt(song)
            Spacer(Modifier.height(12.dp))
            CompositionLocalProvider(
                LocalLayoutDirection provides if (song.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(8.dp))
            LanguageChip(song.language, isFavorite)
        }
    }
}

@Composable
private fun CoverArt(song: Song) {
    val palette = listOf(
        Color(0xFF0E7490), Color(0xFF7C3AED), Color(0xFFBE185D),
        Color(0xFF047857), Color(0xFFB45309), Color(0xFF1D4ED8)
    )
    val base = palette[Math.floorMod(song.id.hashCode(), palette.size)]
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(base, base.copy(alpha = 0.45f), Color(0xFF0F172A)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = song.title.take(1).uppercase(),
            fontSize = 64.sp,
            fontWeight = FontWeight.Black,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
        Text(
            text = song.originalKey,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun LanguageChip(language: Language, isFavorite: Boolean) {
    val label = if (language == Language.HEBREW) "עברית" else "English"
    Text(
        text = if (isFavorite) "$label  ★" else label,
        style = MaterialTheme.typography.bodyMedium,
        color = if (isFavorite) FavoriteColor else AccentCyan,
        maxLines = 1
    )
}

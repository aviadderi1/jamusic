package com.aviad.chordstv.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val TvColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = BgDark,
    secondary = AccentBlue,
    onSecondary = BgDark,
    background = BgDark,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = BgSurfaceHigh,
    onSurfaceVariant = TextSecondary,
    border = AccentCyanDim
)

@Composable
fun ChordsTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TvColorScheme,
        typography = TvTypography
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
        ) {
            content()
        }
    }
}

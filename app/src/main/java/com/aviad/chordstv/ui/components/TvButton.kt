package com.aviad.chordstv.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.aviad.chordstv.ui.theme.AccentCyan
import com.aviad.chordstv.ui.theme.BgSurface
import com.aviad.chordstv.ui.theme.BgSurfaceHigh
import com.aviad.chordstv.ui.theme.TextPrimary

/**
 * Pill-shaped, D-pad friendly button with a glowing cyan focus ring.
 * Built on tv-material [Surface] so focus scale / border / glow are handled natively.
 */
@Composable
fun TvPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    selected: Boolean = false,
    contentColor: Color = TextPrimary
) {
    val shape = RoundedCornerShape(50)
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (selected) BgSurfaceHigh else BgSurface,
            contentColor = contentColor,
            focusedContainerColor = AccentCyan.copy(alpha = 0.18f),
            focusedContentColor = AccentCyan,
            pressedContainerColor = AccentCyan.copy(alpha = 0.3f),
            pressedContentColor = AccentCyan
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(2.dp, if (selected) AccentCyan.copy(alpha = 0.5f) else Color.Transparent),
                shape = shape
            ),
            focusedBorder = Border(border = BorderStroke(3.dp, AccentCyan), shape = shape)
        ),
        glow = ClickableSurfaceDefaults.glow(
            focusedGlow = Glow(elevationColor = AccentCyan, elevation = 14.dp)
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.06f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
                Spacer(Modifier.width(10.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge, maxLines = 1)
        }
    }
}

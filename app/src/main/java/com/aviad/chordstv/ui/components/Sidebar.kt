package com.aviad.chordstv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aviad.chordstv.R
import com.aviad.chordstv.ui.theme.AccentCyan
import com.aviad.chordstv.ui.theme.BgSurface

enum class NavItem(val labelRes: Int, val icon: ImageVector) {
    SEARCH(R.string.nav_search, Icons.Default.Search),
    MY_SONGS(R.string.nav_my_songs, Icons.Default.Favorite),
    SETTINGS(R.string.nav_settings, Icons.Default.Settings),
    HELP(R.string.nav_help, Icons.Default.Info)
}

/** Left-hand navigation rail. Hebrew labels are laid out RTL inside the rail. */
@Composable
fun Sidebar(
    selected: NavItem,
    onSelect: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(BgSurface.copy(alpha = 0.55f))
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(Modifier.height(56.dp))

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                NavItem.entries.forEach { item ->
                    TvPillButton(
                        text = stringResource(item.labelRes),
                        icon = item.icon,
                        selected = item == selected,
                        onClick = { onSelect(item) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

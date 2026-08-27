package com.aviad.chordstv.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aviad.chordstv.R
import com.aviad.chordstv.domain.repository.UserPreferences
import com.aviad.chordstv.ui.components.TvPillButton
import com.aviad.chordstv.ui.theme.AccentCyan

@Composable
fun SettingsPane(
    prefs: UserPreferences,
    onFontChange: (Int) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onPreferFlats: (Boolean) -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))

            StepperRow(
                label = stringResource(R.string.settings_font_size),
                value = "${prefs.defaultFontSp} sp",
                onMinus = { onFontChange(prefs.defaultFontSp - 2) },
                onPlus = { onFontChange(prefs.defaultFontSp + 2) }
            )
            StepperRow(
                label = stringResource(R.string.settings_scroll_speed),
                value = "${prefs.defaultScrollSpeed} / 10",
                onMinus = { onSpeedChange(prefs.defaultScrollSpeed - 1) },
                onPlus = { onSpeedChange(prefs.defaultScrollSpeed + 1) }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.settings_prefer_flats),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.width(420.dp)
                )
                TvPillButton(
                    text = stringResource(R.string.settings_sharps),
                    selected = !prefs.preferFlats,
                    onClick = { onPreferFlats(false) }
                )
                Spacer(Modifier.width(16.dp))
                TvPillButton(
                    text = stringResource(R.string.settings_flats),
                    selected = prefs.preferFlats,
                    onClick = { onPreferFlats(true) }
                )
            }
        }
    }
}

@Composable
private fun StepperRow(label: String, value: String, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.width(420.dp)
        )
        TvPillButton(text = "−", onClick = onMinus)
        Spacer(Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = AccentCyan,
            modifier = Modifier.width(120.dp)
        )
        Spacer(Modifier.width(16.dp))
        TvPillButton(text = "+", onClick = onPlus)
    }
}

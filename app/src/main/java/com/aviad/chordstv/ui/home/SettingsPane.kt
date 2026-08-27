package com.aviad.chordstv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aviad.chordstv.R
import com.aviad.chordstv.data.repository.CatalogStatus
import com.aviad.chordstv.data.source.CatalogConfig
import com.aviad.chordstv.domain.repository.UserPreferences
import com.aviad.chordstv.ui.components.TvPillButton
import com.aviad.chordstv.ui.theme.AccentCyan
import com.aviad.chordstv.ui.theme.BgSurface
import com.aviad.chordstv.ui.theme.TextPrimary
import com.aviad.chordstv.ui.theme.TextSecondary

@Composable
fun SettingsPane(
    prefs: UserPreferences,
    catalogStatus: CatalogStatus,
    onFontChange: (Int) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onPreferFlats: (Boolean) -> Unit,
    onCatalogUrlChange: (String) -> Unit,
    onRefreshCatalog: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )

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

            // ---- Catalogue ----
            Text(
                text = stringResource(R.string.settings_catalog_title),
                style = MaterialTheme.typography.titleLarge,
                color = AccentCyan,
                modifier = Modifier.fillMaxWidth()
            )
            CatalogUrlField(current = prefs.catalogUrlOverride, onCommit = onCatalogUrlChange)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TvPillButton(text = stringResource(R.string.settings_catalog_refresh), onClick = onRefreshCatalog)
                Text(
                    text = when (catalogStatus) {
                        CatalogStatus.Idle -> stringResource(R.string.catalog_idle)
                        CatalogStatus.Loading -> stringResource(R.string.catalog_loading)
                        is CatalogStatus.Loaded -> stringResource(R.string.catalog_loaded, catalogStatus.count) +
                            if (catalogStatus.fromCache) " (cache)" else ""
                        is CatalogStatus.Error -> stringResource(R.string.catalog_error) + ": " + catalogStatus.message
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (catalogStatus is CatalogStatus.Error) MaterialTheme.colorScheme.error else TextSecondary
                )
            }
            Text(
                text = stringResource(R.string.settings_catalog_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** URL entry; committing (IME Done) saves and reloads the catalogue. Empty = built-in default. */
@Composable
private fun CatalogUrlField(current: String, onCommit: (String) -> Unit) {
    var text by remember { mutableStateOf(current) }
    var focused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(current) { if (!focused) text = current }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                textStyle = TextStyle(color = TextPrimary, fontSize = 20.sp, textDirection = TextDirection.Ltr),
                cursorBrush = SolidColor(AccentCyan),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboard?.hide(); onCommit(text) }),
                decorationBox = { inner ->
                    Box {
                        if (text.isEmpty()) {
                            Text(
                                text = CatalogConfig.DEFAULT_CATALOG_URL,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                        inner()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgSurface, RoundedCornerShape(14.dp))
                    .border(
                        if (focused) 3.dp else 1.dp,
                        if (focused) AccentCyan else TextSecondary.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .onFocusChanged { focused = it.isFocused }
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown &&
                            (event.key == Key.DirectionCenter || event.key == Key.Enter || event.key == Key.NumPadEnter)
                        ) { keyboard?.show(); true } else false
                    }
            )
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

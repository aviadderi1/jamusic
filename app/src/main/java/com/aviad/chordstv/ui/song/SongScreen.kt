package com.aviad.chordstv.ui.song

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aviad.chordstv.R
import com.aviad.chordstv.di.AppContainer
import com.aviad.chordstv.domain.model.SongLine
import com.aviad.chordstv.ui.components.TvPillButton
import com.aviad.chordstv.ui.theme.AccentCyan
import com.aviad.chordstv.ui.theme.BgSurface
import com.aviad.chordstv.ui.theme.FavoriteColor
import com.aviad.chordstv.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * Song viewer.
 *
 * Remote mapping (works from anywhere on the screen via media keys, and via
 * D-pad while the lyric area is focused):
 *   OK / Play-Pause          toggle auto-scroll
 *   LEFT / RIGHT, RW / FF    scroll speed − / +
 *   PREV / NEXT, CH− / CH+   transpose − / +
 *   UP / DOWN                manual scroll (UP at the top moves focus to the toolbar)
 */
@Composable
fun SongScreen(
    container: AppContainer,
    songId: String,
    onBack: () -> Unit
) {
    val viewModel: SongViewModel = viewModel(
        key = "song_$songId",
        factory = viewModelFactory { initializer { SongViewModel(container, songId) } }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val lyricsFocus = remember { FocusRequester() }
    var lyricsFocused by remember { mutableStateOf(false) }

    // ---- Auto-scroll engine: frame-synced, speed in dp/second ----
    LaunchedEffect(state.isAutoScrolling, state.scrollSpeed) {
        if (!state.isAutoScrolling) return@LaunchedEffect
        val pxPerSecond = with(density) { (state.scrollSpeed * 14).dp.toPx() }
        var lastFrame = 0L
        while (true) {
            val now = withFrameNanos { it }
            if (lastFrame != 0L) {
                val dt = (now - lastFrame) / 1_000_000_000f
                scrollState.scrollBy(pxPerSecond * dt)
            }
            lastFrame = now
            if (scrollState.value >= scrollState.maxValue) {
                viewModel.setAutoScroll(false)
                break
            }
        }
    }

    // Focus the lyric area once the song is loaded so media keys work immediately.
    LaunchedEffect(state.song) {
        if (state.song != null) runCatching { lyricsFocus.requestFocus() }
    }

    val manualStep = with(density) { 120.dp.toPx() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp, vertical = 24.dp)
            // Global media-key shortcuts for the whole screen
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.MediaPlayPause, Key.MediaPlay, Key.MediaPause -> { viewModel.toggleAutoScroll(); true }
                    Key.MediaFastForward -> { viewModel.speedUp(); true }
                    Key.MediaRewind -> { viewModel.speedDown(); true }
                    Key.MediaNext, Key.ChannelUp -> { viewModel.transposeUp(); true }
                    Key.MediaPrevious, Key.ChannelDown -> { viewModel.transposeDown(); true }
                    else -> false
                }
            }
    ) {
        val song = state.song
        if (song == null) {
            Text(
                text = if (state.notFound) "Song not found" else "…",
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondary
            )
            return@Column
        }

        // ---- Header: title / artist / key ----
        CompositionLocalProvider(
            LocalLayoutDirection provides if (song.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = song.title, style = MaterialTheme.typography.headlineLarge, maxLines = 1)
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                KeyBadge(displayKey = state.displayKey, transpose = state.transpose)
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Toolbar ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TvPillButton(text = stringResource(R.string.back), icon = Icons.Default.ArrowBack, onClick = onBack)
            TvPillButton(
                text = if (state.isAutoScrolling) "⏸ " + stringResource(R.string.song_autoscroll)
                       else "▶ " + stringResource(R.string.song_autoscroll),
                selected = state.isAutoScrolling,
                onClick = viewModel::toggleAutoScroll
            )
            ControlGroup(
                label = stringResource(R.string.song_speed),
                value = state.scrollSpeed.toString(),
                onMinus = viewModel::speedDown,
                onPlus = viewModel::speedUp
            )
            ControlGroup(
                label = stringResource(R.string.song_key),
                value = state.displayKey,
                onMinus = viewModel::transposeDown,
                onPlus = viewModel::transposeUp
            )
            ControlGroup(
                label = stringResource(R.string.song_font),
                value = state.fontSp.toString(),
                onMinus = viewModel::fontDown,
                onPlus = viewModel::fontUp
            )
            Spacer(Modifier.weight(1f))
            TvPillButton(
                text = "★",
                icon = null,
                selected = state.isFavorite,
                contentColor = if (state.isFavorite) FavoriteColor else TextSecondary,
                onClick = viewModel::toggleFavorite
            )
        }

        Spacer(Modifier.height(16.dp))

        // ---- Lyrics / chords area ----
        val fontSize = state.fontSp.spUnit()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(BgSurface.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .border(
                    width = if (lyricsFocused) 3.dp else 1.dp,
                    color = if (lyricsFocused) AccentCyan else TextSecondary.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(20.dp)
                )
                .focusRequester(lyricsFocus)
                .onFocusChanged { lyricsFocused = it.isFocused }
                .onKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                    when (event.key) {
                        Key.DirectionDown -> {
                            scope.launch { scrollState.animateScrollBy(manualStep) }; true
                        }
                        Key.DirectionUp -> {
                            // At the very top let focus escape to the toolbar.
                            if (scrollState.value > 0) {
                                scope.launch { scrollState.animateScrollBy(-manualStep) }; true
                            } else false
                        }
                        Key.DirectionRight -> { viewModel.speedUp(); true }
                        Key.DirectionLeft -> { viewModel.speedDown(); true }
                        Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> { viewModel.toggleAutoScroll(); true }
                        else -> false
                    }
                }
                .focusable()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 32.dp, vertical = 24.dp)
            ) {
                state.lines.forEach { line ->
                    when (line) {
                        is SongLine.Header -> SectionHeaderLine(line.text, song.isRtl, fontSize)
                        is SongLine.Lyric -> ChordLyricLine(line, song.isRtl, fontSize,
                            modifier = Modifier.padding(vertical = 4.dp))
                        SongLine.Blank -> BlankLine(fontSize)
                    }
                }
                // Room so the last line can scroll up to the middle of the screen
                Spacer(Modifier.height(400.dp))
            }
        }

        Spacer(Modifier.height(10.dp))

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = stringResource(R.string.song_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun KeyBadge(displayKey: String, transpose: Int) {
    val sign = if (transpose > 0) "+$transpose" else transpose.toString()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = displayKey,
            style = MaterialTheme.typography.headlineMedium,
            color = AccentCyan,
            fontWeight = FontWeight.Bold
        )
        if (transpose != 0) {
            Text(text = sign, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun ControlGroup(label: String, value: String, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .background(BgSurface.copy(alpha = 0.6f), RoundedCornerShape(50))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        TvPillButton(text = "−", onClick = onMinus)
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp)) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = AccentCyan, maxLines = 1)
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, maxLines = 1)
        }
        TvPillButton(text = "+", onClick = onPlus)
    }
}

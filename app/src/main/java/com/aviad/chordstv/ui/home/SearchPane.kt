package com.aviad.chordstv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aviad.chordstv.R
import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.model.WebSource
import com.aviad.chordstv.ui.components.SongCard
import com.aviad.chordstv.ui.components.TvPillButton
import com.aviad.chordstv.ui.theme.AccentCyan
import com.aviad.chordstv.ui.theme.BgSurface
import com.aviad.chordstv.ui.theme.TextPrimary
import com.aviad.chordstv.ui.theme.TextSecondary

@Composable
fun SearchPane(
    query: String,
    isSearching: Boolean,
    results: List<Song>,
    featured: List<Song>,
    favoriteIds: Set<String>,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onOpenSong: (Song) -> Unit,
    onOpenWeb: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchField(query = query, onQueryChange = onQueryChange, onSubmit = onSubmit)

        Spacer(Modifier.height(18.dp))

        // Where to search: the built-in catalogue, or an external site in the TV browser.
        WebSourceRow(query = query, onOpenWeb = onOpenWeb)

        Spacer(Modifier.height(24.dp))

        if (query.isBlank()) {
            SectionTitle(stringResource(R.string.featured_title))
            Spacer(Modifier.height(20.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)
            ) {
                items(featured, key = { it.id }) { song ->
                    SongCard(song = song, isFavorite = song.id in favoriteIds, onClick = { onOpenSong(song) })
                }
            }
        } else {
            SectionTitle(
                if (isSearching) "…" else stringResource(R.string.results_title) + "  (${results.size})"
            )
            Spacer(Modifier.height(20.dp))
            if (results.isEmpty() && !isSearching) {
                Text(
                    text = stringResource(R.string.no_results),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 230.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    items(results, key = { it.id }) { song ->
                        SongCard(song = song, isFavorite = song.id in favoriteIds, onClick = { onOpenSong(song) })
                    }
                }
            }
        }
    }
}

@Composable
private fun WebSourceRow(query: String, onOpenWeb: (String) -> Unit) {
    val suggested = WebSource.suggestFor(query)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.search_on_web),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            WebSource.entries.forEach { source ->
                TvPillButton(
                    text = source.label,
                    selected = query.isNotBlank() && source == suggested,
                    onClick = {
                        onOpenWeb(if (query.isBlank()) source.homeUrl else source.searchUrl(query))
                    }
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Search box. Pressing OK on the remote opens the on-screen keyboard.
 * The IME "Search" action submits; typing also searches live (debounced).
 */
@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val shape = RoundedCornerShape(50)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (focused) Modifier.shadow(18.dp, shape, ambientColor = AccentCyan, spotColor = AccentCyan) else Modifier)
            .background(BgSurface, shape)
            .border(if (focused) 3.dp else 1.dp, if (focused) AccentCyan else TextSecondary.copy(alpha = 0.4f), shape)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = if (focused) AccentCyan else TextSecondary,
            modifier = Modifier.size(30.dp)
        )
        Spacer(Modifier.width(16.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            if (query.isEmpty()) {
                Text(
                    text = stringResource(R.string.search_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 24.sp,
                    textDirection = TextDirection.Content   // Hebrew types RTL, English LTR
                ),
                cursorBrush = SolidColor(AccentCyan),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused }
                    .onPreviewKeyEvent { event ->
                        // OK / Enter on the remote opens the on-screen keyboard
                        if (event.type == KeyEventType.KeyDown &&
                            (event.key == Key.DirectionCenter || event.key == Key.Enter || event.key == Key.NumPadEnter)
                        ) {
                            keyboard?.show(); true
                        } else false
                    }
            )
        }
    }
}

package com.aviad.chordstv.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aviad.chordstv.R
import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.model.WebBookmark
import com.aviad.chordstv.ui.components.SongCard
import com.aviad.chordstv.ui.components.TvPillButton
import com.aviad.chordstv.ui.theme.TextSecondary

@Composable
fun MySongsPane(
    songs: List<Song>,
    bookmarks: List<WebBookmark>,
    onOpenSong: (Song) -> Unit,
    onOpenWeb: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = stringResource(R.string.my_songs_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            if (songs.isEmpty() && bookmarks.isEmpty()) {
                Text(
                    text = stringResource(R.string.my_songs_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (songs.isNotEmpty()) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = stringResource(R.string.my_songs_catalog),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp)
            ) {
                items(songs, key = { it.id }) { song ->
                    SongCard(song = song, isFavorite = true, onClick = { onOpenSong(song) })
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        if (bookmarks.isNotEmpty()) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = stringResource(R.string.my_songs_web),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    bookmarks.forEach { bm ->
                        TvPillButton(text = bm.title.take(60), onClick = { onOpenWeb(bm.url) })
                    }
                }
            }
        }
    }
}

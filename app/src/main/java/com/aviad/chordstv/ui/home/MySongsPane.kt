package com.aviad.chordstv.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.aviad.chordstv.ui.components.SongCard
import com.aviad.chordstv.ui.theme.TextSecondary

@Composable
fun MySongsPane(songs: List<Song>, onOpenSong: (Song) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = stringResource(R.string.my_songs_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            if (songs.isEmpty()) {
                Text(
                    text = stringResource(R.string.my_songs_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (songs.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 230.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(songs, key = { it.id }) { song ->
                    SongCard(song = song, isFavorite = true, onClick = { onOpenSong(song) })
                }
            }
        }
    }
}

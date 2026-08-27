package com.aviad.chordstv.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aviad.chordstv.di.AppContainer
import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.ui.components.NavItem
import com.aviad.chordstv.ui.components.Sidebar

/**
 * Dashboard: sidebar on the left, active pane on the right.
 * Each pane owns its own D-pad focus; the sidebar is reachable with LEFT.
 */
@Composable
fun HomeScreen(
    container: AppContainer,
    onOpenSong: (Song) -> Unit,
    onOpenWeb: (String) -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = viewModelFactory { initializer { HomeViewModel(container) } }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val catalogStatus by viewModel.catalogStatus.collectAsStateWithLifecycle()

    Row(modifier = Modifier.fillMaxSize()) {
        Sidebar(selected = state.nav, onSelect = viewModel::selectNav)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 32.dp)
        ) {
            when (state.nav) {
                NavItem.SEARCH -> SearchPane(
                    query = state.query,
                    isSearching = state.isSearching,
                    results = state.results,
                    featured = state.featured,
                    favoriteIds = prefs.favoriteIds,
                    onQueryChange = viewModel::onQueryChange,
                    onSubmit = viewModel::submitSearch,
                    onOpenSong = onOpenSong,
                    onOpenWeb = onOpenWeb
                )
                NavItem.MY_SONGS -> MySongsPane(
                    songs = state.favorites,
                    bookmarks = prefs.webBookmarks,
                    onOpenSong = onOpenSong,
                    onOpenWeb = onOpenWeb
                )
                NavItem.SETTINGS -> SettingsPane(
                    prefs = prefs,
                    catalogStatus = catalogStatus,
                    onFontChange = viewModel::setDefaultFont,
                    onSpeedChange = viewModel::setDefaultSpeed,
                    onPreferFlats = viewModel::setPreferFlats,
                    onCatalogUrlChange = viewModel::setCatalogUrl,
                    onRefreshCatalog = viewModel::refreshCatalog
                )
                NavItem.HELP -> HelpPane()
            }
        }
    }
}

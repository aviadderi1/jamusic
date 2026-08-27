package com.aviad.chordstv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aviad.chordstv.data.repository.CatalogStatus
import com.aviad.chordstv.di.AppContainer
import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.repository.UserPreferences
import com.aviad.chordstv.ui.components.NavItem
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val nav: NavItem = NavItem.SEARCH,
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<Song> = emptyList(),
    val featured: List<Song> = emptyList(),
    val favorites: List<Song> = emptyList()
)

@OptIn(FlowPreview::class)
class HomeViewModel(private val container: AppContainer) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    val preferences: StateFlow<UserPreferences> = container.userPreferencesRepository.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences())

    val catalogStatus: StateFlow<CatalogStatus> = container.remoteSongRepository.status

    private val queryFlow = MutableStateFlow("")
    private var searchJob: Job? = null

    init {
        // Featured list follows the catalogue (refreshes when the remote catalogue loads)
        viewModelScope.launch {
            container.remoteSongRepository.status.collect {
                _state.update { it.copy(featured = container.getFeaturedSongs()) }
                if (_state.value.query.isNotBlank()) runSearch(_state.value.query)
            }
        }
        // Debounced search-as-you-type
        viewModelScope.launch {
            queryFlow.debounce(250).distinctUntilChanged().collect { q -> runSearch(q) }
        }
        // Keep the "My Songs" list in sync with favourites
        viewModelScope.launch {
            container.userPreferencesRepository.preferences.collect { prefs ->
                val favs = container.songRepository.getByIds(prefs.favoriteIds)
                _state.update { it.copy(favorites = favs) }
            }
        }
    }

    fun selectNav(item: NavItem) = _state.update { it.copy(nav = item) }

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
        queryFlow.value = q
    }

    fun submitSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch { runSearch(_state.value.query) }
    }

    private suspend fun runSearch(q: String) {
        if (q.isBlank()) {
            _state.update { it.copy(results = emptyList(), isSearching = false) }
            return
        }
        _state.update { it.copy(isSearching = true) }
        val found = container.searchSongs(q)
        _state.update { it.copy(results = found, isSearching = false) }
    }

    fun setDefaultFont(sp: Int) = viewModelScope.launch {
        container.userPreferencesRepository.setDefaultFontSp(sp)
    }

    fun setDefaultSpeed(speed: Int) = viewModelScope.launch {
        container.userPreferencesRepository.setDefaultScrollSpeed(speed)
    }

    fun setPreferFlats(flats: Boolean) = viewModelScope.launch {
        container.userPreferencesRepository.setPreferFlats(flats)
    }

    fun setCatalogUrl(url: String) = viewModelScope.launch {
        container.userPreferencesRepository.setCatalogUrlOverride(url)
        container.refreshCatalog()
    }

    fun refreshCatalog() = container.refreshCatalog()
}

package com.aviad.chordstv.di

import android.content.Context
import com.aviad.chordstv.data.repository.CompositeSongRepository
import com.aviad.chordstv.data.repository.DataStoreUserPreferencesRepository
import com.aviad.chordstv.data.repository.MockSongRepository
import com.aviad.chordstv.data.repository.RemoteSongRepository
import com.aviad.chordstv.data.source.CatalogConfig
import com.aviad.chordstv.domain.repository.SongRepository
import com.aviad.chordstv.domain.repository.UserPreferencesRepository
import com.aviad.chordstv.domain.usecase.GetFeaturedSongsUseCase
import com.aviad.chordstv.domain.usecase.GetSongUseCase
import com.aviad.chordstv.domain.usecase.SearchSongsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Hand-rolled dependency container (Clean Architecture composition root).
 */
class AppContainer(context: Context) {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val userPreferencesRepository: UserPreferencesRepository =
        DataStoreUserPreferencesRepository(context.applicationContext)

    /** Catalogue you host yourself (JSON). URL comes from Settings, else the built-in default. */
    val remoteSongRepository = RemoteSongRepository(
        context = context.applicationContext,
        urlProvider = {
            userPreferencesRepository.preferences.first().catalogUrlOverride
                .ifBlank { CatalogConfig.DEFAULT_CATALOG_URL }
        }
    )

    val songRepository: SongRepository = CompositeSongRepository(
        remote = remoteSongRepository,
        local = MockSongRepository()
    )

    val searchSongs = SearchSongsUseCase(songRepository)
    val getFeaturedSongs = GetFeaturedSongsUseCase(songRepository)
    val getSong = GetSongUseCase(songRepository)

    init {
        // Show cached catalogue immediately, then refresh from the network.
        appScope.launch {
            remoteSongRepository.loadCache()
            remoteSongRepository.refresh()
        }
    }

    fun refreshCatalog() {
        appScope.launch { remoteSongRepository.refresh() }
    }
}

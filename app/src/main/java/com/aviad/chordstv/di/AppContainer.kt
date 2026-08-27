package com.aviad.chordstv.di

import android.content.Context
import com.aviad.chordstv.data.repository.DataStoreUserPreferencesRepository
import com.aviad.chordstv.data.repository.MockSongRepository
import com.aviad.chordstv.domain.repository.SongRepository
import com.aviad.chordstv.domain.repository.UserPreferencesRepository
import com.aviad.chordstv.domain.usecase.GetFeaturedSongsUseCase
import com.aviad.chordstv.domain.usecase.GetSongUseCase
import com.aviad.chordstv.domain.usecase.SearchSongsUseCase

/**
 * Hand-rolled dependency container (Clean Architecture composition root).
 *
 * To plug in a real backend later, replace [MockSongRepository] with a
 * network-backed implementation of [SongRepository] – nothing else changes.
 */
class AppContainer(context: Context) {

    val songRepository: SongRepository = MockSongRepository()

    val userPreferencesRepository: UserPreferencesRepository =
        DataStoreUserPreferencesRepository(context.applicationContext)

    val searchSongs = SearchSongsUseCase(songRepository)
    val getFeaturedSongs = GetFeaturedSongsUseCase(songRepository)
    val getSong = GetSongUseCase(songRepository)
}

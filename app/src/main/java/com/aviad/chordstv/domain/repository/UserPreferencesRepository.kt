package com.aviad.chordstv.domain.repository

import kotlinx.coroutines.flow.Flow

data class UserPreferences(
    val favoriteIds: Set<String> = emptySet(),
    val defaultFontSp: Int = 26,
    val defaultScrollSpeed: Int = 3,   // 1..10
    val preferFlats: Boolean = false
)

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun toggleFavorite(songId: String)
    suspend fun setDefaultFontSp(sp: Int)
    suspend fun setDefaultScrollSpeed(speed: Int)
    suspend fun setPreferFlats(preferFlats: Boolean)
}

package com.aviad.chordstv.domain.repository

import com.aviad.chordstv.domain.model.WebBookmark
import kotlinx.coroutines.flow.Flow

data class UserPreferences(
    val favoriteIds: Set<String> = emptySet(),
    val defaultFontSp: Int = 26,
    val defaultScrollSpeed: Int = 3,   // 1..10
    val preferFlats: Boolean = false,
    /** Empty = use CatalogConfig.DEFAULT_CATALOG_URL */
    val catalogUrlOverride: String = "",
    val webBookmarks: List<WebBookmark> = emptyList(),
    val webTextZoom: Int = 140         // percent
)

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun toggleFavorite(songId: String)
    suspend fun setDefaultFontSp(sp: Int)
    suspend fun setDefaultScrollSpeed(speed: Int)
    suspend fun setPreferFlats(preferFlats: Boolean)
    suspend fun setCatalogUrlOverride(url: String)
    suspend fun toggleWebBookmark(bookmark: WebBookmark)
    suspend fun setWebTextZoom(percent: Int)
}

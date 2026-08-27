package com.aviad.chordstv.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aviad.chordstv.domain.model.WebBookmark
import com.aviad.chordstv.domain.repository.UserPreferences
import com.aviad.chordstv.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chordstv_prefs")

class DataStoreUserPreferencesRepository(private val context: Context) : UserPreferencesRepository {

    private object Keys {
        val FAVORITES = stringSetPreferencesKey("favorite_ids")
        val FONT_SP = intPreferencesKey("default_font_sp")
        val SCROLL_SPEED = intPreferencesKey("default_scroll_speed")
        val PREFER_FLATS = booleanPreferencesKey("prefer_flats")
        val CATALOG_URL = stringPreferencesKey("catalog_url_override")
        val WEB_BOOKMARKS = stringSetPreferencesKey("web_bookmarks")
        val WEB_TEXT_ZOOM = intPreferencesKey("web_text_zoom")
    }

    override val preferences: Flow<UserPreferences> = context.dataStore.data.map { p ->
        UserPreferences(
            favoriteIds = p[Keys.FAVORITES] ?: emptySet(),
            defaultFontSp = p[Keys.FONT_SP] ?: 26,
            defaultScrollSpeed = p[Keys.SCROLL_SPEED] ?: 3,
            preferFlats = p[Keys.PREFER_FLATS] ?: false,
            catalogUrlOverride = p[Keys.CATALOG_URL] ?: "",
            webBookmarks = (p[Keys.WEB_BOOKMARKS] ?: emptySet())
                .mapNotNull { WebBookmark.decode(it) }
                .sortedBy { it.title },
            webTextZoom = p[Keys.WEB_TEXT_ZOOM] ?: 140
        )
    }

    override suspend fun toggleFavorite(songId: String) {
        context.dataStore.edit { p ->
            val current = p[Keys.FAVORITES] ?: emptySet()
            p[Keys.FAVORITES] = if (songId in current) current - songId else current + songId
        }
    }

    override suspend fun setDefaultFontSp(sp: Int) {
        context.dataStore.edit { it[Keys.FONT_SP] = sp.coerceIn(16, 48) }
    }

    override suspend fun setDefaultScrollSpeed(speed: Int) {
        context.dataStore.edit { it[Keys.SCROLL_SPEED] = speed.coerceIn(1, 10) }
    }

    override suspend fun setPreferFlats(preferFlats: Boolean) {
        context.dataStore.edit { it[Keys.PREFER_FLATS] = preferFlats }
    }

    override suspend fun setCatalogUrlOverride(url: String) {
        context.dataStore.edit { it[Keys.CATALOG_URL] = url.trim() }
    }

    override suspend fun toggleWebBookmark(bookmark: WebBookmark) {
        context.dataStore.edit { p ->
            val current = p[Keys.WEB_BOOKMARKS] ?: emptySet()
            val existing = current.firstOrNull { WebBookmark.decode(it)?.url == bookmark.url }
            p[Keys.WEB_BOOKMARKS] = if (existing != null) current - existing else current + bookmark.encode()
        }
    }

    override suspend fun setWebTextZoom(percent: Int) {
        context.dataStore.edit { it[Keys.WEB_TEXT_ZOOM] = percent.coerceIn(80, 250) }
    }
}

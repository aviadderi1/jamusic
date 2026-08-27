package com.aviad.chordstv.data.repository

import android.content.Context
import com.aviad.chordstv.domain.model.Language
import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

sealed class CatalogStatus {
    data object Idle : CatalogStatus()
    data object Loading : CatalogStatus()
    data class Loaded(val count: Int, val fromCache: Boolean) : CatalogStatus()
    data class Error(val message: String) : CatalogStatus()
}

/**
 * Loads a JSON song catalogue from a URL you control (GitHub raw / Pages, Drive,
 * your own server…). The last successful download is cached on disk so the
 * catalogue also works offline.
 *
 * JSON format (body may be a single string with "\n" or an array of lines):
 * {
 *   "songs": [
 *     { "id": "mashina-001", "title": "…", "artist": "…", "language": "he",
 *       "key": "Am", "tags": ["רוק"], "body": ["[Am]שורה", "[G]שורה"] }
 *   ]
 * }
 */
class RemoteSongRepository(
    context: Context,
    private val urlProvider: suspend () -> String
) : SongRepository {

    private val cacheFile = File(context.filesDir, "catalog_cache.json")

    private val _status = MutableStateFlow<CatalogStatus>(CatalogStatus.Idle)
    val status: StateFlow<CatalogStatus> = _status

    @Volatile
    private var songs: List<Song> = emptyList()

    /** Load the on-disk cache (fast, offline) – call once at startup. */
    suspend fun loadCache() = withContext(Dispatchers.IO) {
        if (!cacheFile.exists()) return@withContext
        runCatching { parse(cacheFile.readText()) }.onSuccess {
            songs = it
            _status.value = CatalogStatus.Loaded(it.size, fromCache = true)
        }
    }

    /** Download the catalogue again. Safe to call any time. */
    suspend fun refresh() = withContext(Dispatchers.IO) {
        val url = urlProvider().trim()
        if (url.isBlank() || url.contains("REPO_NAME")) {
            _status.value = CatalogStatus.Error("Catalog URL not configured")
            return@withContext
        }
        _status.value = CatalogStatus.Loading
        runCatching {
            val text = download(url)
            val parsed = parse(text)
            cacheFile.writeText(text)
            parsed
        }.onSuccess {
            songs = it
            _status.value = CatalogStatus.Loaded(it.size, fromCache = false)
        }.onFailure { e ->
            _status.value = CatalogStatus.Error(e.message ?: e.javaClass.simpleName)
        }
    }

    private fun download(url: String): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 15_000
            instanceFollowRedirects = true
            setRequestProperty("Accept", "application/json, text/plain, */*")
            setRequestProperty("Cache-Control", "no-cache")
        }
        try {
            val code = conn.responseCode
            if (code !in 200..299) throw IllegalStateException("HTTP $code")
            return conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }

    private fun parse(text: String): List<Song> {
        val trimmed = text.trim()
        val array: JSONArray = if (trimmed.startsWith("[")) JSONArray(trimmed)
                               else JSONObject(trimmed).getJSONArray("songs")
        val out = ArrayList<Song>(array.length())
        for (i in 0 until array.length()) {
            val o = array.getJSONObject(i)
            val title = o.optString("title").trim()
            if (title.isEmpty()) continue
            val artist = o.optString("artist").trim()
            val bodyValue = o.opt("body")
            val body = when (bodyValue) {
                is JSONArray -> (0 until bodyValue.length()).joinToString("\n") { bodyValue.optString(it) }
                is String -> bodyValue
                else -> ""
            }
            val lang = when (o.optString("language").lowercase()) {
                "he", "heb", "hebrew", "עברית" -> Language.HEBREW
                "en", "eng", "english" -> Language.ENGLISH
                else -> if (title.any { it in '\u0590'..'\u05FF' }) Language.HEBREW else Language.ENGLISH
            }
            val tags = o.optJSONArray("tags")?.let { arr ->
                (0 until arr.length()).map { arr.optString(it) }
            } ?: emptyList()
            out += Song(
                id = o.optString("id").ifBlank { "remote-" + (artist + "-" + title).hashCode().toUInt() },
                title = title,
                artist = artist,
                language = lang,
                originalKey = o.optString("key").ifBlank { "C" },
                body = body,
                tags = tags
            )
        }
        return out
    }

    private val chordToken = Regex("""\[[^\]]*]""")

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.Default) {
        val terms = query.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        songs.filter { song ->
            val haystack = (song.title + " " + song.artist + " " + song.tags.joinToString(" ") + " " +
                chordToken.replace(song.body, "")).lowercase()
            terms.all { haystack.contains(it) }
        }
    }

    override suspend fun featured(): List<Song> = songs
    override suspend fun getById(id: String): Song? = songs.firstOrNull { it.id == id }
    override suspend fun getByIds(ids: Collection<String>): List<Song> = songs.filter { it.id in ids }
}

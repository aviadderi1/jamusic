package com.aviad.chordstv.data.repository

import com.aviad.chordstv.data.source.SampleSongs
import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * In-memory implementation used for the demo. Search covers title, artist,
 * tags and the lyric text itself (chord tokens are stripped first so that
 * searching "am" doesn't match every [Am] chord).
 */
class MockSongRepository(
    private val songs: List<Song> = SampleSongs.all
) : SongRepository {

    private val chordToken = Regex("""\[[^\]]*]""")

    override suspend fun search(query: String): List<Song> = withContext(Dispatchers.Default) {
        delay(120) // simulate latency so the loading state is visible
        val terms = query.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        songs.filter { song ->
            val haystack = buildString {
                append(song.title.lowercase()).append(' ')
                append(song.artist.lowercase()).append(' ')
                append(song.tags.joinToString(" ").lowercase()).append(' ')
                append(chordToken.replace(song.body, "").lowercase())
            }
            terms.all { haystack.contains(it) }
        }
    }

    override suspend fun featured(): List<Song> = songs

    override suspend fun getById(id: String): Song? = songs.firstOrNull { it.id == id }

    override suspend fun getByIds(ids: Collection<String>): List<Song> =
        songs.filter { it.id in ids }
}

package com.aviad.chordstv.data.repository

import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.repository.SongRepository

/** Your remote catalogue first, then the built-in sample songs. */
class CompositeSongRepository(
    private val remote: SongRepository,
    private val local: SongRepository
) : SongRepository {

    override suspend fun search(query: String): List<Song> =
        (remote.search(query) + local.search(query)).distinctBy { it.id }

    override suspend fun featured(): List<Song> =
        (remote.featured() + local.featured()).distinctBy { it.id }

    override suspend fun getById(id: String): Song? =
        remote.getById(id) ?: local.getById(id)

    override suspend fun getByIds(ids: Collection<String>): List<Song> =
        (remote.getByIds(ids) + local.getByIds(ids)).distinctBy { it.id }
}

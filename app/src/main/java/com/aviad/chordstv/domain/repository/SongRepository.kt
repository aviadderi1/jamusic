package com.aviad.chordstv.domain.repository

import com.aviad.chordstv.domain.model.Song

interface SongRepository {
    suspend fun search(query: String): List<Song>
    suspend fun featured(): List<Song>
    suspend fun getById(id: String): Song?
    suspend fun getByIds(ids: Collection<String>): List<Song>
}

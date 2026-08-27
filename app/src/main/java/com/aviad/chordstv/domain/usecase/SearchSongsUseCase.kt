package com.aviad.chordstv.domain.usecase

import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.repository.SongRepository

class SearchSongsUseCase(private val repository: SongRepository) {
    suspend operator fun invoke(query: String): List<Song> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        return repository.search(q)
    }
}

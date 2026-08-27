package com.aviad.chordstv.domain.usecase

import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.repository.SongRepository

class GetSongUseCase(private val repository: SongRepository) {
    suspend operator fun invoke(id: String): Song? = repository.getById(id)
}

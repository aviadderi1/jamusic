package com.aviad.chordstv.domain.usecase

import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.repository.SongRepository

class GetFeaturedSongsUseCase(private val repository: SongRepository) {
    suspend operator fun invoke(): List<Song> = repository.featured()
}

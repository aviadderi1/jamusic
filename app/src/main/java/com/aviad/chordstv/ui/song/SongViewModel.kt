package com.aviad.chordstv.ui.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aviad.chordstv.di.AppContainer
import com.aviad.chordstv.domain.chords.ChordParser
import com.aviad.chordstv.domain.chords.Transposer
import com.aviad.chordstv.domain.model.Segment
import com.aviad.chordstv.domain.model.Song
import com.aviad.chordstv.domain.model.SongLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SongUiState(
    val song: Song? = null,
    val lines: List<SongLine> = emptyList(),
    val transpose: Int = 0,          // semitones, -11..+11
    val displayKey: String = "",
    val fontSp: Int = 26,
    val scrollSpeed: Int = 3,        // 1..10
    val isAutoScrolling: Boolean = false,
    val isFavorite: Boolean = false,
    val preferFlats: Boolean = false,
    val notFound: Boolean = false
)

class SongViewModel(
    private val container: AppContainer,
    private val songId: String
) : ViewModel() {

    private val _state = MutableStateFlow(SongUiState())
    val state: StateFlow<SongUiState> = _state

    private var parsed: List<SongLine> = emptyList()

    init {
        viewModelScope.launch {
            val prefs = container.userPreferencesRepository.preferences.first()
            val song = container.getSong(songId)
            if (song == null) {
                _state.update { it.copy(notFound = true) }
                return@launch
            }
            parsed = ChordParser.parse(song.body)
            _state.update {
                it.copy(
                    song = song,
                    fontSp = prefs.defaultFontSp,
                    scrollSpeed = prefs.defaultScrollSpeed,
                    preferFlats = prefs.preferFlats,
                    isFavorite = songId in prefs.favoriteIds
                )
            }
            applyTranspose()
        }
        viewModelScope.launch {
            container.userPreferencesRepository.preferences.collect { prefs ->
                _state.update { it.copy(isFavorite = songId in prefs.favoriteIds) }
            }
        }
    }

    // ---- Transposition ----
    fun transposeUp() = setTranspose(_state.value.transpose + 1)
    fun transposeDown() = setTranspose(_state.value.transpose - 1)
    fun resetTranspose() = setTranspose(0)

    private fun setTranspose(value: Int) {
        val wrapped = Math.floorMod(value + 11, 23) - 11   // keep within -11..+11
        _state.update { it.copy(transpose = wrapped) }
        applyTranspose()
    }

    private fun applyTranspose() {
        val s = _state.value
        val song = s.song ?: return
        val lines = parsed.map { line ->
            when (line) {
                is SongLine.Lyric -> SongLine.Lyric(
                    line.segments.map { seg ->
                        Segment(
                            chord = seg.chord?.let { Transposer.transpose(it, s.transpose, s.preferFlats) },
                            text = seg.text
                        )
                    }
                )
                else -> line
            }
        }
        val key = Transposer.transposeKey(song.originalKey, s.transpose, s.preferFlats)
        _state.update { it.copy(lines = lines, displayKey = key) }
    }

    // ---- Auto-scroll ----
    fun toggleAutoScroll() = _state.update { it.copy(isAutoScrolling = !it.isAutoScrolling) }
    fun setAutoScroll(on: Boolean) = _state.update { it.copy(isAutoScrolling = on) }
    fun speedUp() = _state.update { it.copy(scrollSpeed = (it.scrollSpeed + 1).coerceIn(1, 10)) }
    fun speedDown() = _state.update { it.copy(scrollSpeed = (it.scrollSpeed - 1).coerceIn(1, 10)) }

    // ---- Font ----
    fun fontUp() = _state.update { it.copy(fontSp = (it.fontSp + 2).coerceIn(16, 48)) }
    fun fontDown() = _state.update { it.copy(fontSp = (it.fontSp - 2).coerceIn(16, 48)) }

    // ---- Favorites ----
    fun toggleFavorite() = viewModelScope.launch {
        container.userPreferencesRepository.toggleFavorite(songId)
    }
}

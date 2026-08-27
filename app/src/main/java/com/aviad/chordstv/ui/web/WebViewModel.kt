package com.aviad.chordstv.ui.web

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aviad.chordstv.di.AppContainer
import com.aviad.chordstv.domain.model.WebBookmark
import com.aviad.chordstv.domain.repository.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WebViewModel(private val container: AppContainer) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = container.userPreferencesRepository.preferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    fun toggleBookmark(bookmark: WebBookmark) = viewModelScope.launch {
        container.userPreferencesRepository.toggleWebBookmark(bookmark)
    }

    fun setZoom(percent: Int) = viewModelScope.launch {
        container.userPreferencesRepository.setWebTextZoom(percent)
    }
}

package com.mirimomekiku.wavvy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.StateFlow

class PlaybackViewModelFactory(
    private val enableFetchingLyrics: StateFlow<Boolean>,
    private val enableFetchingArtistBiography: StateFlow<Boolean>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaybackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaybackViewModel(
                enableFetchingLyrics,
                enableFetchingArtistBiography,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

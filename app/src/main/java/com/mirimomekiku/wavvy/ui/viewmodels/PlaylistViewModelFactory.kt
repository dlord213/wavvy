package com.mirimomekiku.wavvy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mirimomekiku.wavvy.db.dao.PlaylistDao
import com.mirimomekiku.wavvy.viewmodel.PlaylistViewModel

class PlaylistViewModelFactory(
    private val dao: PlaylistDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
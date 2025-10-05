package com.mirimomekiku.wavvy.ui.viewmodels

import Favorite
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.mirimomekiku.wavvy.db.dao.FavoriteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import toMediaItem

class FavoriteViewModel(private val dao: FavoriteDao) : ViewModel() {
    val mediaItems: Flow<List<MediaItem>> =
        dao.getAll().map { entities -> entities.map { it.toMediaItem() } }

    fun toggleFavorite(item: Favorite) {
        viewModelScope.launch {
            val existing = dao.getById(item.mediaId).first()
            if (existing != null) {
                dao.delete(item)
            } else {
                dao.insert(item)
            }
        }
    }

    fun getFavoriteById(mediaId: String) = dao.getById(mediaId)
}

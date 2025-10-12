package com.mirimomekiku.wavvy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.mirimomekiku.wavvy.db.dao.PlaylistDao
import com.mirimomekiku.wavvy.db.entity.Playlists
import com.mirimomekiku.wavvy.db.entity.toMediaItem
import com.mirimomekiku.wavvy.db.entity.toPlaylistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class PlaylistWithMediaItems(
    val name: String,
    val mediaItems: List<MediaItem>
)

class PlaylistViewModel(
    private val playlistDao: PlaylistDao
) : ViewModel() {

    val allPlaylists: StateFlow<List<Playlists>> =
        playlistDao.getAll()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun savePlaylist(playlistId: String, playlistName: String, mediaItems: List<MediaItem>) {
        viewModelScope.launch {
            val items = mediaItems.map { it.toPlaylistEntity() }
            val playlist = Playlists(
                playlistId = playlistId,
                playlistName = playlistName,
                items = items
            )
            playlistDao.insert(playlist)
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            playlistDao.deleteById(playlistId)
        }
    }

    fun loadPlaylistAsMediaItems(
        playlistId: String,
    ) {
        viewModelScope.launch {
            val playlist = playlistDao.getOne(playlistId)
            val mediaItems = playlist?.items?.map { it.toMediaItem() } ?: emptyList()
        }
    }

    fun removeMediaItemFromPlaylist(playlistId: String, mediaId: String) {
        viewModelScope.launch {
            val playlist = playlistDao.getOne(playlistId)
            if (playlist != null) {
                val updatedItems = playlist.items.filterNot { it.mediaId == mediaId }
                val updatedPlaylist = playlist.copy(items = updatedItems)
                playlistDao.update(updatedPlaylist)
            }
        }
    }

    fun observePlaylistAsMediaItems(
        playlistId: String
    ): Flow<PlaylistWithMediaItems> {
        return playlistDao.getById(playlistId)
            .map { playlist ->
                PlaylistWithMediaItems(
                    name = playlist?.playlistName ?: "Untitled Playlist",
                    mediaItems = playlist?.items?.map { it.toMediaItem() } ?: emptyList()
                )
            }
    }
}

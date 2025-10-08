package com.mirimomekiku.wavvy.ui.viewmodels

import AlbumWithSongs
import ArtistWithAlbums
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.BuildConfig
import com.mirimomekiku.wavvy.extensions.currentMediaItems
import com.mirimomekiku.wavvy.extensions.getDominantColor
import com.mirimomekiku.wavvy.extensions.shuffledQueue
import com.mirimomekiku.wavvy.instances.GeniusArtistResponse
import com.mirimomekiku.wavvy.instances.RetrofitInstance
import com.mirimomekiku.wavvy.instances.extractPrimaryArtist
import com.mirimomekiku.wavvy.widgets.WidgetUpdater
import getAlbumArtFile
import getAllAudioFiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaybackViewModel : ViewModel() {

    // Playback state
    private val _currentMediaItem = MutableStateFlow<MediaItem?>(null)
    private val _isPlaying = MutableStateFlow(false)
    private val _mediaMetaData = MutableStateFlow<MediaMetadata?>(null)
    private val _bottomBarColor = MutableStateFlow<Color?>(null)
    private val _isRepeatEnabled = MutableStateFlow(0)
    private val _isShuffledEnabled = MutableStateFlow(false)

    // Library state
    private val _audioFiles = MutableStateFlow<List<MediaItem>>(emptyList())
    private val _queue = MutableStateFlow<List<MediaItem>>(emptyList())
    private val _searchQuery = MutableStateFlow<String?>(null)
    private val _artistInfo = MutableStateFlow<GeniusArtistResponse?>(null)

    // States for selected screens (albums/artists)
    private val _albumSelected = MutableStateFlow<AlbumWithSongs?>(null)
    private val _artistSelected = MutableStateFlow<ArtistWithAlbums?>(null)

    // Public flows
    val audioFiles: StateFlow<List<MediaItem>> = _audioFiles
    val queue: StateFlow<List<MediaItem>> = _queue

    val currentMediaItem: StateFlow<MediaItem?> = _currentMediaItem
    val isPlaying: StateFlow<Boolean> = _isPlaying
    val bottomBarColor: StateFlow<Color?> = _bottomBarColor
    val isRepeatEnabled: StateFlow<Int> = _isRepeatEnabled
    val isShuffledEnabled: StateFlow<Boolean> = _isShuffledEnabled

    val albumSelected: StateFlow<AlbumWithSongs?> = _albumSelected
    val artistSelected: StateFlow<ArtistWithAlbums?> = _artistSelected
    val searchQuery: StateFlow<String?> = _searchQuery
    val artistInfo: StateFlow<GeniusArtistResponse?> = _artistInfo

    // Load local audio into state
    fun loadAudioFiles(context: Context) {
        viewModelScope.launch {
            _audioFiles.value = getAllAudioFiles(context)
            _queue.value = _audioFiles.value
        }
    }

    private fun fetchGeniusArtistInfo(artistName: String) {
        if (artistName.isBlank()) return

        viewModelScope.launch {
            try {
                _artistInfo.value = null
                val artistQuery = extractPrimaryArtist(artistName)
                val token = BuildConfig.token
                val response = RetrofitInstance.api.search(query = artistQuery, token = token)

                val song = response.response.hits.firstOrNull()?.result
                val info = song?.primary_artist?.id?.let {
                    RetrofitInstance.api.getArtist(
                        it,
                        token = token
                    )
                }
                _artistInfo.value = info
            } catch (e: Exception) {
                e.printStackTrace()
                _artistInfo.value = null
            }
        }
    }

    fun attachController(mediaController: MediaController, context: Context) {
        mediaController.addListener(object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                _mediaMetaData.value = mediaController.mediaMetadata
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if (_isShuffledEnabled.value == true) {
                    _queue.value = mediaController.shuffledQueue()
                } else {
                    _queue.value = mediaController.currentMediaItems
                }

                mediaItem?.mediaMetadata?.artist?.toString()?.let { artistName ->
                    fetchGeniusArtistInfo(artistName)
                }

                _currentMediaItem.value = mediaItem
                _bottomBarColor.value =
                    mediaItem?.getDominantColor()?.let { Color(it) } ?: Color(0xFF484848)

                viewModelScope.launch {

                    val uri = getAlbumArtFile(
                        context, mediaItem?.mediaMetadata?.artworkUri!!, mediaItem.mediaId.toLong()
                    )

                    WidgetUpdater.updateNowPlaying(
                        context = context,
                        mediaItem = mediaItem,
                        uri = uri!!.toUri()
                    )
                }

            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                _isPlaying.value = isPlaying
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)
                _isRepeatEnabled.value = repeatMode

                if (_isShuffledEnabled.value == true) {
                    _queue.value = mediaController.shuffledQueue()
                } else {
                    _queue.value = mediaController.currentMediaItems
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
                if (_isShuffledEnabled.value == true) {
                    _queue.value = mediaController.shuffledQueue()
                } else {
                    _queue.value = mediaController.currentMediaItems
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)
                _isShuffledEnabled.value = shuffleModeEnabled

                if (_isShuffledEnabled.value == true) {
                    _queue.value = mediaController.shuffledQueue()
                } else {
                    _queue.value = mediaController.currentMediaItems
                }

            }

        })
    }

    fun selectArtist(artistName: String) {
        val allSongs = _audioFiles.value

        val songsForArtist = allSongs.filter {
            it.mediaMetadata.artist?.equals(artistName) == true
        }

        if (songsForArtist.isEmpty()) return

        val albums = songsForArtist.groupBy { it.mediaMetadata.albumTitle ?: "Unknown Album" }
            .map { (album, songs) ->
                AlbumWithSongs(
                    album.toString(),
                    songs.sortedBy { it.mediaMetadata.title.toString() })
            }.sortedBy { it.album }

        _artistSelected.value = ArtistWithAlbums(artistName, albums)
    }

    fun selectAlbum(albumName: String) {
        val allSongs = _audioFiles.value

        val songsForAlbum = allSongs.filter {
            it.mediaMetadata.albumTitle?.equals(albumName) == true
        }

        if (songsForAlbum.isEmpty()) return

        _albumSelected.value = AlbumWithSongs(
            album = albumName,
            songs = songsForAlbum.sortedBy { it.mediaMetadata.title.toString() })
    }

    // Playback controls
    fun updateCurrentMediaItem(value: MediaItem?) {
        _currentMediaItem.value = value
    }

    fun updateBottomBarColor(value: Color) {
        _bottomBarColor.value = value
    }

    fun updatePlayingState(value: Boolean) {
        _isPlaying.value = value
    }

    fun toggleRepeatMode(mediaController: MediaController) {
        val newMode = when (mediaController.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
            else -> Player.REPEAT_MODE_OFF
        }

        mediaController.repeatMode = newMode
        _isRepeatEnabled.value = newMode
    }

    fun updateSearchQuery(value: String) {
        _searchQuery.value = value
    }
}

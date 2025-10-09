package com.mirimomekiku.wavvy.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mirimomekiku.wavvy.enums.Screens
import com.mirimomekiku.wavvy.extensions.KEY_DOMINANT_COLOR
import com.mirimomekiku.wavvy.ui.composables.dialogs.AddToPlaylistDialog
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel
import formatDurationMs

@Composable
fun MediaItemRow(
    audio: MediaItem,
    mediaController: MediaController,
    showGoToAlbum: Boolean = true,
    showGoToArtist: Boolean = true,
    albumTrackNumber: String? = null,
    showAlbumTrackNumber: Boolean? = false,
    showDuration: Boolean? = false,
    showDropdown: Boolean? = true,
    modifier: Modifier = Modifier,
) {
    val playbackViewModel = LocalPlaybackViewModel.current
    val navController = LocalNavController.current
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    var showAddToPlaylistDialog by rememberSaveable { mutableStateOf(false) }

    fun playAudio() {
        val existingIndex = (0 until mediaController.mediaItemCount).firstOrNull { i ->
            mediaController.getMediaItemAt(i).mediaId == audio.mediaId
        }

        if (existingIndex != null) {
            mediaController.seekTo(existingIndex, 0)
        } else {
            mediaController.addMediaItem(audio)
            val newIndex = mediaController.mediaItemCount - 1
            mediaController.seekTo(newIndex, 0)
        }

        mediaController.prepare()
        mediaController.play()
    }

    fun addToQueue() {
        val exists =
            (0 until mediaController.mediaItemCount).any { i -> mediaController.getMediaItemAt(i).mediaId == audio.mediaId }

        if (!exists) {
            mediaController.addMediaItem(audio)
            mediaController.prepare()
        }
        menuExpanded = false
    }

    fun addToQueueNext() {
        val currentIndex = mediaController.currentMediaItemIndex

        val existingIndex = (0 until mediaController.mediaItemCount).firstOrNull { i ->
            mediaController.getMediaItemAt(i).mediaId == audio.mediaId
        }

        if (existingIndex != null) {
            if (existingIndex != currentIndex + 1) {
                mediaController.moveMediaItem(existingIndex, currentIndex + 1)
            }
        } else {
            mediaController.addMediaItem(currentIndex + 1, audio)
        }

        mediaController.prepare()
        menuExpanded = false
    }

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    playAudio()
                }
                .padding(8.dp)) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showAlbumTrackNumber == true) {
                    Text(albumTrackNumber.toString(), style = MaterialTheme.typography.labelMedium)
                }

                val dominantColor = (audio.mediaMetadata.extras?.getInt(KEY_DOMINANT_COLOR)
                    ?: 0x484848) or 0xFF000000.toInt()

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(audio.mediaMetadata.artworkUri).crossfade(true)
                        .memoryCachePolicy(CachePolicy.ENABLED).diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "Album art",
                    placeholder = ColorPainter(Color(dominantColor)),
                    error = ColorPainter(Color(dominantColor)),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .size(48.dp)
                )
            }


            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    audio.mediaMetadata.title.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    audio.mediaMetadata.artist.toString(),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (showDuration == true) {
                Text(
                    formatDurationMs(audio.mediaMetadata.durationMs!!),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }

            if (showDropdown == true) {
                IconButton(
                    onClick = {
                        menuExpanded = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "Repeat Mode",
                    )
                    DropdownMenu(
                        expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Play next") },
                            onClick = { addToQueueNext() })
                        DropdownMenuItem(
                            text = { Text("Add to playing queue") },
                            onClick = { addToQueue() })
                        if (showGoToAlbum) {
                            DropdownMenuItem(text = { Text("Go to album") }, onClick = {
                                menuExpanded = false
                                playbackViewModel.selectAlbum(audio.mediaMetadata.albumTitle.toString())
                                navController.navigate(Screens.Album.name) {
                                    popUpTo(Screens.Album.name)
                                }
                            })
                        }
                        if (showGoToArtist) {
                            DropdownMenuItem(text = { Text("Go to artist") }, onClick = {
                                menuExpanded = false
                                playbackViewModel.selectArtist(audio.mediaMetadata.artist.toString())
                                navController.navigate(Screens.Artist.name) {
                                    popUpTo(Screens.Artist.name)
                                }
                            })
                        }
                        DropdownMenuItem(text = { Text("Add to playlist") }, onClick = {
                            showAddToPlaylistDialog = true
                        })
                    }
                }
            }
        }
    }

    if (showAddToPlaylistDialog) {
        AddToPlaylistDialog(audio, onDismiss = {
            showAddToPlaylistDialog = false
        })
    }
}

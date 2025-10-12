package com.mirimomekiku.wavvy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import com.mirimomekiku.wavvy.extensions.getDominantColor
import com.mirimomekiku.wavvy.ui.composables.MediaItemRow
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaylistViewModel
import com.mirimomekiku.wavvy.viewmodel.PlaylistWithMediaItems

@Composable
fun PlaylistScreen(
    id: String,
    mediaController: MediaController,
) {
    val playlistViewModel = LocalPlaylistViewModel.current
    val playbackViewModel = LocalPlaybackViewModel.current
    val navController = LocalNavController.current

    val playlist by playlistViewModel.observePlaylistAsMediaItems(id)
        .collectAsState(
            initial = PlaylistWithMediaItems(
                name = "Loading...",
                mediaItems = emptyList()
            )
        )

    fun playAllSongs() {
        mediaController.clearMediaItems()
        mediaController.setMediaItems(playlist.mediaItems)

        val bgColor = mediaController.currentMediaItem?.getDominantColor()?.let { Color(it) }
            ?: Color.DarkGray

        playbackViewModel.updateCurrentMediaItem(mediaController.currentMediaItem)
        playbackViewModel.updateBottomBarColor(bgColor)

        mediaController.prepare()
        mediaController.play()
    }

    Column(
        modifier = Modifier.padding(horizontal = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                )
            }
            IconButton(onClick = {
                playlistViewModel.deletePlaylist(id)
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete, contentDescription = "Delete"
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {


            item(span = { GridItemSpan(maxLineSpan) }) {
                AsyncImage(
                    model = playlist.mediaItems.firstOrNull()?.mediaMetadata?.artworkUri,
                    contentDescription = "Album art",
                    placeholder = ColorPainter(Color(0xFF484848)),
                    error = ColorPainter(Color(0xFF484848)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        playlist.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(
                        onClick = { playAllSongs() }, modifier = Modifier.weight(1f, fill = true)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play all songs"
                        )
                        Text("Play all", style = MaterialTheme.typography.labelLarge)
                    }
                }

            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Songs",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(
                playlist.mediaItems,
                span = { GridItemSpan(maxLineSpan) }, key = { it.mediaId }) { song ->
                MediaItemRow(
                    audio = song,
                    mediaController = mediaController,
                    showGoToArtist = false,
                    showDuration = false,
                    showRemovePlaylist = true,
                    playlistId = id
                )
            }
        }
    }
}
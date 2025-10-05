package com.mirimomekiku.wavvy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import com.mirimomekiku.wavvy.extensions.getDominantColor
import com.mirimomekiku.wavvy.ui.composables.MediaItemRow
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun AlbumScreen(mediaController: MediaController) {
    val playbackViewModel = LocalPlaybackViewModel.current
    val navController = LocalNavController.current

    val album by playbackViewModel.albumSelected.collectAsStateWithLifecycle()

    fun playAllSongs() {
        mediaController.clearMediaItems()
        mediaController.setMediaItems(
            album?.songs ?: emptyList()
        )

        val bgColor = mediaController.currentMediaItem?.getDominantColor()
            ?.let { Color(it) }
            ?: Color.DarkGray

        playbackViewModel.updateCurrentMediaItem(mediaController.currentMediaItem)
        playbackViewModel.updateBottomBarColor(bgColor)

        mediaController.prepare()
        mediaController.play()
    }

    Column(
        modifier = Modifier.padding(horizontal = 6.dp),
    ) {
        Row {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }


        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                AsyncImage(
                    model = album?.songs?.get(0)?.mediaMetadata?.artworkUri,
                    contentDescription = "Album art",
                    placeholder = ColorPainter(Color(0xFF484848)),
                    error = ColorPainter(Color(0xFF484848)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        album?.album.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        album?.songs?.get(0)?.mediaMetadata?.artist.toString(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            item {
                Row {
                    FilledTonalButton(
                        onClick = { playAllSongs() },
                        modifier = Modifier.weight(1f, fill = true)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play all songs"
                        )
                        Text("Play all", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            item {
                Text(
                    "Songs",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(
                album?.songs?.sortedWith(compareBy(
                    { it.mediaMetadata.extras?.getInt("disc_number") ?: 0 },
                    { it.mediaMetadata.extras?.getInt("track_number") ?: 0 }
                )) ?: emptyList(),
                key = { it.mediaId }
            ) { song ->

                MediaItemRow(
                    audio = song,
                    mediaController = mediaController,
                    showGoToAlbum = false,
                    albumTrackNumber = if (song.mediaMetadata.extras?.getInt("track_number") != null) song.mediaMetadata.extras?.getInt(
                        "track_number"
                    ).toString() else song.mediaMetadata.extras?.getInt("disc_number").toString(),
                    showAlbumTrackNumber = true,
                    showDuration = true,
                )
            }
        }
    }
}
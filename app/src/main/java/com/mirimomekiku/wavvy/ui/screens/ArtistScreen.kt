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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import com.mirimomekiku.wavvy.enums.Screens
import com.mirimomekiku.wavvy.extensions.getDominantColor
import com.mirimomekiku.wavvy.ui.composables.MediaCard
import com.mirimomekiku.wavvy.ui.composables.MediaItemRow
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun ArtistScreen(
    mediaController: MediaController,
) {
    val playbackViewModel = LocalPlaybackViewModel.current
    val navController = LocalNavController.current

    val artist = playbackViewModel.artistSelected.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        playbackViewModel.attachController(mediaController)

        onDispose { }
    }

    fun playAllSongs() {
        mediaController.clearMediaItems()
        mediaController.setMediaItems(
            artist.value?.albums?.flatMap { it.songs } ?: emptyList()
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {


            item(span = { GridItemSpan(maxLineSpan) }) {
                AsyncImage(
                    model = artist.value?.albums?.firstOrNull()?.songs?.firstOrNull()?.mediaMetadata?.artworkUri,
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
                        artist.value?.artist ?: "Unknown Artist",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "${artist.value?.albums?.size ?: 0} albums • ${artist.value?.albums?.sumOf { it.songs.size } ?: 0} songs",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Albums",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            val grouped = artist.value?.albums
                ?.flatMap { it.songs }
                ?.groupBy { it.mediaMetadata.albumTitle ?: "Unknown Album" }
                ?.toList()
                ?: emptyList()

            items(grouped, key = { it.first }) { (album, songs) ->
                val artwork = songs.firstOrNull()?.mediaMetadata?.artworkUri

                MediaCard(
                    text = album.toString(),
                    imageUri = artwork,
                    navigate = {
                        playbackViewModel.selectAlbum(album.toString())
                        navController.navigate(Screens.Album.name) {
                            popUpTo(Screens.Album.name) { inclusive = true }
                        }
                    }
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Songs",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(
                artist.value?.albums?.flatMap { it.songs } ?: emptyList(),
                key = { it.mediaMetadata.title.toString() },
                span = { GridItemSpan(maxLineSpan) }
            ) { song ->
                MediaItemRow(
                    audio = song,
                    mediaController = mediaController,
                    showGoToArtist = false,
                    showDuration = true,
                )
            }
        }
    }

}
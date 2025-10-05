package com.mirimomekiku.wavvy.ui.composables.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.enums.Screens
import com.mirimomekiku.wavvy.ui.composables.MediaCard
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun AlbumsPage(mediaController: MediaController) {
    val navController = LocalNavController.current
    val playbackViewModel = LocalPlaybackViewModel.current
    val audioFiles by playbackViewModel.audioFiles.collectAsStateWithLifecycle()
    val sortedAlbums =
        remember(audioFiles) {
            audioFiles.groupBy { it.mediaMetadata.albumTitle }.toList()
        }

    val lazyGridState = LazyGridState()

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sortedAlbums,
            key = { (album, _) -> album ?: "Unknown" }) { (album, songs) ->
            MediaCard(text = album?.toString() ?: "Unknown Album",
                imageUri = songs.firstOrNull()?.mediaMetadata?.artworkUri,
                navigate = {
                    playbackViewModel.selectAlbum(album.toString())
                    navController.navigate(route = Screens.Album.name) {
                        popUpTo(Screens.Album.name)
                    }
                })
        }
    }
}
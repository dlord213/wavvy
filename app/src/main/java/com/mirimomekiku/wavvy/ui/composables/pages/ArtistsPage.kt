package com.mirimomekiku.wavvy.ui.composables.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
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
fun ArtistsPage(mediaController: MediaController) {
    val navController = LocalNavController.current
    val playbackViewModel = LocalPlaybackViewModel.current
    val audioFiles by playbackViewModel.audioFiles.collectAsStateWithLifecycle()

    val sortedArtists = remember(audioFiles) {
        audioFiles.groupBy { it.mediaMetadata.artist }.toList()
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
        items(sortedArtists, key = { (artist, _) -> artist ?: "Unknown" }) { (artist, songs) ->
            MediaCard(text = (artist ?: "Unknown Artist").toString(),
                imageUri = songs.firstOrNull()?.mediaMetadata?.artworkUri,
                navigate = {
                    playbackViewModel.selectArtist(artist.toString())
                    navController.navigate(route = Screens.Artist.name) {
                        popUpTo(Screens.Artist.name)
                    }
                }
            )
        }
    }
}
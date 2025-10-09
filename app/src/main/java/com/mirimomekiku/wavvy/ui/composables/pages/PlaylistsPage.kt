package com.mirimomekiku.wavvy.ui.composables.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.ui.composables.MediaCard
import com.mirimomekiku.wavvy.ui.composables.dialogs.CreatePlaylistDialog
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaylistViewModel
import java.util.UUID

@Composable
fun PlaylistsPage(
    mediaController: MediaController,
) {
    val navController = LocalNavController.current
    val playlistViewModel = LocalPlaylistViewModel.current
    val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    val lazyGridState = LazyGridState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(playlists) { playlist ->
                val artworkUri = playlist.items.firstOrNull()?.artworkUri?.toUri()

                MediaCard(
                    text = playlist.playlistName,
                    imageUri = artworkUri,
                    navigate = {
                        navController.navigate("playlist/${playlist.playlistId}")
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Playlist")
        }
    }

    if (showDialog) {
        CreatePlaylistDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name ->
                val newId = UUID.randomUUID().toString()
                playlistViewModel.savePlaylist(newId, name, emptyList())
                showDialog = false
            }
        )
    }
}
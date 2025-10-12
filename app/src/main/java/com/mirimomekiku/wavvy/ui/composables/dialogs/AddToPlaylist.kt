package com.mirimomekiku.wavvy.ui.composables.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.mirimomekiku.wavvy.db.entity.toMediaItem
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaylistViewModel
import kotlinx.coroutines.launch

@Composable
fun AddToPlaylistDialog(
    mediaItem: MediaItem,
    onDismiss: () -> Unit
) {
    val playlistViewModel = LocalPlaylistViewModel.current
    val playlists by playlistViewModel.allPlaylists.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                coroutineScope.launch {
                    val newId = java.util.UUID.randomUUID().toString()
                    playlistViewModel.savePlaylist(
                        playlistId = newId,
                        playlistName = name,
                        mediaItems = listOf(mediaItem)
                    )
                    showCreateDialog = false
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Playlist") },
        text = {
            if (playlists.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No playlists found.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { showCreateDialog = true }) {
                        Text("Create Playlist")
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(playlists) { playlist ->
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    coroutineScope.launch {
                                        val existingItems =
                                            playlist.items.map { it.toMediaItem() }

                                        val alreadyExists = existingItems.any {
                                            it.mediaId == mediaItem.mediaId
                                        }

                                        if (!alreadyExists) {
                                            val updatedItems = existingItems + mediaItem
                                            playlistViewModel.savePlaylist(
                                                playlistId = playlist.playlistId,
                                                playlistName = playlist.playlistName,
                                                mediaItems = updatedItems
                                            )
                                        }

                                        onDismiss()
                                    }
                                }
                        ) {
                            Text(
                                text = playlist.playlistName,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { showCreateDialog = true }) {
                Text("New Playlist")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

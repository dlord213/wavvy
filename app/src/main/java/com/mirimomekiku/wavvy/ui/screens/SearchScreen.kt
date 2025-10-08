package com.mirimomekiku.wavvy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.ui.composables.MediaItemRow
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun SearchScreen(mediaController: MediaController) {
    val playbackViewModel = LocalPlaybackViewModel.current
    val navController = LocalNavController.current

    val audioFiles by playbackViewModel.audioFiles.collectAsStateWithLifecycle()
    val query by playbackViewModel.searchQuery.collectAsStateWithLifecycle()

    val textFieldState = rememberTextFieldState(initialText = query ?: "")

    LaunchedEffect(textFieldState.text) {
        playbackViewModel.updateSearchQuery(textFieldState.text.toString())
    }

    val filteredFiles = if (query.isNullOrEmpty()) {
        emptyList()
    } else {
        audioFiles.filter {
            val title = it.mediaMetadata.title?.toString()?.lowercase().orEmpty()
            val artist = it.mediaMetadata.artist?.toString()?.lowercase().orEmpty()
            val album = it.mediaMetadata.albumTitle?.toString()?.lowercase().orEmpty()
            val q = query!!.lowercase()
            q in title || q in artist || q in album
        }
    }.sortedBy { it.mediaMetadata.title?.toString().orEmpty() }

    Column(
        modifier = Modifier.padding(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            TextField(
                state = textFieldState,
                label = { Text("Search") },
                textStyle = MaterialTheme.typography.labelLarge,
                lineLimits = TextFieldLineLimits.SingleLine,
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.primary,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .fillMaxWidth()
                    .padding(12.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            items(
                filteredFiles,
                key = { it.mediaId }
            ) { audio ->
                MediaItemRow(audio, mediaController)
            }
        }
    }
}
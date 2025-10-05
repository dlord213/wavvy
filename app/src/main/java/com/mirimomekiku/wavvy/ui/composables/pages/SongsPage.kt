package com.mirimomekiku.wavvy.ui.composables.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.ui.composables.MediaItemRow
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun SongsPage(mediaController: MediaController) {
    val playbackViewModel = LocalPlaybackViewModel.current
    val audioFiles by playbackViewModel.audioFiles.collectAsStateWithLifecycle()


    val sortedAudioFiles =
        remember(audioFiles) { audioFiles.sortedBy { it -> it.mediaMetadata.title.toString() } }
    val lazyListState = LazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        items(sortedAudioFiles,
            key = { it.mediaId }) { audio ->
            MediaItemRow(audio, mediaController, modifier = Modifier.animateItem())
        }
    }
}
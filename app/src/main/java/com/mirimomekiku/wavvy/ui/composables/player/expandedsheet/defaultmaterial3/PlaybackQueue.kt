package com.mirimomekiku.wavvy.ui.composables.player.expandedsheet.defaultmaterial3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.helpers.getDominantColorAdjusted
import com.mirimomekiku.wavvy.ui.composables.MediaVolumeSlider
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun PlaybackQueue(mediaController: MediaController) {
    val context = LocalContext.current
    val playbackViewModel = LocalPlaybackViewModel.current

    val mediaItem by playbackViewModel.currentMediaItem.collectAsStateWithLifecycle()

    val queue by playbackViewModel.queue.collectAsStateWithLifecycle()
    val baseColor = getDominantColorAdjusted(
        context,
        mediaItem?.mediaMetadata?.artworkUri
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Queue", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f, fill = true)
        ) {
            items(queue, key = { it.mediaId }) { item ->
                QueueRow(item, mediaController)
            }
        }
        MediaVolumeSlider(mediaController, baseColor)
    }
}
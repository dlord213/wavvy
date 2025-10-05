package com.mirimomekiku.wavvy.ui.composables.player.expandedsheet.defaultmaterial3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun PlaybackArtistInfo() {
    val playbackViewModel = LocalPlaybackViewModel.current

    val artistInfo by playbackViewModel.artistInfo.collectAsStateWithLifecycle()

    if (artistInfo != null) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "About artist",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AsyncImage(
                        model = artistInfo?.response?.artist?.image_url,
                        contentDescription = "Artist image",
                        placeholder = ColorPainter(Color(0xFF484848)),
                        error = ColorPainter(Color(0xFF484848)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }

                if (artistInfo?.response?.artist?.name != null) {
                    item {
                        Text(
                            artistInfo?.response?.artist?.name.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                if (artistInfo?.response?.artist?.description != null) {
                    item {
                        Text(
                            artistInfo?.response?.artist?.description?.plain.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }


}
package com.mirimomekiku.wavvy.ui.composables.player.expandedsheet.defaultmaterial3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel
import kotlinx.coroutines.delay

@Composable
fun PlaybackLyricsInfo(mediaController: MediaController) {
    val playbackViewModel = LocalPlaybackViewModel.current

    val lyricsInfo by playbackViewModel.lyricsInfo.collectAsStateWithLifecycle()
    val showLyrics by playbackViewModel.showLyrics.collectAsStateWithLifecycle()

    if (showLyrics) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Song lyrics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black
            )

            if (lyricsInfo != null) {
                val syncedLines = remember(lyricsInfo) {
                    lyricsInfo?.syncedLyrics
                        ?.lines()
                        ?.mapNotNull { line ->
                            val match = Regex("""\[(\d{2}):(\d{2}\.\d{2})] ?(.*)""").find(line)
                            match?.let {
                                val (min, sec, text) = it.destructured
                                ((min.toInt() * 60 * 1000) + (sec.toFloat() * 1000).toInt()) to text
                            }
                        } ?: emptyList()
                }

                val position by produceState(initialValue = 0L) {
                    while (true) {
                        value = mediaController.currentPosition
                        delay(300)
                    }
                }

                SyncedLyrics(syncedLines, position)
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(
                "Auto-fetch synced lyrics is disabled.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
    }

}
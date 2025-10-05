package com.mirimomekiku.wavvy.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import coil.compose.rememberAsyncImagePainter
import com.mirimomekiku.wavvy.helpers.getDominantColorAdjusted
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheetHandle(
    scaffoldState: BottomSheetScaffoldState,
    mediaController: MediaController,
    scope: CoroutineScope
) {
    val playbackViewModel = LocalPlaybackViewModel.current
    val context = LocalContext.current

    val interactionSource = remember { MutableInteractionSource() }
    val currentMediaItem by playbackViewModel.currentMediaItem.collectAsStateWithLifecycle()
    val playingState by playbackViewModel.isPlaying.collectAsStateWithLifecycle()

    var progress by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(0f) }
    val opacity by animateFloatAsState(
        targetValue = if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) 1f else 0f,
        label = "opacityAnimation"
    )
    val progressColor by playbackViewModel.bottomBarColor.collectAsStateWithLifecycle()
    val baseColor = getDominantColorAdjusted(context, currentMediaItem?.mediaMetadata?.artworkUri)

    LaunchedEffect(mediaController) {
        while (true) {
            val pos = mediaController.currentPosition.coerceAtLeast(0L)
            val dur = mediaController.duration.coerceAtLeast(1L) // avoid divide-by-zero
            progress = pos.toFloat() / dur.toFloat()
            duration = dur.toFloat()
            delay(500L)
        }
    }

    if (currentMediaItem != null) {
        Column(
            modifier = Modifier
                .alpha(opacity)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    scope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
        ) {
            progressColor?.let {
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(MaterialTheme.shapes.extraLarge),
                    color = baseColor,
                    trackColor = it,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentMediaItem?.mediaMetadata?.artworkUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(currentMediaItem?.mediaMetadata?.artworkUri),
                        contentDescription = "Album art",
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .size(40.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh) // fallback to 484848
                            .size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        currentMediaItem?.mediaMetadata?.title?.toString()
                            ?: "Loading...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        currentMediaItem?.mediaMetadata?.artist?.toString()
                            ?: "Loading...",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                IconButton(
                    onClick = {
                        if (playingState) mediaController.pause() else mediaController.play()
                    }
                ) {
                    Icon(
                        imageVector = if (playingState) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (playingState) "Pause" else "Play"
                    )
                }
            }
        }
    }
}
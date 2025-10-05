package com.mirimomekiku.wavvy.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel
import formatDurationMs
import kotlinx.coroutines.delay

@Composable
fun MediaSlider(
    mediaController: MediaController,
    baseColor: Color
) {
    val playbackViewModel = LocalPlaybackViewModel.current

    val isPlaying by playbackViewModel.isPlaying.collectAsStateWithLifecycle()
    var sliderPosition by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(0f) }

    val disabledColor = baseColor.copy(alpha = 0.3f)

    val sliderColors: SliderColors = SliderDefaults.colors(
        thumbColor = if (isPlaying) baseColor else disabledColor,
        activeTrackColor = if (isPlaying) baseColor else disabledColor,
        inactiveTrackColor = if (isPlaying) baseColor.copy(alpha = 0.3f) else disabledColor.copy(
            alpha = 0.3f
        ),
        disabledThumbColor = disabledColor,
        disabledActiveTrackColor = disabledColor,
        disabledInactiveTrackColor = disabledColor.copy(alpha = 0.3f),
        activeTickColor = if (isPlaying) baseColor else disabledColor,
        inactiveTickColor = if (isPlaying) baseColor.copy(alpha = 0.3f) else disabledColor.copy(
            alpha = 0.3f
        ),
        disabledActiveTickColor = disabledColor,
        disabledInactiveTickColor = disabledColor.copy(alpha = 0.3f)
    )

    LaunchedEffect(mediaController) {
        while (true) {
            val pos = mediaController.currentPosition.coerceAtLeast(0L).toFloat()
            val dur = if (mediaController.duration > 0) mediaController.duration.toFloat() else 0f
            sliderPosition = pos
            duration = dur
            delay(500L)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(
            16.dp, Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = { newValue ->
                sliderPosition = newValue
            },
            onValueChangeFinished = {
                mediaController.seekTo(sliderPosition.toLong())
            },
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier.weight(6f, fill = true),
            steps = 0,
            colors = sliderColors
        )
        Text(
            formatDurationMs(sliderPosition.toLong()),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
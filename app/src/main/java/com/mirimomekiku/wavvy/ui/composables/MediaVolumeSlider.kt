package com.mirimomekiku.wavvy.ui.composables

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun MediaVolumeSlider(
    mediaController: MediaController,
    baseColor: Color
) {
    val context = LocalContext.current
    val audioManager = remember {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    val playbackViewModel = LocalPlaybackViewModel.current

    val deviceInfo = mediaController.deviceInfo
    val isRemote =
        deviceInfo.playbackType == 1
    val isPlaying by playbackViewModel.isPlaying.collectAsStateWithLifecycle()

    // Local system volume
    var currentVolume by remember { mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) }
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    // Remote device volume info
    val remoteVolume = mediaController.deviceVolume
    val remoteMaxVolume = deviceInfo.maxVolume // fallback if unknown

    val sliderValue = if (isRemote) remoteVolume.toFloat() else currentVolume.toFloat()
    val sliderMax = if (isRemote) remoteMaxVolume.toFloat() else maxVolume.toFloat()

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

    DisposableEffect(Unit) {
        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                // update volume when system volume changes
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            }
        }

        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            contentObserver
        )

        onDispose {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {
                if (isRemote) {
                    mediaController.decreaseDeviceVolume(0)
                } else {
                    val newVol = (currentVolume - 1).coerceAtLeast(0)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                    currentVolume = newVol
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                contentDescription = "Volume down"
            )
        }

        Slider(
            value = sliderValue,
            onValueChange = { value ->
                if (isRemote) {
                    mediaController.setDeviceVolume(value.toInt(), 0)
                } else {
                    val newVol = value.toInt().coerceIn(0, maxVolume)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                    currentVolume = newVol
                }
            },
            valueRange = 0f..sliderMax,
            steps = (sliderMax / 5 - 1).toInt().coerceAtLeast(0),
            modifier = Modifier.weight(1f),
            colors = sliderColors
        )

        IconButton(
            onClick = {
                if (isRemote) {
                    mediaController.increaseDeviceVolume(0)
                } else {
                    val newVol = (currentVolume + 1).coerceAtMost(maxVolume)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                    currentVolume = newVol
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Volume up"
            )
        }
    }
}
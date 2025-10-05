package com.mirimomekiku.wavvy.ui.compositions

import androidx.compose.runtime.compositionLocalOf
import androidx.media3.session.MediaController

val LocalMediaController = compositionLocalOf<MediaController> {
    error("No media controller")
}
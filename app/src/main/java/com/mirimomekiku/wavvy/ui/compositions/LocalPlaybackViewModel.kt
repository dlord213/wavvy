package com.mirimomekiku.wavvy.ui.compositions

import androidx.compose.runtime.compositionLocalOf
import com.mirimomekiku.wavvy.ui.viewmodels.PlaybackViewModel

val LocalPlaybackViewModel =
    compositionLocalOf<PlaybackViewModel> { error("No playback model/state") }
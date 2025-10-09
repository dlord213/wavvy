package com.mirimomekiku.wavvy.ui.compositions

import androidx.compose.runtime.compositionLocalOf
import com.mirimomekiku.wavvy.viewmodel.PlaylistViewModel

val LocalPlaylistViewModel = compositionLocalOf<PlaylistViewModel> {
    error("No favorite view model")
}
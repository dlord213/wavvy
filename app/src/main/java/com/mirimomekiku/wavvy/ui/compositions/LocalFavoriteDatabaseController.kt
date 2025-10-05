package com.mirimomekiku.wavvy.ui.compositions

import androidx.compose.runtime.compositionLocalOf
import com.mirimomekiku.wavvy.ui.viewmodels.FavoriteViewModel

val LocalFavoriteViewModel = compositionLocalOf<FavoriteViewModel> {
    error("No favorite view model")
}
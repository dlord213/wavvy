package com.mirimomekiku.wavvy.ui.compositions

import androidx.compose.runtime.compositionLocalOf
import com.mirimomekiku.wavvy.ui.viewmodels.SettingsViewModel

val LocalSettingsViewModel = compositionLocalOf<SettingsViewModel> {
    error("No SettingsViewModel provided")
}
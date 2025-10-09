package com.mirimomekiku.wavvy.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mirimomekiku.wavvy.instances.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {

    val enableMaterialYou = context.enableMaterialYouFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val appTheme = context.appThemeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.FOLLOW_SYSTEM)

    val playingNowTheme = context.playingNowThemeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayingNowTheme.DEFAULT)

    val showExtraDetails = context.showExtraDetailsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val enableFetchingArtistBiography = context.enableFetchingArtistBiographyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val showExtraControlsOnSheetBar = context.showExtraControlsOnSheetBarFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun toggleMaterialYou(enabled: Boolean) = viewModelScope.launch {
        toggleMaterialYou(context, enabled)
    }

    fun updateAppTheme(theme: AppTheme) = viewModelScope.launch {
        updateAppTheme(context, theme)
    }

    fun updatePlayingNowTheme(theme: PlayingNowTheme) = viewModelScope.launch {
        updatePlayingNowTheme(context, theme)
    }

    fun toggleExtraDetails(enabled: Boolean) = viewModelScope.launch {
        toggleExtraDetails(context, enabled)
    }

    fun toggleArtistBiography(enabled: Boolean) = viewModelScope.launch {
        toggleArtistBiography(context, enabled)
    }

    fun toggleExtraControls(enabled: Boolean) = viewModelScope.launch {
        toggleExtraControls(context, enabled)
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

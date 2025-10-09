package com.mirimomekiku.wavvy.instances

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class PlayingNowTheme(val value: Int) {
    DEFAULT(0), FULL(1), MINI(2), CARD(3), MP3(4);

    companion object {
        fun fromValue(value: Int): PlayingNowTheme =
            entries.find { it.value == value } ?: DEFAULT
    }
}

enum class AppTheme(val value: Int) {
    FOLLOW_SYSTEM(0), LIGHT(1), DARK(2);

    companion object {
        fun fromValue(value: Int): AppTheme =
            entries.find { it.value == value } ?: FOLLOW_SYSTEM
    }
}

object SettingsKeys {
    val enableMaterialYou = booleanPreferencesKey("enable_material_you")
    val appTheme = intPreferencesKey("app_theme")
    val playingNowTheme = intPreferencesKey("playing_now_theme")
    val showExtraDetails = booleanPreferencesKey("show_extra_details_on_playing_now")
    val enableFetchingArtistBiography = booleanPreferencesKey("enable_fetching_artist_biography")
    val showExtraControlsOnSheetBar = booleanPreferencesKey("show_extra_controls_on_sheet_bar")
}

val Context.enableMaterialYouFlow: Flow<Boolean>
    get() = dataStore.data.map { prefs ->
        prefs[SettingsKeys.enableMaterialYou] ?: true
    }

val Context.appThemeFlow: Flow<AppTheme>
    get() = dataStore.data.map { prefs ->
        val value = prefs[SettingsKeys.appTheme] ?: AppTheme.FOLLOW_SYSTEM.value
        AppTheme.fromValue(value)
    }

val Context.playingNowThemeFlow: Flow<PlayingNowTheme>
    get() = dataStore.data.map { prefs ->
        val value = prefs[SettingsKeys.playingNowTheme] ?: PlayingNowTheme.DEFAULT.value
        PlayingNowTheme.fromValue(value)
    }

val Context.showExtraDetailsFlow: Flow<Boolean>
    get() = dataStore.data.map { prefs ->
        prefs[SettingsKeys.showExtraDetails] ?: true
    }

val Context.enableFetchingArtistBiographyFlow: Flow<Boolean>
    get() = dataStore.data.map { prefs ->
        prefs[SettingsKeys.enableFetchingArtistBiography] ?: true
    }

val Context.showExtraControlsOnSheetBarFlow: Flow<Boolean>
    get() = dataStore.data.map { prefs ->
        prefs[SettingsKeys.showExtraControlsOnSheetBar] ?: true
    }

suspend fun toggleMaterialYou(context: Context, value: Boolean) {
    context.dataStore.edit { prefs ->
        prefs[SettingsKeys.enableMaterialYou] = value
    }
}

suspend fun updateAppTheme(context: Context, value: AppTheme) {
    context.dataStore.edit { prefs ->
        prefs[SettingsKeys.appTheme] = value.value
    }
}

suspend fun updatePlayingNowTheme(context: Context, value: PlayingNowTheme) {
    context.dataStore.edit { prefs ->
        prefs[SettingsKeys.playingNowTheme] = value.value
    }
}

suspend fun toggleExtraDetails(context: Context, value: Boolean) {
    context.dataStore.edit { prefs ->
        prefs[SettingsKeys.showExtraDetails] = value
    }
}

suspend fun toggleArtistBiography(context: Context, value: Boolean) {
    context.dataStore.edit { prefs ->
        prefs[SettingsKeys.enableFetchingArtistBiography] = value
    }
}

suspend fun toggleExtraControls(context: Context, value: Boolean) {
    context.dataStore.edit { prefs ->
        prefs[SettingsKeys.showExtraControlsOnSheetBar] = value
    }
}

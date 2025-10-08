package com.mirimomekiku.wavvy.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.net.toUri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.media3.common.MediaItem
import com.mirimomekiku.wavvy.MainActivity
import com.mirimomekiku.wavvy.R
import java.io.File

data class MusicWidgetState(
    val title: String = "No song playing",
    val artist: String = "",
    val isPlaying: Boolean = false,
    val artworkRes: Int = R.drawable.placeholder_artwork
)


class WidgetUpdater {
    companion object {
        suspend fun updateNowPlaying(context: Context, mediaItem: MediaItem?, uri: Uri) {
            val titleKey = stringPreferencesKey("song_title")
            val artistKey = stringPreferencesKey("artist")
            val artKey = stringPreferencesKey("album_art_path")

            val contentUri =
                getUriForFile(context, "${context.packageName}.fileprovider", File(uri.path))

            val glanceId = GlanceAppWidgetManager(context).getGlanceIds(MusicWidget::class.java)

            updateAppWidgetState(context, glanceId[0]) { prefs ->
                prefs[titleKey] = mediaItem?.mediaMetadata?.title.toString()
                prefs[artistKey] = mediaItem?.mediaMetadata?.artist.toString()
                prefs[artKey] = contentUri?.toString() ?: ""
            }

            MusicWidget().updateAll(context)
        }
    }
}

class MusicWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val title = prefs[stringPreferencesKey("song_title")] ?: "No song playing"
            val artist = prefs[stringPreferencesKey("artist")] ?: "Unknown artist"
            val artPath = prefs[stringPreferencesKey("album_art_path")] ?: ""

            MusicWidgetContent(
                title = title,
                artist = artist,
                artworkRes = artPath.toUri()
            )
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun MusicWidgetContent(
        title: String, artist: String, artworkRes: Uri
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth()
                .background(ColorProvider(R.color.ic_launcher_background)).padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(artworkRes),
                contentDescription = "Album art",
                modifier = GlanceModifier.size(48.dp)
            )

            Spacer(GlanceModifier.width(12.dp))

            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = title, style = TextStyle(color = ColorProvider(R.color.black))
                )
                Text(
                    text = artist, style = TextStyle(color = ColorProvider(R.color.black))
                )
            }
        }
    }
}

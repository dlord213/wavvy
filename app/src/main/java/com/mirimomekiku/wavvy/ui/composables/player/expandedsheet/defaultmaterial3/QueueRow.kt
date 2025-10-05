package com.mirimomekiku.wavvy.ui.composables.player.expandedsheet.defaultmaterial3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.mirimomekiku.wavvy.extensions.KEY_DOMINANT_COLOR
import com.mirimomekiku.wavvy.extensions.getDominantColor
import com.mirimomekiku.wavvy.helpers.getDominantColorAdjusted
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun QueueRow(mediaItem: MediaItem, mediaController: MediaController) {

    val context = LocalContext.current
    val playbackViewModel = LocalPlaybackViewModel.current
    val currentItem by playbackViewModel.currentMediaItem.collectAsStateWithLifecycle()
    val bgColor = mediaItem.getDominantColor()?.let { Color(it) } ?: Color.DarkGray
    val baseColor = getDominantColorAdjusted(context, mediaItem.mediaMetadata.artworkUri)

    fun playAudio() {
        val existingIndex = (0 until mediaController.mediaItemCount).firstOrNull { i ->
            mediaController.getMediaItemAt(i).mediaId == mediaItem.mediaId
        }

        if (existingIndex != null) {
            mediaController.seekTo(existingIndex, 0)
        } else {
            mediaController.addMediaItem(mediaItem)
            val newIndex = mediaController.mediaItemCount - 1
            mediaController.seekTo(newIndex, 0)
        }

        playbackViewModel.updateBottomBarColor(bgColor)
        playbackViewModel.updateCurrentMediaItem(mediaItem)

        mediaController.prepare()
        mediaController.play()
    }

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                playAudio()
            }
            .background(
                if (mediaItem.mediaMetadata.title.toString() == currentItem?.mediaMetadata?.title.toString()) baseColor
                else Color.Transparent
            )
            .padding(8.dp)
            .fillMaxWidth()) {

        val dominantColor = (mediaItem.mediaMetadata.extras?.getInt(KEY_DOMINANT_COLOR)
            ?: 0x484848) or 0xFF000000.toInt()

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(mediaItem.mediaMetadata.artworkUri).crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED).diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = "Album art",
            placeholder = ColorPainter(Color(dominantColor)),
            error = ColorPainter(Color(dominantColor)),
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .size(48.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                mediaItem.mediaMetadata.title.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                mediaItem.mediaMetadata.artist.toString(),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

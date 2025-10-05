package com.mirimomekiku.wavvy.extensions

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController

const val KEY_DOMINANT_COLOR = "dominantColor"

val Player.currentMediaItems: List<MediaItem>
    get() = List(mediaItemCount, ::getMediaItemAt)

fun Player.updatePlaylist(incoming: List<MediaItem>) {
    val oldMediaIds = currentMediaItems.map { it.mediaId }.toSet()
    val itemsToAdd = incoming.filterNot { item -> item.mediaId in oldMediaIds }
    addMediaItems(itemsToAdd)
}

fun MediaItem.getDominantColor(): Int? {
    return mediaMetadata.extras?.getInt(KEY_DOMINANT_COLOR)
}


fun MediaController.shuffledQueue(): List<MediaItem> {
    val timeline = currentTimeline
    if (timeline.isEmpty) return emptyList()

    val fullQueue = mutableListOf<MediaItem>()
    val visited = mutableSetOf<Int>()

    var windowIndex = timeline.getFirstWindowIndex(shuffleModeEnabled)

    while (visited.size < mediaItemCount) {
        fullQueue.add(getMediaItemAt(windowIndex))
        visited.add(windowIndex)

        windowIndex = timeline.getNextWindowIndex(windowIndex, repeatMode, shuffleModeEnabled)
        if (windowIndex == C.INDEX_UNSET) break
    }

    return fullQueue
}


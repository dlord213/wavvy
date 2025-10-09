package com.mirimomekiku.wavvy.db.entity

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mirimomekiku.wavvy.extensions.KEY_DOMINANT_COLOR

data class PlaylistItem(
    @PrimaryKey val mediaId: String,
    val uri: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val composer: String?,
    val artworkUri: String?,
    val dominantColor: Int?,
    val durationMs: Long?,
    val mimeType: String?,
    val releaseYear: Int?,
    val trackNumber: Int?,
    val discNumber: Int?,
    val bitrate: Int?,
    val sizeBytes: Long?,
    val filePath: String?,
    val dateAdded: Long?,
    val dateModified: Long?
)

@Entity(tableName = "playlists")
data class Playlists(
    @PrimaryKey val playlistId: String,
    val playlistName: String,
    val items: List<PlaylistItem>
)

// Convert PlaylistItem → MediaItem
fun PlaylistItem.toMediaItem(): MediaItem {
    val extras = android.os.Bundle().apply {
        dominantColor?.let { putInt(KEY_DOMINANT_COLOR, it) }
        discNumber?.let { putInt("disc_number", it) }
        trackNumber?.let { putInt("track_number", it) }
        bitrate?.let { putInt("bitrate", it) }
        sizeBytes?.let { putLong("size_bytes", it) }
        filePath?.let { putString("file_path", it) }
        dateAdded?.let { putLong("date_added", it) }
        dateModified?.let { putLong("date_modified", it) }
        mimeType?.let { putString("mime_type", it) }
        composer?.let { putString("composer", it) }
    }

    return MediaItem.Builder()
        .setMediaId(mediaId)
        .setUri(uri)
        .setMimeType(mimeType)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setComposer(composer)
                .setArtworkUri(artworkUri?.toUri())
                .setDurationMs(durationMs ?: 0L)
                .setTrackNumber(trackNumber)
                .setReleaseYear(releaseYear)
                .setExtras(extras)
                .build()
        )
        .build()
}

// Convert MediaItem → PlaylistItem
fun MediaItem.toPlaylistEntity(): PlaylistItem {
    val extras = mediaMetadata.extras
    return PlaylistItem(
        mediaId = mediaId,
        uri = localConfiguration?.uri.toString(),
        title = mediaMetadata.title?.toString() ?: "Unknown",
        artist = mediaMetadata.artist?.toString(),
        album = mediaMetadata.albumTitle?.toString(),
        composer = mediaMetadata.composer?.toString(),
        artworkUri = mediaMetadata.artworkUri?.toString(),
        dominantColor = extras?.getInt(KEY_DOMINANT_COLOR),
        durationMs = mediaMetadata.durationMs,
        mimeType = extras?.getString("mime_type"),
        releaseYear = mediaMetadata.releaseYear,
        trackNumber = extras?.getInt("track_number"),
        discNumber = extras?.getInt("disc_number"),
        bitrate = extras?.getInt("bitrate"),
        sizeBytes = extras?.getLong("size_bytes"),
        filePath = extras?.getString("file_path"),
        dateAdded = extras?.getLong("date_added"),
        dateModified = extras?.getLong("date_modified")
    )
}

// Helper conversions
fun List<MediaItem>.toPlaylistItems() = map { it.toPlaylistEntity() }
fun List<PlaylistItem>.toMediaItems() = map { it.toMediaItem() }

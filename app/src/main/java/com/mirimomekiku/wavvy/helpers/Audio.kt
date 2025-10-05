import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.mirimomekiku.wavvy.extensions.KEY_DOMINANT_COLOR
import com.mirimomekiku.wavvy.helpers.getAlbumArtURIDominantColorAdjusted
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioFile(
    val id: Long,
    val displayName: String,
    val title: String?,
    val artist: String?,
    val album: String?,
    val albumId: Long?,
    val albumArtUri: Uri?,
    val duration: Long?,
    val size: Long?,
    val mimeType: String?,
    val dateAdded: Long?,
    val year: Int?,
    val contentUri: Uri,
    val dominantColor: Int? = null
) : Parcelable

data class ArtistWithSongs(
    val artist: String, val songs: List<MediaItem>
)

data class AlbumWithSongs(
    val album: String, val songs: List<MediaItem>
)

data class FolderWithSongs(
    val folder: String, val songs: List<MediaItem>
)

data class ArtistWithAlbums(
    val artist: String,
    val albums: List<AlbumWithSongs>
)


fun formatDurationMs(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}

fun getAlbumArtUri(albumId: Long?): Uri? {
    return if (albumId != null) {
        Uri.parse("content://media/external/audio/albumart/$albumId")
    } else null
}


private const val KEY_DISC_NUMBER = "disc_number"
private const val KEY_TRACK_NUMBER = "track_number"

fun getAllAudioFiles(context: Context): List<MediaItem> {
    val mediaItemList = mutableListOf<MediaItem>()

    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.COMPOSER,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.TRACK,
        "bitrate", // Some devices store it as lowercase or missing, so we handle gracefully
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.DATE_MODIFIED,
        MediaStore.Audio.Media.IS_MUSIC
    )

    val query = context.contentResolver.query(
        collection,
        projection,
        "${MediaStore.Audio.Media.IS_MUSIC} != 0",
        null,
        "${MediaStore.Audio.Media.DATE_ADDED} DESC"
    )

    query?.use { cursor ->
        fun safeString(col: Int): String? =
            if (col >= 0 && !cursor.isNull(col)) cursor.getString(col) else null

        fun safeInt(col: Int): Int? =
            if (col >= 0 && !cursor.isNull(col)) cursor.getInt(col) else null

        fun safeLong(col: Int): Long? =
            if (col >= 0 && !cursor.isNull(col)) cursor.getLong(col) else null

        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val composerCol = cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
        val yearCol = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
        val trackCol = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK)
        val bitrateCol = cursor.getColumnIndex("bitrate")
        val sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
        val dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
        val dateAddedCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
        val dateModifiedCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idCol)
            val contentUri =
                Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString())

            val albumId = safeLong(albumIdCol)
            val albumArtUri = getAlbumArtUri(albumId)
            val dominantColor = getAlbumArtURIDominantColorAdjusted(context, albumArtUri, 1f, 0.7f)

            // --- Parse track/disc numbers safely ---
            var discNumber: Int? = null
            var trackNumber: Int? = null
            safeInt(trackCol)?.let { v ->
                when {
                    v >= 1000 -> {
                        discNumber = v / 1000
                        trackNumber = v % 1000
                    }

                    (v ushr 16) > 0 -> {
                        discNumber = v ushr 16
                        trackNumber = v and 0xFFFF
                    }

                    else -> trackNumber = v
                }
            }
            if (trackNumber == null) trackNumber = 0
            if (discNumber == null) discNumber = 0

            val extras = Bundle().apply {
                dominantColor.let { putInt(KEY_DOMINANT_COLOR, it) }
                putInt(KEY_DISC_NUMBER, discNumber!!)
                putInt(KEY_TRACK_NUMBER, trackNumber!!)
                safeString(composerCol)?.let { putString("composer", it) }
                safeInt(bitrateCol)?.let { putInt("bitrate", it) }
                safeLong(sizeCol)?.let { putLong("size_bytes", it) }
                safeString(dataCol)?.let { putString("file_path", it) }
                safeLong(dateAddedCol)?.let { putLong("date_added", it) }
                safeLong(dateModifiedCol)?.let { putLong("date_modified", it) }
                safeString(mimeCol)?.let { putString("mime_type", it) }
            }

            val metadata = MediaMetadata.Builder()
                .setTitle(safeString(titleCol) ?: "Unknown Title")
                .setArtist(safeString(artistCol) ?: "Unknown Artist")
                .setAlbumTitle(safeString(albumCol) ?: "Unknown Album")
                .setArtworkUri(albumArtUri)
                .setComposer(safeString(composerCol))
                .setReleaseYear(safeInt(yearCol))
                .setDurationMs(safeLong(durationCol) ?: 0L)
                .setTrackNumber(trackNumber)
                .setExtras(extras)
                .build()

            val mediaItem = MediaItem.Builder()
                .setUri(contentUri)
                .setMediaId(id.toString())
                .setMimeType(safeString(mimeCol))
                .setMediaMetadata(metadata)
                .build()

            mediaItemList.add(mediaItem)
        }
    }

    return mediaItemList.sortedWith(
        compareBy(
            { it.mediaMetadata.extras?.getInt(KEY_DISC_NUMBER) ?: 0 },
            { it.mediaMetadata.extras?.getInt(KEY_TRACK_NUMBER) ?: 0 },
            { it.mediaMetadata.title?.toString() ?: it.mediaMetadata.albumTitle?.toString() ?: "" }
        )
    )
}

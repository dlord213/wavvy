package com.mirimomekiku.wavvy.ui.composables.player.expandedsheet.defaultmaterial3

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import coil.compose.AsyncImage
import com.mirimomekiku.wavvy.enums.Screens
import com.mirimomekiku.wavvy.extensions.currentMediaItems
import com.mirimomekiku.wavvy.helpers.getDominantColorAdjusted
import com.mirimomekiku.wavvy.helpers.openEqualizer
import com.mirimomekiku.wavvy.ui.composables.MediaSlider
import com.mirimomekiku.wavvy.ui.composables.SelectableIconButton
import com.mirimomekiku.wavvy.ui.compositions.LocalFavoriteViewModel
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

// FOR _AUDIO_BOTTOM_SHEET USE ONLY
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackInfo(
    mediaController: MediaController,
    toggleFavorites: () -> Unit,
    pagerState: PagerState,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState
) {
    val favoriteViewModel = LocalFavoriteViewModel.current
    val navController = LocalNavController.current
    val playbackViewModel = LocalPlaybackViewModel.current
    val context = LocalContext.current

    val favoriteItem by favoriteViewModel.getFavoriteById(
        mediaController.currentMediaItem?.mediaId ?: ""
    ).collectAsState(initial = null)
    val isFavorite = favoriteItem != null

    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    val isPlaying by playbackViewModel.isPlaying.collectAsStateWithLifecycle()
    val mediaItem by playbackViewModel.currentMediaItem.collectAsStateWithLifecycle()
    val isShuffled by playbackViewModel.isShuffledEnabled.collectAsStateWithLifecycle()
    val repeatMode by playbackViewModel.isRepeatEnabled.collectAsStateWithLifecycle()

    val baseColor = getDominantColorAdjusted(context, mediaItem?.mediaMetadata?.artworkUri)
    val textColor = if (baseColor.luminance() > 0.5f) Color.Black else Color.White
    val contentColor = if (baseColor.luminance() > 0.5f) Color.Black else Color.White
    val disabledTextColor = baseColor.copy(alpha = 0.3f)   // faded background for disabled
    val disabledTextContentColor = contentColor.copy(alpha = 0.3f) // faded text/icon

    fun seekBack() {
        val currentIndex = mediaController.currentMediaItemIndex

        if (mediaController.isCurrentMediaItemSeekable && mediaController.currentPosition > 5000) {
            mediaController.seekBack() // seek within track
        } else if (currentIndex > 0) {
            mediaController.seekToPrevious()
        }
    }

    fun seekForward() {
        val currentIndex = mediaController.currentMediaItemIndex

        val lastIndex = mediaController.currentMediaItems.lastIndex
        if (mediaController.isCurrentMediaItemSeekable && mediaController.currentPosition + 10000 < mediaController.duration) {
            mediaController.seekForward()
        } else if (currentIndex < lastIndex) {
            mediaController.seekToNext()
        }
    }

    fun shareAudioFile() {
        if (mediaItem == null) return

        val filePath = mediaItem?.mediaMetadata?.extras?.getString("file_path") ?: return
        val file = File(filePath)
        if (!file.exists()) return

        val audioUri = FileProvider.getUriForFile(
            context, "com.mirimomekiku.wavvy.fileprovider", file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, audioUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "Share audio")
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                model = mediaItem?.mediaMetadata?.artworkUri,
                contentDescription = "Album art",
                placeholder = ColorPainter(Color(0xFF484848)),
                error = ColorPainter(Color(0xFF484848)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .aspectRatio(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = mediaItem?.mediaMetadata?.title.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = mediaItem?.mediaMetadata?.artist.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                SelectableIconButton(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorites",
                    selected = isFavorite,
                    onClick = toggleFavorites
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                FilledTonalButton(
                    onClick = { openEqualizer(context) }, colors = ButtonColors(
                        containerColor = baseColor,
                        contentColor = textColor,
                        disabledTextColor,
                        disabledTextContentColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Equalizer,
                        contentDescription = "System/device equalizer"
                    )
                    Text(
                        "Equalizer",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                FilledTonalButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                2
                            )
                        }
                    }, colors = ButtonColors(
                        containerColor = baseColor,
                        contentColor = textColor,
                        disabledTextColor,
                        disabledTextContentColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info, contentDescription = "Song info"
                    )
                    Text(
                        "Info",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                FilledTonalButton(
                    onClick = { shareAudioFile() }, colors = ButtonColors(
                        containerColor = baseColor,
                        contentColor = textColor,
                        disabledTextColor,
                        disabledTextContentColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share, contentDescription = "Share audio file"
                    )
                    Text(
                        "Share",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            MediaSlider(mediaController, baseColor)
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(
                    24.dp, Alignment.CenterHorizontally
                )
            ) {
                IconButton(onClick = {
                    mediaController.seekToPrevious()
                }, modifier = Modifier.size(64.dp)) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Go to previous track",
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(onClick = {
                    if (isPlaying) mediaController.pause() else mediaController.play()
                }, modifier = Modifier.size(64.dp)) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(onClick = {
                    mediaController.seekToNext()
                }, modifier = Modifier.size(64.dp)) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Go to next track",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                TextButton(onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            1
                        )
                    }
                }) {
                    Text(
                        "Queue", style = MaterialTheme.typography.bodyMedium, color = textColor
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = {
                        if (isShuffled) {
                            mediaController.setShuffleModeEnabled(false)
                        } else {
                            mediaController.setShuffleModeEnabled(true)
                        }
                    }) {
                    Icon(
                        imageVector = if (isShuffled) Icons.Filled.ShuffleOn else Icons.Filled.Shuffle,
                        contentDescription = "Shuffle queue",
                    )
                }
                IconButton(
                    onClick = { playbackViewModel.toggleRepeatMode(mediaController) }) {
                    Icon(
                        imageVector = when (repeatMode) {
                            Player.REPEAT_MODE_OFF -> Icons.Filled.Repeat      // normal repeat icon, off
                            Player.REPEAT_MODE_ONE -> Icons.Filled.RepeatOne  // repeat-one icon
                            Player.REPEAT_MODE_ALL -> Icons.Filled.RepeatOn      // repeat all icon
                            else -> Icons.Filled.Repeat
                        }, contentDescription = "Repeat Mode"
                    )

                }
                IconButton(
                    onClick = {
                        menuExpanded = true
                    }) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "Repeat Mode",
                    )
                    DropdownMenu(
                        expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Go to album") },
                            onClick = {
                                menuExpanded = false
                                scope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                                playbackViewModel.selectAlbum(mediaItem?.mediaMetadata?.albumTitle.toString())
                                navController.navigate(route = Screens.Album.name) {
                                    popUpTo(Screens.Album.name)
                                }
                            },
                        )
                        DropdownMenuItem(text = { Text("Go to artist") }, onClick = {
                            menuExpanded = false
                            scope.launch {
                                scaffoldState.bottomSheetState.partialExpand()
                            }
                            playbackViewModel.selectArtist(mediaItem?.mediaMetadata?.artist.toString())
                            navController.navigate(route = Screens.Artist.name) {
                                popUpTo(Screens.Artist.name)
                            }
                        })
                        DropdownMenuItem(text = { Text("Clear playing queue") }, onClick = {
                            menuExpanded = false
                            scope.launch {
                                scaffoldState.bottomSheetState.partialExpand()
                            }
                            mediaController.clearMediaItems()
                        })
                    }
                }
            }
        }
    }
}
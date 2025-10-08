package com.mirimomekiku.wavvy.ui.composables

import Favorite
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.extensions.getDominantColor
import com.mirimomekiku.wavvy.ui.composables.player.expandedsheet.defaultmaterial3.PlaybackArtistInfo
import com.mirimomekiku.wavvy.ui.composables.player.expandedsheet.defaultmaterial3.PlaybackInfo
import com.mirimomekiku.wavvy.ui.composables.player.expandedsheet.defaultmaterial3.PlaybackQueue
import com.mirimomekiku.wavvy.ui.compositions.LocalFavoriteViewModel
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheetBar(
    scaffoldState: BottomSheetScaffoldState,
    mediaController: MediaController,
    scope: CoroutineScope
) {
    val favoriteViewModel = LocalFavoriteViewModel.current
    val playbackViewModel = LocalPlaybackViewModel.current
    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 0)
    val currentMediaItem by playbackViewModel.currentMediaItem.collectAsStateWithLifecycle()

    BackHandler(enabled = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
        scope.launch {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    DisposableEffect(Unit) {
        playbackViewModel.attachController(mediaController, context)

        onDispose { }
    }

    val containerColor by playbackViewModel.bottomBarColor.collectAsStateWithLifecycle()
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    val isSheetExpanded = scaffoldState.bottomSheetState.hasExpandedState

    val animatedColor by animateColorAsState(
        targetValue = containerColor ?: MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "sheetColorAnimation",
    )


    fun toggleFavorite() {
        isFavorite = !isFavorite
        val favoriteItem = Favorite(
            mediaId = playbackViewModel.currentMediaItem.value?.mediaId
                ?: return,
            title = playbackViewModel.currentMediaItem.value?.mediaMetadata?.title.toString(),
            artist = playbackViewModel.currentMediaItem.value?.mediaMetadata?.artist.toString(),
            artworkUri = playbackViewModel.currentMediaItem.value?.mediaMetadata?.artworkUri.toString(),
            uri = playbackViewModel.currentMediaItem.value?.localConfiguration?.uri.toString(),
            dominantColor = playbackViewModel.currentMediaItem.value?.getDominantColor(),
            album = playbackViewModel.currentMediaItem.value?.mediaMetadata?.albumTitle.toString(),
            composer = playbackViewModel.currentMediaItem.value?.mediaMetadata?.composer.toString(),
            durationMs = playbackViewModel.currentMediaItem.value?.mediaMetadata?.durationMs,
            mimeType = playbackViewModel.currentMediaItem.value?.mediaMetadata?.extras?.getString(
                "mime_type"
            ),
            releaseYear = playbackViewModel.currentMediaItem.value?.mediaMetadata?.releaseYear,
            trackNumber = playbackViewModel.currentMediaItem.value?.mediaMetadata?.trackNumber,
            discNumber = playbackViewModel.currentMediaItem.value?.mediaMetadata?.discNumber,
            bitrate = playbackViewModel.currentMediaItem.value?.mediaMetadata?.extras?.getInt(
                "bitrate"
            ),
            sizeBytes = playbackViewModel.currentMediaItem.value?.mediaMetadata?.extras?.getLong(
                "size_bytes"
            ),
            filePath = playbackViewModel.currentMediaItem.value?.mediaMetadata?.extras?.getString(
                "file_path"
            ),
            dateAdded = playbackViewModel.currentMediaItem.value?.mediaMetadata?.extras?.getLong(
                "date_added"
            ),
            dateModified = playbackViewModel.currentMediaItem.value?.mediaMetadata?.extras?.getLong(
                "date_modified"
            )
        )
        favoriteViewModel.toggleFavorite(favoriteItem)
    }

    if (isSheetExpanded && mediaController.isConnected && currentMediaItem != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedColor)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                beyondViewportPageCount = 1,
            ) { page ->
                when (page) {
                    0 -> PlaybackInfo(
                        toggleFavorites = {
                            toggleFavorite()
                        },
                        pagerState = pagerState,
                        scope = scope,
                        mediaController = mediaController,
                        scaffoldState = scaffoldState
                    )

                    1 -> PlaybackQueue(
                        mediaController = mediaController
                    )

                    2 -> {
                        PlaybackArtistInfo()
                    }
                }
            }
        }
    }
}

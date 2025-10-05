package com.mirimomekiku.wavvy.ui.composables.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.ui.composables.MediaItemRow
import com.mirimomekiku.wavvy.ui.compositions.LocalFavoriteViewModel
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@Composable
fun FavoritePage(mediaController: MediaController) {
    val favoritesViewModel = LocalFavoriteViewModel.current

    val favorites by favoritesViewModel.mediaItems.collectAsState(initial = emptyList())
    val lazyListState = LazyListState()


    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        items(favorites, key = { it.mediaId }) { audio ->
            MediaItemRow(audio, mediaController)
        }
    }
}
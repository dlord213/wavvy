package com.mirimomekiku.wavvy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.media3.session.MediaController
import com.mirimomekiku.wavvy.enums.HomeScreenPages
import com.mirimomekiku.wavvy.enums.Screens
import com.mirimomekiku.wavvy.ui.composables.HeadingLogo
import com.mirimomekiku.wavvy.ui.composables.SelectableButton
import com.mirimomekiku.wavvy.ui.composables.SelectableIconButton
import com.mirimomekiku.wavvy.ui.composables.pages.AlbumsPage
import com.mirimomekiku.wavvy.ui.composables.pages.ArtistsPage
import com.mirimomekiku.wavvy.ui.composables.pages.FavoritePage
import com.mirimomekiku.wavvy.ui.composables.pages.SongsPage
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    mediaController: MediaController,
) {
    val navController = LocalNavController.current
    val playbackViewModel = LocalPlaybackViewModel.current
    val context = LocalContext.current

    var uiState by rememberSaveable { mutableStateOf("Songs") }
    val textFieldState = rememberTextFieldState()

    val pagerState = rememberPagerState(pageCount = { 4 }, initialPage = 1)
    val pagerCoroutineScope = rememberCoroutineScope()

    val headingPagerState = rememberPagerState(pageCount = { 2 }, initialPage = 0)
    val headingCoroutineScope = rememberCoroutineScope()

    val onPageClick: (HomeScreenPages) -> Unit = { page ->
        pagerCoroutineScope.launch {
            pagerState.animateScrollToPage(page.ordinal)
        }
    }

    DisposableEffect(Unit) {
        playbackViewModel.attachController(mediaController, context)

        onDispose { }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            uiState = when (page) {
                0 -> "Favorites"
                1 -> "Songs"
                2 -> "Artists"
                3 -> "Albums"
                else -> uiState
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = headingPagerState,
            modifier = Modifier.padding(horizontal = 6.dp),
            userScrollEnabled = false,
            beyondViewportPageCount = 1,
        ) { page ->
            when (page) {
                0 -> {
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                12.dp, Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            HeadingLogo()
                            Spacer(modifier = Modifier.weight(1f, fill = true))
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search music",
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .clickable {
                                        headingCoroutineScope.launch {
                                            headingPagerState.animateScrollToPage(1)
                                        }
                                    })
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp, Alignment.Start
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp)
                        ) {
                            HomeScreenPages.entries.forEach { page ->
                                if (page.icon != null) { // Check if it's the special icon button
                                    SelectableIconButton(
                                        imageVector = if (uiState == page.title) page.selectedIcon!! else page.unselectedIcon!!,
                                        contentDescription = page.title,
                                        selected = uiState == page.title,
                                        onClick = { onPageClick(page) })
                                } else {
                                    SelectableButton(
                                        text = page.title,
                                        selected = uiState == page.title,
                                        onClick = { onPageClick(page) })
                                }
                            }
                        }
                    }
                }

                1 -> {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close search bar",
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraLarge)
                                .clickable {
                                    headingCoroutineScope.launch {
                                        headingPagerState.animateScrollToPage(0)
                                    }
                                })

                        TextField(
                            state = textFieldState,
                            label = { Text("Search") },
                            textStyle = MaterialTheme.typography.labelLarge,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = TextFieldDefaults.colors(
                                cursorColor = MaterialTheme.colorScheme.primary,
                                disabledIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .fillMaxWidth()
                                .padding(12.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            onKeyboardAction = {
                                val query = textFieldState.text.trim()
                                if (query.isNotEmpty()) {
                                    headingCoroutineScope.launch {
                                        headingPagerState.animateScrollToPage(0)
                                    }
                                    playbackViewModel.updateSearchQuery(query.toString())
                                    textFieldState.clearText()
                                    navController.navigate(Screens.Search.name)
                                }
                            },
                        )
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            modifier = Modifier
                .weight(6f, fill = true)
                .padding(horizontal = 6.dp)
        ) { page ->
            when (page) {
                0 -> {
                    FavoritePage(mediaController)
                }

                1 -> {
                    SongsPage(mediaController)
                }

                2 -> {
                    ArtistsPage(mediaController)

                }

                3 -> {
                    AlbumsPage(mediaController)
                }
            }
        }
    }
}
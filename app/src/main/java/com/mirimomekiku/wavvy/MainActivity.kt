package com.mirimomekiku.wavvy

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.common.util.concurrent.ListenableFuture
import com.mirimomekiku.wavvy.db.AppDatabase
import com.mirimomekiku.wavvy.enums.Screens
import com.mirimomekiku.wavvy.helpers.getDominantColorAdjusted
import com.mirimomekiku.wavvy.services.PlaybackService
import com.mirimomekiku.wavvy.ui.composables.AppBottomSheetBar
import com.mirimomekiku.wavvy.ui.composables.AppBottomSheetHandle
import com.mirimomekiku.wavvy.ui.compositions.LocalFavoriteViewModel
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel
import com.mirimomekiku.wavvy.ui.screens.AlbumScreen
import com.mirimomekiku.wavvy.ui.screens.ArtistScreen
import com.mirimomekiku.wavvy.ui.screens.HomeScreen
import com.mirimomekiku.wavvy.ui.screens.SearchScreen
import com.mirimomekiku.wavvy.ui.screens.StartScreen
import com.mirimomekiku.wavvy.ui.theme.WavvyTheme
import com.mirimomekiku.wavvy.ui.viewmodels.FavoriteViewModel
import com.mirimomekiku.wavvy.ui.viewmodels.FavoriteViewModelFactory
import com.mirimomekiku.wavvy.ui.viewmodels.PlaybackViewModel


class MainActivity : ComponentActivity() {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    override fun onStart() {
        super.onStart()

        val sessionToken = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        controllerFuture.addListener({
            mediaController = controllerFuture.get()

            val playbackViewModel: PlaybackViewModel =
                ViewModelProvider(this)[PlaybackViewModel::class.java]

            playbackViewModel.attachController(mediaController!!, this)
            playbackViewModel.updatePlayingState(mediaController!!.isPlaying)
            playbackViewModel.updateCurrentMediaItem(mediaController!!.currentMediaItem)

        }, ContextCompat.getMainExecutor(this))

    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val playbackViewModel: PlaybackViewModel = viewModel()
            val db = Room.databaseBuilder(
                applicationContext, AppDatabase::class.java, "favorites"
            ).build()
            val dao = db.favoriteDao()

            val favoriteViewModel: FavoriteViewModel = viewModel(
                factory = FavoriteViewModelFactory(dao)
            )

            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            val scaffoldState = rememberBottomSheetScaffoldState()

            WavvyTheme {
                CompositionLocalProvider(LocalNavController provides navController) {
                    CompositionLocalProvider(LocalPlaybackViewModel provides playbackViewModel) {
                        CompositionLocalProvider(LocalFavoriteViewModel provides favoriteViewModel) {

                            val currentMediaItem by playbackViewModel.currentMediaItem.collectAsStateWithLifecycle()
                            val bgColor by playbackViewModel.bottomBarColor.collectAsStateWithLifecycle()

                            val baseColor = getDominantColorAdjusted(
                                context, currentMediaItem?.mediaMetadata?.artworkUri
                            )

                            val textColor =
                                if (baseColor.luminance() > 0.5f) Color.Black else Color.White

                            val animatedBaseColor by animateColorAsState(
                                targetValue = bgColor ?: MaterialTheme.colorScheme.background,
                                label = "sheetBaseColorAnimation",
                            )
                            val animatedTextColor by animateColorAsState(
                                targetValue = textColor,
                                label = "sheetTextColorAnimation",
                            )

                            BottomSheetScaffold(
                                modifier = Modifier
                                    .animateContentSize()
                                    .navigationBarsPadding(),
                                scaffoldState = scaffoldState,
                                sheetPeekHeight = if (currentMediaItem != null) 68.dp else 0.dp,
                                sheetDragHandle = {
                                    mediaController?.let {
                                        AppBottomSheetHandle(
                                            scaffoldState = scaffoldState,
                                            mediaController = it,
                                            scope = scope
                                        )
                                    }
                                },
                                sheetContent = {
                                    mediaController?.let {
                                        AppBottomSheetBar(
                                            scaffoldState, it, scope
                                        )
                                    }
                                },
                                sheetContainerColor = animatedBaseColor,
                                sheetContentColor = animatedTextColor,
                            ) { innerPadding ->
                                NavHost(
                                    navController = navController,
                                    startDestination = Screens.Start.name,
                                    modifier = Modifier
                                        .padding(innerPadding)
                                        .padding(top = 28.dp)
                                ) {
                                    composable(route = Screens.Start.name) {
                                        StartScreen()
                                    }
                                    composable(route = Screens.Home.name) {
                                        if (mediaController != null) {
                                            HomeScreen(mediaController = mediaController!!)
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        }
                                    }
                                    composable(route = Screens.Artist.name) {
                                        if (mediaController != null) {
                                            ArtistScreen(mediaController = mediaController!!)
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        }
                                    }
                                    composable(route = Screens.Album.name) {
                                        if (mediaController != null) {
                                            AlbumScreen(mediaController = mediaController!!)
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        }
                                    }
                                    composable(route = Screens.Search.name) {
                                        if (mediaController != null) {
                                            SearchScreen(mediaController = mediaController!!)
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
    }
}

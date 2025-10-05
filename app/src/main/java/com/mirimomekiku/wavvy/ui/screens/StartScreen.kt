package com.mirimomekiku.wavvy.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mirimomekiku.wavvy.enums.Screens
import com.mirimomekiku.wavvy.ui.composables.HeadingLogo
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalPlaybackViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val playbackViewModel = LocalPlaybackViewModel.current

    val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else Manifest.permission.READ_EXTERNAL_STORAGE

    val audioState = rememberPermissionState(audioPermission)
    val allGranted = audioState.status.isGranted

    LaunchedEffect(audioState.status.isGranted) {
        if (audioState.status.isGranted) {
            playbackViewModel.loadAudioFiles(context)
            navController.navigate(route = Screens.Home.name) {
                popUpTo(Screens.Start.name) {
                    inclusive = true
                }
            }
        }
    }

    if (!audioState.status.isGranted) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeadingLogo()

            Column(
                modifier = Modifier
                    .weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Storage,
                    contentDescription = "Storage access",
                    modifier = Modifier.size(128.dp)
                )
                Text(
                    "To play and organize your songs, Wavvy needs permission to access audio files saved on your device.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (allGranted) {
                        navController.navigate(Screens.Home.name)
                    } else {

                        audioState.launchPermissionRequest()
                        Toast.makeText(
                            context,
                            "Please grant all permissions to continue.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Listen to some music")
            }
        }
    }
}

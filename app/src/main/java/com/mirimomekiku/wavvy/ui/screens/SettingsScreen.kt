package com.mirimomekiku.wavvy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.compositions.LocalSettingsViewModel

@Composable
fun SettingsScreen() {
    val navController = LocalNavController.current
    val settingsViewModel = LocalSettingsViewModel.current

    val materialYou by settingsViewModel.enableMaterialYou.collectAsState()
    val appTheme by settingsViewModel.appTheme.collectAsState()
    val showExtraControlsOnSheetBar by settingsViewModel.showExtraControlsOnSheetBar.collectAsState()
    val showExtraDetails by settingsViewModel.showExtraDetails.collectAsState()
    val enableFetchingArtistBiography by settingsViewModel.enableFetchingArtistBiography.collectAsState()
    val enableFetchingLyrics by settingsViewModel.enableFetchingLyrics.collectAsState()


    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
            Text(
                "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable {}
        ) {
            Icon(
                imageVector = Icons.Filled.ColorLens,
                contentDescription = "Set app theme",
                modifier = Modifier.padding(start = 12.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Text("Set app theme", style = MaterialTheme.typography.bodyLarge)
                Text(
                    when (appTheme.name) {
                        "FOLLOW_SYSTEM" -> "System default"
                        "LIGHT" -> "Light"
                        "DARK" -> "Dark"
                        else -> ""
                    }, style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                Text("Material You")
                Text("Enables Material You colors", style = MaterialTheme.typography.labelSmall)
            }
            Switch(
                checked = materialYou,
                onCheckedChange = { settingsViewModel.toggleMaterialYou(it) }
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                Text("Extra controls on sheet bar")
                Text(
                    "Adds previous/next track button on sheet bar",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Switch(
                checked = showExtraControlsOnSheetBar,
                onCheckedChange = { settingsViewModel.toggleExtraControls(it) }
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                Text("Show extra details on expanded bottom sheet bar")
                Text(
                    "Shows details like bitrate/audio type & etc.",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Switch(
                checked = showExtraDetails,
                onCheckedChange = { settingsViewModel.toggleExtraDetails(it) }
            )
        }


        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                Text("Enable fetching artist biography")
                Text(
                    "Enables auto-fetch on artist biography (requires internet connection)",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Switch(
                checked = enableFetchingArtistBiography,
                onCheckedChange = { settingsViewModel.toggleArtistBiography(it) }
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                Text("Enable fetching lyrics")
                Text(
                    "Enables auto-fetch on synced song's lyrics (requires internet connection)",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Switch(
                checked = enableFetchingLyrics,
                onCheckedChange = { settingsViewModel.toggleLyrics(it) }
            )
        }
    }
}

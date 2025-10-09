package com.mirimomekiku.wavvy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mirimomekiku.wavvy.ui.compositions.LocalNavController
import com.mirimomekiku.wavvy.ui.viewmodels.SettingsViewModel
import com.mirimomekiku.wavvy.ui.viewmodels.SettingsViewModelFactory

@Composable
fun SettingsScreen() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModelFactory(context))

    val materialYou by settingsViewModel.enableMaterialYou.collectAsState()
    val appTheme by settingsViewModel.appTheme.collectAsState()
    val playingNowTheme by settingsViewModel.playingNowTheme.collectAsState()

    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                Text(appTheme.name, style = MaterialTheme.typography.labelSmall)
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Material You")
            Spacer(modifier = Modifier.weight(1f, fill = true))
            Switch(
                checked = materialYou,
                onCheckedChange = { settingsViewModel.toggleMaterialYou(it) }
            )
        }
    }
}

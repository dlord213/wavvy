package com.mirimomekiku.wavvy.ui.composables.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.mirimomekiku.wavvy.instances.AppTheme
import com.mirimomekiku.wavvy.ui.compositions.LocalSettingsViewModel

@Composable
fun AppThemeSelectionDialog(
    onDismissRequest: () -> Unit,
) {
    val settingsViewModel = LocalSettingsViewModel.current
    val currentTheme by settingsViewModel.appTheme.collectAsState()

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppTheme.entries.map { theme ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            6.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (currentTheme.value == theme.value), onClick = {
                            settingsViewModel.updateAppTheme(theme)
                            onDismissRequest()
                        })
                        Text(
                            when (theme.name) {
                                "FOLLOW_SYSTEM" -> "System default"
                                "LIGHT" -> "Light"
                                "DARK" -> "Dark"
                                else -> ""
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            null
        },
        dismissButton = {
            null
        }
    )
}
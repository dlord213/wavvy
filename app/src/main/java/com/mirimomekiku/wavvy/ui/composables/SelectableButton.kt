package com.mirimomekiku.wavvy.ui.composables

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SelectableButton(text: String, onClick: () -> Unit, selected: Boolean) {
    Button(
        onClick, colors = if (selected) {
            ButtonDefaults.buttonColors()
        } else {
            ButtonDefaults.filledTonalButtonColors()
        }
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

package com.mirimomekiku.wavvy.ui.composables


import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SelectableIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    selected: Boolean
) {
    IconButton(
        onClick, colors = if (selected) {
            IconButtonDefaults.filledIconButtonColors()
        } else {
            IconButtonDefaults.iconButtonColors()
        }
    ) {
        Icon(
            imageVector,
            contentDescription
        )
    }
}

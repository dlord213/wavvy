package com.mirimomekiku.wavvy.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RowPermissions(title: String, body: String, index: String, onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            16.dp,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        Card(
            shape = RoundedCornerShape(999.dp)
        ) {
            Text(
                index,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    body,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            FilledTonalButton(
                onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Grant permission")
            }
        }
    }
}
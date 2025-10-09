package com.mirimomekiku.wavvy.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mirimomekiku.wavvy.R

@Composable
fun HeadingLogo() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            12.dp, Alignment.CenterHorizontally
        ),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Text(
            "Wavvy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black
        )
    }
}
package com.mirimomekiku.wavvy.ui.composables.player.expandedsheet.defaultmaterial3

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SyncedLyrics(
    syncedLines: List<Pair<Int, String>>,
    position: Long
) {
    val listState = rememberLazyListState()

    val currentIndex = remember(position) {
        syncedLines.indexOfLast { position >= it.first }
    }.coerceAtLeast(0)

    LaunchedEffect(currentIndex) {
        if (currentIndex in syncedLines.indices) {
            listState.animateScrollToItem(
                index = currentIndex,
                scrollOffset = -listState.layoutInfo.viewportEndOffset / 3
            )
        }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(syncedLines) { index, (_, text) ->
            val isCurrent = index == currentIndex

            val animatedFontSize by animateFloatAsState(
                targetValue = if (isCurrent) 28f else 20f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isCurrent) 1f else 0.3f,
                animationSpec = tween(500)
            )

            Text(
                text = text.trim(),
                fontSize = animatedFontSize.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = animatedAlpha),
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.animateContentSize(),
                textAlign = TextAlign.Start
            )
        }
    }
}

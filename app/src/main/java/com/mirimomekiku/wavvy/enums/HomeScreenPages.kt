package com.mirimomekiku.wavvy.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.vector.ImageVector

enum class HomeScreenPages(val title: String, val icon: ImageVector? = null) {
    Favorites("Favorites", Icons.Filled.Favorite),
    Songs("Songs"),
    Playlists("Playlists"),
    Artists("Artists"),
    Albums("Albums");

    val selectedIcon: ImageVector?
        get() = if (this == Favorites) Icons.Filled.Favorite else null

    val unselectedIcon: ImageVector?
        get() = if (this == Favorites) Icons.Filled.FavoriteBorder else null
}

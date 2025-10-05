package com.mirimomekiku.wavvy.ui.compositions

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController =
    compositionLocalOf<NavController> { error("No route controller") }
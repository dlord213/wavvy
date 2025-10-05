package com.mirimomekiku.wavvy.helpers

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.widget.Toast

fun openEqualizer(context: Context, audioSessionId: Int = 0) {
    val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
        putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
        putExtra(AudioEffect.EXTRA_AUDIO_SESSION, audioSessionId)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No equalizer app found on this device", Toast.LENGTH_SHORT).show()
    }
}
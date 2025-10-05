package com.mirimomekiku.wavvy.helpers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette

fun adjustColorBrightness(color: Int, factor: Float = 0.7f): Int {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color, hsl)

    hsl[2] = (hsl[2] * factor).coerceIn(0f, 1f)

    return ColorUtils.HSLToColor(hsl)
}

fun getDominantColorFromUri(context: Context, uri: Uri?): Int? {
    if (uri == null) return null

    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val palette = Palette.from(bitmap).generate()
            palette.getDominantColor(0x000000) // fallback to black
        }
    } catch (e: Exception) {
        null
    }
}

fun getDominantColorWithOpacity(context: Context, uri: Uri?, alpha: Float = 0.7f): Int? {
    if (uri == null) return null
    if (alpha !in 0f..1f) return null

    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val palette = Palette.from(bitmap).generate()
            val dominant = palette.getDominantColor(0x000000)
            val a = (alpha * 255).toInt()
            (dominant and 0x00FFFFFF) or (a shl 24)
        }
    } catch (e: Exception) {
        null
    }
}

fun getGradientColorsFromUri(context: Context, uri: Uri?): Pair<Int, Int>? {
    if (uri == null) return null

    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val palette = Palette.from(bitmap).generate()
            val dominant = palette.getDominantColor(0x000000)
            val darkVibrant = palette.getDarkVibrantColor(dominant)
            dominant to darkVibrant
        }
    } catch (e: Exception) {
        null
    }
}

// for MediaItems & Base Color for BG bottom sheet
fun getAlbumArtURIDominantColorAdjusted(
    context: Context,
    uri: Uri?,
    alpha: Float = 0.7f,
    brightnessFactor: Float = 1f
): Int {
    val defaultColor = 0xFF969696.toInt() // full alpha fallback

    // Clamp alpha
    val clampedAlpha = alpha.coerceIn(0f, 1f)
    val alphaInt = (clampedAlpha * 255).toInt()

    if (uri == null) return defaultColor

    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val palette = Palette.from(bitmap).generate()
            var dominant = palette.getDominantColor(defaultColor)

            // Ensure fully opaque before brightness adjustment
            dominant = dominant or (0xFF shl 24)

            // Adjust brightness
            dominant = adjustColorBrightness(dominant, brightnessFactor)

            // Apply requested alpha
            (dominant and 0x00FFFFFF) or (alphaInt shl 24)
        } ?: defaultColor
    } catch (e: Exception) {
        defaultColor
    }
}


// for Components
fun getDominantColorAdjusted(context: Context, uri: Uri?): Color {
    val fallbackColor = Color(0xFF484848) // full alpha

    if (uri == null) return fallbackColor

    return try {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val bitmap = BitmapFactory.decodeStream(stream)
            val palette = Palette.from(bitmap).generate()

            // Force default to opaque fallback if palette fails
            var dominant = palette.getDominantColor(fallbackColor.toArgb())

            // Ensure alpha is full
            dominant = dominant or (0xFF shl 24)

            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(dominant, hsl)
            val lightness = hsl[2]

            val adjustedColor = when {
                lightness < 0.4f -> Color(ColorUtils.HSLToColor(floatArrayOf(hsl[0], hsl[1], 0.5f)))
                lightness > 0.75f -> Color(
                    ColorUtils.HSLToColor(
                        floatArrayOf(
                            hsl[0],
                            hsl[1],
                            0.65f
                        )
                    )
                )

                else -> Color(dominant)
            }

            adjustedColor
        } ?: fallbackColor
    } catch (e: Exception) {
        fallbackColor
    }
}


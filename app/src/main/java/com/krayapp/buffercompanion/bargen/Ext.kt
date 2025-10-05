package com.krayapp.buffercompanion.bargen

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

fun Context.justVibrateABit() {
    @Suppress("DEPRECATION") val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(1, VibrationEffect.DEFAULT_AMPLITUDE))
}


fun Offset.toIntOffset() = IntOffset(x = x.toInt(), y = y.toInt())

fun Offset.toDpOffset() = DpOffset(x = x.toInt().dp, y = y.toInt().dp)
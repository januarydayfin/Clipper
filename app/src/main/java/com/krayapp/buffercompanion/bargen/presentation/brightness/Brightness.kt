package com.krayapp.buffercompanion.bargen.presentation.brightness

import com.krayapp.buffercompanion.bargen.presentation.MainActivity

fun MainActivity.peakBright() {
    val layoutParams = window.attributes
    window.attributes = layoutParams.apply { screenBrightness = 1f }
}

fun MainActivity.restoreBright() {
    val layoutParams = window.attributes
    window.attributes = layoutParams.apply { screenBrightness = -1f }
}

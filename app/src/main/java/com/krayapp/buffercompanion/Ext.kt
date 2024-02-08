package com.krayapp.buffercompanion

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

fun Context.dp(value: Int): Int {
	val displayMetrics = resources.displayMetrics
	return (value * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}
package com.krayapp.buffercompanion

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.roundToInt

fun Context.dp(value: Int): Int {
	val displayMetrics = resources.displayMetrics
	return (value * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun Context.hideKeyboard(v: View) {
	(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
		v.windowToken,
		0
	)
}

package com.krayman.toolbox

import android.content.Context
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import androidx.annotation.Px
import androidx.core.view.setMargins

fun LayoutParams.addMargins(margins: Int): LayoutParams {
    return LinearLayout.LayoutParams(this).apply {
        setMargins(margins)
    }
}

fun LayoutParams.addMargins(
    @Px left: Int,
    @Px top: Int,
    @Px right: Int,
    @Px bottom: Int
): LayoutParams {
    return LinearLayout.LayoutParams(this).apply {
        setMargins(left, top, right, bottom)
    }
}

fun rectangleParams(size: Int) = LinearLayout.LayoutParams(size, size)
fun Context?.displayWidth() = this?.resources?.displayMetrics?.widthPixels ?: 0
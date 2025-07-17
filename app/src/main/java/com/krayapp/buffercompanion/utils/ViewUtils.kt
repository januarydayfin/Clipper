package com.krayapp.buffercompanion.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import kotlin.math.roundToInt

@SuppressLint("ClickableViewAccessibility")
fun View.onTouchCoordinates(point: (PointF) -> Unit) {
    setOnTouchListener { v, event ->
        point(PointF(event.x, event.y))
        false
    }
}


fun View.getPercentagePxFromDisplayWidth(percentage: Int): Int {
    val displayWidth = context.resources.displayMetrics.widthPixels
    return (displayWidth / 100) * percentage
}

fun View.getPercentagePxFromDisplayHeight(percentage: Int) : Int {
    val displayHeight = context.resources.displayMetrics.heightPixels
    return (displayHeight / 100) * percentage
}

fun View.dp(value: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    return (value * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        windowToken,
        0
    )
}

fun DatePicker.removeYearField() {
    val linear = (children.toList()[0] as LinearLayout).children.toList()[0] as LinearLayout
    linear.children.toList()[2].isVisible = false
}

val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(this.context)


fun View.addCircleRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

fun View.removeCircleRipple() {
    setBackgroundResource(0)
}

@SuppressLint("ClickableViewAccessibility")
fun View.onTouchCoordinates(xy: (Float, Float) -> Unit) {
    setOnTouchListener { v, event ->
        xy(event.x, event.y)
        false
    }
}
package com.krayapp.buffercompanion

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.krayapp.buffercompanion.ui.MainActivity
import kotlin.math.roundToInt

fun Context.dp(value: Int): Int {
	val displayMetrics = resources.displayMetrics
	return (value * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun Context.justVibrateABit() {
	val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		vibrator.vibrate(VibrationEffect.createOneShot(1, VibrationEffect.DEFAULT_AMPLITUDE))
	} else {
		vibrator.vibrate(1)
	}
}

fun Fragment.activity(): MainActivity {
	return activity as MainActivity
}

fun Context.hideKeyboard(v: View) {
	(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
		v.windowToken,
		0
	)
}

fun View.setVisible() {
	visibility = View.VISIBLE

}

fun View.setInvisible() {
	visibility = View.INVISIBLE
}

fun View.setGone() {
	visibility = View.GONE
}

fun CheckBox.switchState() {
	isChecked = !isChecked
}

fun EditText.addTextWatcher(onChanged: (String) -> Unit) {
	addTextChangedListener(object : TextWatcher {
		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

		}

		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

		}

		override fun afterTextChanged(s: Editable?) {
			onChanged(s.toString().trim())
		}

	})
}

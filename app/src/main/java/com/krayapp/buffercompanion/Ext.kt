package com.krayapp.buffercompanion

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.krayapp.buffercompanion.ui.MainActivity
import kotlin.math.roundToInt

fun Context.dp(value: Int): Int {
	val displayMetrics = resources.displayMetrics
	return (value * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun View.dp(value: Int): Int {
	val displayMetrics = resources.displayMetrics
	return (value * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun Context.justVibrateABit() {
	@Suppress("DEPRECATION") val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
	vibrator.vibrate(VibrationEffect.createOneShot(1, VibrationEffect.DEFAULT_AMPLITUDE))
}

fun Fragment.activity(): MainActivity {
	return activity as MainActivity
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
fun EditText.onImeDone(onDone: (String) -> Unit) {
	setOnEditorActionListener(object : TextView.OnEditorActionListener {
		override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				onDone(v.text.toString())
				return true
			}
			return false
		}
	})
}

fun View.hideKeyboard() {
	(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
		windowToken,
		0
	)
}

fun getSimpleFragmentAdapter(
	fragList: List<Fragment>,
	parent: Fragment
): FragmentStateAdapter {
	return object : FragmentStateAdapter(parent) {

		override fun getItemCount(): Int {
			return fragList.size
		}

		override fun createFragment(position: Int): Fragment {
			return fragList[position]
		}
	}
}

fun BottomSheetDialogFragment.expand() {
	val sheet = dialog as BottomSheetDialog
	sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
}

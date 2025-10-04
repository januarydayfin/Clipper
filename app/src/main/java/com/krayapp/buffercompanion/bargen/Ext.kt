package com.krayapp.buffercompanion.bargen

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.krayapp.buffercompanion.bargen.ui.MainActivity
import kotlin.math.roundToInt

fun Context.justVibrateABit() {
    @Suppress("DEPRECATION") val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(1, VibrationEffect.DEFAULT_AMPLITUDE))
}


fun EditText.addTextWatcher(onChanged: (String) -> Unit): EditText {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            onChanged(s.toString().trim())
        }

    })
    return this
}

fun EditText.onImeAction(onDone: (View) -> Unit) {
    setOnEditorActionListener { v, _, _ ->
        onDone(v)
        true
    }
}

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        windowToken,
        0
    )
}

fun BottomSheetDialogFragment.expand() {
    val sheet = dialog as BottomSheetDialog
    sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
}

fun Offset.toIntOffset() = IntOffset(x = x.toInt(), y = y.toInt())
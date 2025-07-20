package com.krayapp.buffercompanion.bargen.ui.dialogs

import android.content.Context
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.utils.setCustomBackground
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


fun Context.showColorPickerDialog(
    initialColor: Int? = null,
    onColorPicked: (colorInt: Int) -> Unit
) {
    val dialog =
        ColorPickerDialog.Builder(this)
            .setPositiveButton(R.string.save, object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    onColorPicked(envelope?.color ?: 0)
                }
            })
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .attachAlphaSlideBar(true) // the default value is true.
            .attachBrightnessSlideBar(true)  // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
            .apply {
                if (initialColor != null)
                    colorPickerView.setInitialColor(initialColor)
            }.show()
    dialog.setCustomBackground(R.drawable.dialog_background)
}

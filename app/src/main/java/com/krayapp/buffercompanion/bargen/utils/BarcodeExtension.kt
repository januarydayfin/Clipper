package com.krayapp.buffercompanion.bargen.utils

import android.content.Context
import android.content.res.ColorStateList
import android.nfc.Tag
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel


val decodedSize = ClipperApp.displayWidth to ClipperApp.displayWidth / 2
fun Context.filterChip(tagUiModel: TagUiModel, canChecked: Boolean = true): Chip {
    val drawable = ChipDrawable.createFromAttributes(this, null, 0, filterChipStyle)


    return Chip(this).apply {
        val backgroundColor = tagUiModel.backgroundColor.toColorStateList()
        val fontColor = tagUiModel.fontColor.toColorStateList()

        setChipDrawable(drawable)
        text = tagUiModel.name
        chipBackgroundColor = backgroundColor

        if (fontColor != null)
            setTextColor(fontColor)

        ensureAccessibleTouchTarget(20)
        checkedIconTint = fontColor
        isCheckable = canChecked
        chipStrokeColor = backgroundColor
        isChecked = tagUiModel.checked
        tag = tagUiModel
    }
}

val Chip.uiModelTag
    get() = tag as TagUiModel

private fun Int?.toColorStateList() =
    if (this == null)
        null
    else {
        ColorStateList.valueOf(this)
    }

val filterChipStyle: Int
    get() = com.google.android.material.R.style.Widget_Material3_Chip_Filter


fun Context.showDeleteConfirmationDialog(onDelete: () -> Unit) {
    MaterialAlertDialogBuilder(this)
        .setTitle(R.string.delete)
        .setMessage(R.string.are_you_sure)
        .setPositiveButton(R.string.delete) { dialog, _ ->
            onDelete()
            dialog.dismiss()
        }
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}




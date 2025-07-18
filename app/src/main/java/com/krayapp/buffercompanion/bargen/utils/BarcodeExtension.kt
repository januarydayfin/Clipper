package com.krayapp.buffercompanion.bargen.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.room.withTransaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.color.MaterialColors
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.ClipperApp.Companion.displayWidth
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.bargenCore.generator.BarcodeGenerator
import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

suspend fun BarcodeEntity.toBarcodeUiModel(): BarcodeUiModel =
    withContext(Dispatchers.IO) {
        val db = provideDatabase<BargenDB>()
        val tagsDao = db.tagsDao()
        val bitmapGen: BarGenerator = BarcodeGenerator
        val tagsModel = mutableListOf<TagUiModel>()

        db.withTransaction {
            this@toBarcodeUiModel.tags.forEach {
                tagsModel.add(tagsDao.getTagById(it).toTagUiModel())
            }
        }

        BarcodeUiModel(
            id = this@toBarcodeUiModel.id,
            name = this@toBarcodeUiModel.name,
            description = this@toBarcodeUiModel.description,
            image = bitmapGen.generate(
                this@toBarcodeUiModel.content,
                type = BarcodeFormat.valueOf(this@toBarcodeUiModel.type),
                width = displayWidth,
                height = displayWidth / 2
            ),
            tags = tagsModel
        )
    }

fun BottomSheetDialogFragment.colorNavBar(
    color: Int = MaterialColors.getColor(
        requireContext(),
        R.attr.bottomSheetBottomColor,
        Color.BLACK
    )
) {
    dialog?.window?.navigationBarColor = color
}

fun TagEntity.toTagUiModel() =
    TagUiModel(
        id = this.id,
        backgroundColor = this.backgroundColor,
        fontColor = fontColor,
        name = this.name
    )

fun TagUiModel.toEntity() =
    TagEntity(
        id = this.id,
        name = this.name,
        backgroundColor = this.backgroundColor,
        fontColor = this.fontColor
    )

fun Long.toReadableTime() =
    SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(this)


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

        checkedIconTint = fontColor
        isCheckable = canChecked
        chipStrokeColor = backgroundColor
        isChecked = tagUiModel.checked
    }
}

private fun Int?.toColorStateList() =
    if (this == null)
        null
    else {
        ColorStateList.valueOf(this)
    }

val filterChipStyle: Int
    get() = com.google.android.material.R.style.Widget_Material3_Chip_Filter
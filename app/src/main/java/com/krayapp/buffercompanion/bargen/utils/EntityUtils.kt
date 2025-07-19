package com.krayapp.buffercompanion.bargen.utils

import android.graphics.Color
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

suspend fun BarcodeEntity.toBarcodeUiModel(cachedTags: List<TagEntity>? = null): BarcodeUiModel =
    withContext(Dispatchers.IO) {
        val tagsModel = mutableListOf<TagUiModel>()
        val allTags = cachedTags ?: provideDatabase<BargenDB>().tagsDao().getTags()

        allTags
            .filter { this@toBarcodeUiModel.tags.contains(it.id) }
            .forEach { tagsModel.add(it.toTagUiModel()) }

        BarcodeUiModel(
            id = this@toBarcodeUiModel.id,
            name = this@toBarcodeUiModel.name,
            description = this@toBarcodeUiModel.description,
            barcodeType = this@toBarcodeUiModel.type,
            tags = tagsModel,
            content = this@toBarcodeUiModel.content
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
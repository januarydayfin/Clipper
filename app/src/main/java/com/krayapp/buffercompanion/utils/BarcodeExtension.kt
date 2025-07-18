package com.krayapp.buffercompanion.utils

import android.graphics.Color
import androidx.room.withTransaction
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.ClipperApp
import com.krayapp.buffercompanion.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargenCore.generator.BarcodeGenerator
import com.krayapp.buffercompanion.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.ui.models.TagUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
                width = ClipperApp.displayWidth,
                height = ClipperApp.displayWidth / 2
            ),
            tags = tagsModel
        )
    }

fun TagEntity.toTagUiModel() =
    TagUiModel(
        id = this.id,
        backgroundColor = Color.valueOf(this.backgroundColor),
        fontColor = Color.valueOf(this.textColor),
        name = this.name
    )
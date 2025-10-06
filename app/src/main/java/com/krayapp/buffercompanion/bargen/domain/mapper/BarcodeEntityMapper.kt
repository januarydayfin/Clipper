package com.krayapp.buffercompanion.bargen.domain.mapper

import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.presentation.uiModels.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.uiModels.TagUiModel
import com.krayapp.buffercompanion.bargen.domain.provideDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

suspend fun BarcodeUiModel.toBarcodeEntity(): BarcodeEntity {
    return withContext(Dispatchers.IO) {
        val model = this@toBarcodeEntity
        val existBarcode = provideDatabase<BargenDB>().barcodeDao().getBarcodeById(model.id)

        BarcodeEntity(
            id = model.id,
            usageCount = existBarcode?.usageCount ?: 0,
            content = model.content,
            name = model.name,
            description = model.description,
            tags = model.tags.map { it.id },
            type = model.barcodeType
        )
    }
}
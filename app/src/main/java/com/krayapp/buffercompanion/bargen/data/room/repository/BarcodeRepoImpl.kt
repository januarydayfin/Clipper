package com.krayapp.buffercompanion.bargen.data.room.repository

import com.krayapp.buffercompanion.bargen.data.room.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.provideDatabase
import com.krayapp.buffercompanion.bargen.domain.type.SortType
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel.Companion.NOT_PINNED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BarcodeRepoImpl : BarcodeRepo {
    private val barcodes = provideDatabase<BargenDB>().barcodeDao()
    override suspend fun recordsCount() =  barcodes.count()
    override suspend fun pinnedCount() = barcodes.pinnedCount()

    override suspend fun upsertBarcode(barcodeEntity: BarcodeEntity) {
        barcodes.upsertBarcode(barcodeEntity)
    }

    override suspend fun upsertBarcode(list: List<BarcodeEntity>) {
        barcodes.upsertBarcodes(list)
    }


    override suspend fun removeBarcodesByIds(ids: List<String>) {
        barcodes.removeBarcodesById(ids)
    }

    override suspend fun incrementUsageCount(id: String) = withContext(Dispatchers.IO) {
        val entity = barcodes.getBarcodeById(id)

        if (entity != null)
            upsertBarcode(entity.copy(usageCount = entity.usageCount + 1))
    }


    override suspend fun removeTagFromBarcodes(tagId: String) {
        val modifiedList = mutableListOf<BarcodeEntity>()

        barcodes.getAll().forEach {
            val clearedTags = it.tags.filter { tag -> tag != tagId }
            val newEntity = it.copy(tags = clearedTags)
            modifiedList.add(newEntity)
        }

        barcodes.upsertBarcodes(modifiedList)
    }


    override fun getAllBarcodesPaging(sort: SortType) =
        when (sort) {
            SortType.NAME -> barcodes.getBarcodesByNamePaging()
            SortType.DATE_ASC -> barcodes.getBarcodesByDateAscPaging()
            SortType.DATE_DESC -> barcodes.getBarcodesByDateDescPaging()
            SortType.USAGE -> barcodes.getBarcodesByUsagePaging()
        }

    override suspend fun getPinnedBarcodes(): List<BarcodeEntity> {
        return barcodes.getAllPinnedBarcodes()
    }

    override fun getFilteredBarcodesByNamePaging(filter: String) =
        barcodes.getFilteredBarcodesPaging(filter)

    override suspend fun pinBarcode(id: String, position: Int) {
        barcodes.getBarcodeById(id)?.copy(pinnedPosition = position)
            ?.run { barcodes.upsertBarcode(this) }
    }

    override suspend fun unpinBarcode(id: String) {
        barcodes.getBarcodeById(id)?.copy(pinnedPosition = NOT_PINNED)
            ?.run { barcodes.upsertBarcode(this) }
    }
}

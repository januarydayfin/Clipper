package com.krayapp.buffercompanion.bargen.domain.repository

import com.krayapp.buffercompanion.bargen.data.room.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.provideDatabase
import com.krayapp.buffercompanion.bargen.domain.type.SortType
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BarcodeRepoImpl : BarcodeRepo {
    private val barcodes = provideDatabase<BargenDB>().barcodeDao()
    override suspend fun recordsCount() = withContext(Dispatchers.IO) { barcodes.count() }

    override suspend fun upsertBarcode(barcodeEntity: BarcodeEntity) {
        withIO { barcodes.upsertBarcode(barcodeEntity) }
    }


    override suspend fun removeBarcodesByIds(ids: List<String>) {
        withIO { barcodes.removeBarcodesById(ids) }
    }

    override suspend fun incrementUsageCount(id: String) = withContext(Dispatchers.IO) {
        val entity = barcodes.getBarcodeById(id)

        if (entity != null)
            upsertBarcode(entity.copy(usageCount = entity.usageCount + 1))
    }


    override suspend fun removeTagFromBarcodes(tagId: String) {
        withIO {
            val modifiedList = mutableListOf<BarcodeEntity>()

            barcodes.getAll().forEach {
                val clearedTags = it.tags.filter { tag -> tag != tagId }
                val newEntity = it.copy(tags = clearedTags)
                modifiedList.add(newEntity)
            }

            barcodes.upsertBarcodes(modifiedList)
        }
    }


    override fun getAllBarcodesPaging(sort: SortType) =
        when (sort) {
            SortType.NAME -> barcodes.getBarcodesByNamePaging()
            SortType.DATE_ASC -> barcodes.getBarcodesByDateAscPaging()
            SortType.DATE_DESC -> barcodes.getBarcodesByDateDescPaging()
            SortType.USAGE -> barcodes.getBarcodesByUsagePaging()
        }

    override fun getFilteredBarcodesByNamePaging(filter: String) =
        barcodes.getFilteredBarcodesPaging(filter)
}

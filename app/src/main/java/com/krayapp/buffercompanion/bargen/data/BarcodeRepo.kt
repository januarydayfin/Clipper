package com.krayapp.buffercompanion.bargen.data

import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.utils.provideDatabase
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BarcodeRepo {
    private val barcodes = provideDatabase<BargenDB>().barcodeDao()

    suspend fun upsertBarcode(barcodeEntity: BarcodeEntity) {
        withIO { barcodes.upsertBarcode(barcodeEntity) }
    }


    suspend fun removeBarcodeById(id: String) {
        withIO { barcodes.removeBarcodeById(id) }
    }

    suspend fun removeBarcodesByIds(ids: List<String>) {
        withIO { barcodes.removeBarcodesById(ids) }
    }


    suspend fun searchWithFilter(filter: String) = withContext(Dispatchers.IO) {
        barcodes.getFilteredBarcodes(filter)
    }

    suspend fun getBarcodeById(id: String) = withContext(Dispatchers.IO) {
        barcodes.getBarcodeById(id)
    }

    suspend fun incrementUsageCount(id: String) = withContext(Dispatchers.IO) {
        val entity = barcodes.getBarcodeById(id)

        if (entity != null)
            upsertBarcode(entity.copy(usageCount = entity.usageCount + 1))
    }

    suspend fun filterBarcodesWithTags(
        tags: List<String>,
        sort: SortType,
        sourceList: List<BarcodeEntity>? = null
    ): List<BarcodeEntity> =
        withContext(Dispatchers.IO) {
            (sourceList ?: getAllBarcodes(sort)).filter { it.tags.containsAll(tags) }
        }

    suspend fun removeTagFromBarcodes(tagId: String) {
        withIO {
            val modifiedList = mutableListOf<BarcodeEntity>()

            barcodes.getBarcodesByUsage().forEach {
                val clearedTags = it.tags.filter { tag -> tag != tagId }
                val newEntity = it.copy(tags = clearedTags)
                modifiedList.add(newEntity)
            }

            barcodes.upsertBarcodes(modifiedList)
        }
    }


     suspend fun getAllBarcodes(sort: SortType = SortType.NAME) = withContext(Dispatchers.IO) {
        when (sort) {
            SortType.NAME -> barcodes.getBarcodesByName()
            SortType.DATE_ASC -> barcodes.getBarcodesByDateAsc()
            SortType.DATE_DESC -> barcodes.getBarcodesByDateDesc()
            SortType.USAGE -> barcodes.getBarcodesByUsage()
        }
    }

    fun getAllBarcodesPaging(sort: SortType) =
        when (sort) {
            SortType.NAME -> barcodes.getBarcodesByNamePaging()
            SortType.DATE_ASC -> barcodes.getBarcodesByDateAscPaging()
            SortType.DATE_DESC -> barcodes.getBarcodesByDateDescPaging()
            SortType.USAGE -> barcodes.getBarcodesByUsagePaging()
        }

    fun getFilteredBarcodesByNamePaging(filter: String) = barcodes.getFilteredBarcodesPaging(filter)
}

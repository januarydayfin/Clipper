package com.krayapp.buffercompanion.bargen.data

import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.utils.provideDatabase
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BargenRepo {
    private val barcodes = provideDatabase<BargenDB>().barcodeDao()
    private val tags = provideDatabase<BargenDB>().tagsDao()

    suspend fun upsertBarcode(barcodeEntity: BarcodeEntity) {
        withIO { barcodes.upsertBarcode(barcodeEntity) }
    }

    suspend fun upsertTag(tag: TagEntity) {
        withIO { tags.upsertTag(tag) }
    }

    suspend fun removeBarcodeById(id: String) {
        withIO { barcodes.removeBarcodeById(id) }
    }

    suspend fun removeTagByName(name: String) {
        withIO { tags.removeByName(name) }
    }

    suspend fun searchBarcodesByName(name: String) = withContext(Dispatchers.IO) {
        barcodes.getBarcodesByName().filter { it.name.contains(name, true) }
    }

    suspend fun filterTagsByName(name: String) = withContext(Dispatchers.IO) {
        tags.getTags().filter { it.name.contains(name, true) }
    }

    suspend fun getTagsWithIds(list: List<String>) = withContext(Dispatchers.IO) {
        tags.getTags().filter { it.id in list }
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


    suspend fun getAllBarcodes(sort: SortType) = withContext(Dispatchers.IO) {
        when (sort) {
            SortType.NAME -> barcodes.getBarcodesByName()
            SortType.DATE_ASC -> barcodes.getBarcodesByDateAsc()
            SortType.DATE_DESC -> barcodes.getBarcodesByDateDesc()
            SortType.USAGE -> barcodes.getBarcodesByUsage()
        }
    }

}
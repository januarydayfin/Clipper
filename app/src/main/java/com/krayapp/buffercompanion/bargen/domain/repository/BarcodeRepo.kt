package com.krayapp.buffercompanion.bargen.domain.repository

import androidx.paging.PagingSource
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.type.SortType

interface BarcodeRepo {
    suspend fun recordsCount(): Int
    suspend fun upsertBarcode(barcodeEntity: BarcodeEntity)
    suspend fun removeBarcodesByIds(ids: List<String>)
    suspend fun incrementUsageCount(id: String)
    suspend fun removeTagFromBarcodes(tagId: String)
    fun getAllBarcodesPaging(sort: SortType) : PagingSource<Int, BarcodeEntity>
    fun getFilteredBarcodesByNamePaging(filter: String) : PagingSource<Int, BarcodeEntity>
}
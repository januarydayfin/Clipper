package com.krayapp.buffercompanion.bargen.domain.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.provideDatabase

class BarcodeFilterTagsPagingSource(private val tagIds: List<String>) :
    PagingSource<Int, BarcodeEntity>() {
    private val barcodeDao = provideDatabase<BargenDB>().barcodeDao()
    override fun getRefreshKey(state: PagingState<Int, BarcodeEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BarcodeEntity> {
        val allCount = barcodeDao.count()
        var offset = params.key ?: 0
        val resultList = mutableListOf<BarcodeEntity>()
        var skippedPageItemsCount = 0

        while (resultList.size < PAGE_SIZE) {
            barcodeDao.loadPage(pageSize = PAGE_SIZE, offset).filter {
                it.tags.containsAll(tagIds)
            }.onEach {
                if (resultList.size < PAGE_SIZE)
                    resultList.add(it)
                else
                    skippedPageItemsCount++
            }

            offset += (PAGE_SIZE - skippedPageItemsCount)

            if (offset >= allCount)
                break
        }

        return LoadResult.Page(
            data = resultList,
            nextKey = if (offset >= allCount) null else offset,
            prevKey = null
        )
    }

    companion object {
        private const val PAGE_SIZE = 25
    }
}
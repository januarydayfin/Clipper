package com.krayapp.buffercompanion.bargen.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.utils.provideDatabase

class BarcodeFilterTagsPagingSource(private val tagIds: List<String>) :
    PagingSource<Int, BarcodeEntity>() {
    private val barcodeDao = provideDatabase<BargenDB>().barcodeDao()

    override fun getRefreshKey(state: PagingState<Int, BarcodeEntity>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BarcodeEntity> {
        val pagingNextKey = params.key ?: 0
        val page = barcodeDao.loadPage(pageSize = PAGE_SIZE, offset = pagingNextKey)
        val filtered = page.filter { it.tags.containsAll(tagIds) }

        val nextKey = if (page.isEmpty()) null else pagingNextKey + PAGE_SIZE
        return LoadResult.Page(data = filtered, nextKey = (nextKey), prevKey = nextKey)
    }

    companion object {
        private const val PAGE_SIZE = 25
    }
}
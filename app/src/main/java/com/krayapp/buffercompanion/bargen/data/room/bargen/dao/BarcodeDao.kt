package com.krayapp.buffercompanion.bargen.data.room.bargen.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity

@Dao
interface BarcodeDao {
    @Upsert
    suspend fun upsertBarcode(entity: BarcodeEntity)

    @Upsert
    suspend fun upsertBarcodes(list: List<BarcodeEntity>)

    @Query("DELETE FROM barcodes WHERE :id == id")
    suspend fun removeBarcodeById(id: String)

    @Query("SELECT * FROM barcodes WHERE pinnedPosition != -1 ORDER BY pinnedPosition ASC")
    suspend fun getAllPinnedBarcodes(): List<BarcodeEntity>

    @Query("SELECT * FROM barcodes WHERE name LIKE '%' || :filter || '%' OR content LIKE '%' || :filter || '%'")
    suspend fun getFilteredBarcodes(filter: String): List<BarcodeEntity>

    @Query("select * from barcodes limit :pageSize offset :offset")
    suspend fun loadPage(pageSize: Int, offset: Int): List<BarcodeEntity>

    @Query("DELETE FROM barcodes WHERE id IN (:ids)")
    suspend fun removeBarcodesById(ids: List<String>)

    @Query("select * from barcodes where :id == id")
    suspend fun getBarcodeById(id: String): BarcodeEntity?

    @Query("SELECT * FROM barcodes WHERE pinnedPosition != -1 ORDER BY pinnedPosition ASC")
    fun getPinnedBarcodesPaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM barcodes WHERE pinnedPosition == -1 ORDER BY modificationTime ASC")
    fun getBarcodesByDateAscPaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM barcodes WHERE pinnedPosition == -1 ORDER BY modificationTime DESC")
    fun getBarcodesByDateDescPaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM barcodes WHERE pinnedPosition == -1 ORDER BY usageCount DESC")
    fun getBarcodesByUsagePaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM barcodes WHERE pinnedPosition == -1 ORDER BY name ASC")
    fun getBarcodesByNamePaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM barcodes WHERE name LIKE '%' || :filter || '%' OR content LIKE '%' || :filter || '%'")
    fun getFilteredBarcodesPaging(filter: String): PagingSource<Int, BarcodeEntity>

    @Query("select * from barcodes")
    suspend fun getAll(): List<BarcodeEntity>

    @Query("select count(id) from barcodes")
    suspend fun count(): Int

    @Query("select count(id) from barcodes where pinnedPosition != -1")
    suspend fun pinnedCount(): Int

    @Query("delete from barcodes")
    suspend fun clean()
}
package com.krayapp.buffercompanion.bargen.data.room.bargen.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity

@Dao
interface BarcodeDao {
    @Upsert
    suspend fun upsertBarcode(entity: BarcodeEntity)

    @Upsert
    suspend fun upsertBarcodes(list: List<BarcodeEntity>)

    @Query("DELETE FROM barcodes WHERE :id == id")
    suspend fun removeBarcodeById(id: String)

    @Query("SELECT * FROM barcodes WHERE name LIKE '%' || :filter || '%' OR content LIKE '%' || :filter || '%'")
    suspend fun getFilteredBarcodes(filter: String): List<BarcodeEntity>

    @Query("DELETE FROM barcodes WHERE id IN (:ids)")
    suspend fun removeBarcodesById(ids: List<String>)

    @Query("select * from barcodes where :id == id")
    suspend fun getBarcodeById(id: String): BarcodeEntity?

    @Query("SELECT * FROM barcodes ORDER BY modificationTime ASC")
    suspend fun getBarcodesByDateAsc(): List<BarcodeEntity>

    @Query("SELECT * FROM barcodes ORDER BY modificationTime DESC")
    suspend fun getBarcodesByDateDesc(): List<BarcodeEntity>

    @Query("SELECT * FROM barcodes ORDER BY usageCount DESC")
    suspend fun getBarcodesByUsage(): List<BarcodeEntity>

    @Query("SELECT * FROM barcodes ORDER BY name DESC")
    suspend fun getBarcodesByName(): List<BarcodeEntity>

    @Query("SELECT * FROM barcodes ORDER BY modificationTime ASC")
    fun getBarcodesByDateAscPaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM barcodes ORDER BY modificationTime DESC")
    fun getBarcodesByDateDescPaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM barcodes ORDER BY usageCount DESC")
    fun getBarcodesByUsagePaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM barcodes ORDER BY name DESC")
    fun getBarcodesByNamePaging(): PagingSource<Int, BarcodeEntity>

    @Query("SELECT * FROM barcodes WHERE name LIKE '%' || :filter || '%' OR content LIKE '%' || :filter || '%'")
    fun getFilteredBarcodesPaging(filter: String): PagingSource<Int, BarcodeEntity>

}
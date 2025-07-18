package com.krayapp.buffercompanion.bargen.data.room.bargen

import androidx.room.Database
import androidx.room.RoomDatabase
import com.krayapp.buffercompanion.bargen.data.room.bargen.dao.BarcodeDao
import com.krayapp.buffercompanion.bargen.data.room.bargen.dao.TagDao
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity

@Database(
    entities = [BarcodeEntity::class, TagEntity::class],
    version = 1
)
abstract class BargenDB : RoomDatabase() {
    abstract fun barcodeDao(): BarcodeDao
    abstract fun tagsDao(): TagDao

    companion object {
        const val DB_NAME = "bargen.db"
    }
}
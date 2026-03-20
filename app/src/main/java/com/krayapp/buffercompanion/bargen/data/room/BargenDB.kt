package com.krayapp.buffercompanion.bargen.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.krayapp.buffercompanion.bargen.data.room.bargen.dao.BarcodeDao
import com.krayapp.buffercompanion.bargen.data.room.dao.TagDao
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.entity.TagEntity

@Database(
    entities = [BarcodeEntity::class, TagEntity::class],
    version = 2,
    exportSchema = false
)
abstract class BargenDB : RoomDatabase() {
    abstract fun barcodeDao(): BarcodeDao
    abstract fun tagsDao(): TagDao

    companion object {
        const val DB_NAME = "bargen.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE barcodes ADD COLUMN pinnedPosition INTEGER NOT NULL DEFAULT -1")
            }
        }
    }
}

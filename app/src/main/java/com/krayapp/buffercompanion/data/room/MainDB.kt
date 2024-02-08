package com.krayapp.buffercompanion.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [StringEntity::class],
    version = 2
)
abstract class MainDB : RoomDatabase() {
    abstract fun getDao(): RoomDAO
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE buffered ADD COLUMN position INTEGER")
    }
}
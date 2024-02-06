package com.krayapp.buffercompanion.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StringEntity::class], version = 1)
abstract class MainDB : RoomDatabase() {
    abstract fun getDao(): RoomDAO
}
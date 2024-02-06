package com.krayapp.buffercompanion.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoomDAO {
    @Query("SELECT * FROM buffered")
    fun getAll(): List<StringEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(string: StringEntity)

    @Delete
    fun delete(string: StringEntity)
}
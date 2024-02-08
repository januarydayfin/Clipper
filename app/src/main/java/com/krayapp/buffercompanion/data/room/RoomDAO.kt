package com.krayapp.buffercompanion.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoomDAO {
    @Query("SELECT * FROM buffered ORDER BY position ASC")
    fun getAll(): List<StringEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(string: StringEntity)

    @Query("SELECT COUNT(*) FROM buffered")
    fun getCount(): Int

    @Delete
    fun delete(string: StringEntity)
}
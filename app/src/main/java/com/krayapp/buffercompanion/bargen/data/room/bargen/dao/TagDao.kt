package com.krayapp.buffercompanion.bargen.data.room.bargen.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity

@Dao
interface TagDao {
    @Upsert
    suspend fun upsertTag(tag: TagEntity)

    @Query("DELETE FROM tags WHERE :name == name")
    suspend fun removeByName(name: String)

    @Query("SELECT * FROM tags")
    suspend fun getTags(): List<TagEntity>

    @Query("select * from tags where :id == id")
    suspend fun getTagById(id: String): TagEntity
}
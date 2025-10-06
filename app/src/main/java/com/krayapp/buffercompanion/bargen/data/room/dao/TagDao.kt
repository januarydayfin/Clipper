package com.krayapp.buffercompanion.bargen.data.room.bargen.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity

@Dao
interface TagDao {
    @Upsert
    suspend fun upsertTag(tag: TagEntity)

    @Upsert
    suspend fun upsertTags(list: List<TagEntity>)

    @Query("DELETE FROM tags WHERE :name == name")
    suspend fun removeByName(name: String)

    @Query("DELETE FROM tags WHERE :id == id")
    suspend fun removeById(id: String)

    @Query("SELECT * FROM tags")
    suspend fun getTags(): List<TagEntity>

    @Query("SELECT * FROM tags")
    fun getTagsPaging(): PagingSource<Int, TagEntity>

    @Query("select * from tags where :id == id")
    suspend fun getTagById(id: String): TagEntity

    @Query("select * from tags WHERE name LIKE '%' || :name || '%'")
    suspend fun findTagByName(name: String): List<TagEntity>
}
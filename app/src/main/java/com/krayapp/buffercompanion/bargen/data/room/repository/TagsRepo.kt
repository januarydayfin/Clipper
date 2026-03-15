package com.krayapp.buffercompanion.bargen.data.room.repository

import com.krayapp.buffercompanion.bargen.data.room.entity.TagEntity

interface TagsRepo {
    suspend fun upsertTags(list: List<TagEntity>)
    suspend fun removeTagById(id: String)
    suspend fun findTagWithName(name: String): List<TagEntity>
    suspend fun getTagsWithIds(list: List<String>): List<TagEntity>
    suspend fun getAllTags(): List<TagEntity>
    suspend fun getTagsWithFilter(filter: String): List<TagEntity>
}
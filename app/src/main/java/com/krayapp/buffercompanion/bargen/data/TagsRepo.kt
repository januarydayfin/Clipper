package com.krayapp.buffercompanion.bargen.data

import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.utils.provideDatabase
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TagsRepo {
    private val tags = provideDatabase<BargenDB>().tagsDao()

    suspend fun upsertTag(tag: TagEntity) {
        withIO { tags.upsertTag(tag) }
    }

    suspend fun upsertTags(list: List<TagEntity>) {
        withIO { tags.upsertTags(list) }
    }
    suspend fun removeTagById(id: String) {
        withIO { tags.removeById(id) }
    }

    suspend fun findTagWithName(name: String) = withContext(Dispatchers.IO) {
        tags.findTagByName(name)
    }
    suspend fun filterTagsByName(name: String) = withContext(Dispatchers.IO) {
        tags.getTags().filter { it.name.contains(name, true) }
    }

    suspend fun getTagsWithIds(list: List<String>) = withContext(Dispatchers.IO) {
        tags.getTags().filter { it.id in list }
    }

    suspend fun getAllTags() =
        tags.getTags()
}
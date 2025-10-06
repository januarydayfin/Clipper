package com.krayapp.buffercompanion.bargen.domain.repository

import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.domain.provideDatabase
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TagsRepoImpl : TagsRepo {
    private val tags = provideDatabase<BargenDB>().tagsDao()

    override suspend fun upsertTags(list: List<TagEntity>) {
        withIO { tags.upsertTags(list) }
    }

    override suspend fun removeTagById(id: String) {
        withIO { tags.removeById(id) }
    }

    override suspend fun findTagWithName(name: String) = withContext(Dispatchers.IO) {
        tags.findTagByName(name)
    }

    override suspend fun getTagsWithIds(list: List<String>) = withContext(Dispatchers.IO) {
        tags.getTags().filter { it.id in list }
    }

    override suspend fun getAllTags() =
        tags.getTags()
}
package com.krayapp.buffercompanion.bargen.domain.usecase.tags

import com.krayapp.buffercompanion.bargen.data.room.entity.TagEntity
import com.krayapp.buffercompanion.bargen.data.room.repository.BarcodeRepo
import com.krayapp.buffercompanion.bargen.data.room.repository.TagsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TagsUsecase(
    private val tagsRepo: TagsRepo,
    private val barcodeRepo: BarcodeRepo
) {

    suspend fun getTags(filter: String) = withContext(Dispatchers.IO) {
        val tagList = if (filter.isEmpty())
            tagsRepo.getAllTags()
        else
            tagsRepo.getTagsWithFilter(filter)

        tagList
    }


    suspend fun saveTags(tags: List<TagEntity>) {
        tagsRepo.upsertTags(tags)
    }

    suspend fun loadTagsUiModelsByIds(ids: List<String>) = withContext(Dispatchers.IO) {
        tagsRepo.getTagsWithIds(ids)
    }

    suspend fun removeTagById(id: String) {
        tagsRepo.removeTagById(id)
        barcodeRepo.removeTagFromBarcodes(id)
    }

    suspend fun findTagsWithName(name: String) = withContext(Dispatchers.IO) {
        tagsRepo.findTagWithName(name)
    }
}
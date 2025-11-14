package com.krayapp.buffercompanion.bargen.domain.usecase.tags

import com.krayapp.buffercompanion.bargen.data.room.entity.TagEntity
import com.krayapp.buffercompanion.bargen.domain.repository.BarcodeRepo
import com.krayapp.buffercompanion.bargen.domain.repository.TagsRepo
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TagsUsecase : KoinComponent {
    private val tagsRepo: TagsRepo by inject()
    private val barcodeRepo: BarcodeRepo by inject()

    suspend fun getTags(filter: String) = withContext(Dispatchers.IO) {
        val tagList = if (filter.isEmpty())
            tagsRepo.getAllTags()
        else
            tagsRepo.getTagsWithFilter(filter)

        tagList
    }


    suspend fun saveTags(tags: List<TagEntity>) {
        withIO {
            tagsRepo.upsertTags(tags)
        }
    }

    suspend fun loadTagsUiModelsByIds(ids: List<String>) = withContext(Dispatchers.IO) {
        tagsRepo.getTagsWithIds(ids)
    }

    suspend fun removeTagById(id: String) {
        withIO {
            tagsRepo.removeTagById(id)
            barcodeRepo.removeTagFromBarcodes(id)
        }
    }

    suspend fun findTagsWithName(name: String) = withContext(Dispatchers.IO) {
        tagsRepo.findTagWithName(name)
    }
}
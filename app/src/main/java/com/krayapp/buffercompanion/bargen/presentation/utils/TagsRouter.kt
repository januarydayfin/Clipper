package com.krayapp.buffercompanion.bargen.presentation.utils

import com.krayapp.buffercompanion.bargen.domain.usecase.tags.TagsUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toEntity
import com.krayapp.buffercompanion.bargen.presentation.mapper.toTagUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.withIO
import org.koin.core.component.KoinComponent

object TagsRouter : KoinComponent {
    private val tagsUsecase = TagsUsecase
    suspend fun getTags(filter: String, onLoaded: (List<TagUiModel>) -> Unit) {
        withIO {
            val tagList = tagsUsecase.getTags(filter)
            onLoaded(tagList.map { it.toTagUiModel() })
        }
    }

    suspend fun saveTags(tags: List<TagUiModel>) {
        withIO {
            tagsUsecase.saveTags(tags.map { it.toEntity() })
        }
    }

    suspend fun loadTagsUiModelsByIds(ids: List<String>, onSuccess: (List<TagUiModel>) -> Unit) {
        withIO {
            onSuccess(tagsUsecase.loadTagsUiModelsByIds(ids).map { it.toTagUiModel() })
        }
    }

    suspend fun removeTagById(id: String) {
        withIO {
            tagsUsecase.removeTagById(id)
        }
    }

    suspend fun findTagsWithName(name: String) = tagsUsecase.findTagsWithName(name)
}
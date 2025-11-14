package com.krayapp.buffercompanion.bargen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.usecase.tags.TagsUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toEntity
import com.krayapp.buffercompanion.bargen.presentation.mapper.toTagUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TagsViewModel : ViewModel(), KoinComponent {
    val tagSelector: TagSelector by inject()
    private val tagsUsecase = TagsUsecase
    fun getTags(filter: String, onLoaded: (List<TagUiModel>) -> Unit) {
        launchInIO {
            val tagList = tagsUsecase.getTags(filter)
            onLoaded(tagList.map { it.toTagUiModel() })
        }
    }

    fun saveTags(tags: List<TagUiModel>) {
        launchInIO {
            tagsUsecase.saveTags(tags.map { it.toEntity() })
        }
    }

    fun loadTagsUiModelsByIds(ids: List<String>, onSuccess: (List<TagUiModel>) -> Unit) {
        launchInIO {
            onSuccess(tagsUsecase.loadTagsUiModelsByIds(ids).map { it.toTagUiModel() })
        }
    }

    fun removeTagById(id: String) {
        launchInIO {
            tagSelector.forceUncheck(id)
            tagsUsecase.removeTagById(id)
        }
    }
}
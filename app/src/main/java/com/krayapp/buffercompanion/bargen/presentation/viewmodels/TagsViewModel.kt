package com.krayapp.buffercompanion.bargen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.krayapp.buffercompanion.bargen.data.BarcodeRepo
import com.krayapp.buffercompanion.bargen.data.TagsRepo
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.presentation.uiModels.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import com.krayapp.buffercompanion.bargen.utils.toEntity
import com.krayapp.buffercompanion.bargen.utils.toTagUiModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TagsViewModel : ViewModel(), KoinComponent {
    val tagSelector: TagSelector by inject()
    private val tagsRepo: TagsRepo by inject()
    private val barcodeRepo: BarcodeRepo by inject()

    fun getTags(onLoaded: (List<TagUiModel>) -> Unit) {
        launchInIO {
            onLoaded(tagsRepo.getAllTags().map { it.toTagUiModel() })
        }
    }

    fun saveTags(tags: List<TagUiModel>) {
        launchInIO {
            tagsRepo.upsertTags(tags.map { it.toEntity() })
        }
    }

    fun loadTagsUiModelsByIds(ids: List<String>, onSuccess: (List<TagUiModel>) -> Unit) {
        launchInIO {
            onSuccess(tagsRepo.getTagsWithIds(ids).map { it.toTagUiModel() })
        }
    }

    fun removeTagById(id: String) {
        launchInIO {
            tagsRepo.removeTagById(id)
            barcodeRepo.removeTagFromBarcodes(id)
        }
    }
}
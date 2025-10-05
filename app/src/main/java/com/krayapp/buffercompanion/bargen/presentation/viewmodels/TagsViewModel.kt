package com.krayapp.buffercompanion.bargen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.krayapp.buffercompanion.bargen.data.BarcodeRepo
import com.krayapp.buffercompanion.bargen.data.TagsRepo
import com.krayapp.buffercompanion.bargen.presentation.uiModels.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import com.krayapp.buffercompanion.bargen.utils.toEntity
import com.krayapp.buffercompanion.bargen.utils.toTagUiModel

class TagsViewModel : ViewModel() {
    private val tagsRepo = TagsRepo()
    private val barcodeRepo = BarcodeRepo()

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

    fun removeTagById(id: String) {
        launchInIO {
            tagsRepo.removeTagById(id)
            barcodeRepo.removeTagFromBarcodes(id)
        }
    }
}
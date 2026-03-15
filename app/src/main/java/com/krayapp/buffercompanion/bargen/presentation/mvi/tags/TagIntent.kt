package com.krayapp.buffercompanion.bargen.presentation.mvi.tags

import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel

sealed interface TagIntent {
    data object Init: TagIntent
    data class DeleteTag(val id: String): TagIntent
    data class CheckTag(val id: String): TagIntent
    data class UncheckTag(val id: String): TagIntent

    data class SaveTag(val models: List<TagUiModel>): TagIntent

    data class FilterTags(val filter: String): TagIntent
}
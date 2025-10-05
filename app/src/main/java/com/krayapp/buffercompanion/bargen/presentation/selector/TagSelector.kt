package com.krayapp.buffercompanion.bargen.presentation.selector

import com.krayapp.buffercompanion.bargen.presentation.uiModels.TagUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TagSelector {
    private val _tagsFilterFlow = MutableStateFlow<List<TagUiModel>>(emptyList())
    val tagFilterFlow = _tagsFilterFlow.asStateFlow()
}
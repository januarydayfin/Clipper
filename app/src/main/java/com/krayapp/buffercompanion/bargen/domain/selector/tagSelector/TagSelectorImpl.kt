package com.krayapp.buffercompanion.bargen.domain.selector.tagSelector

import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TagSelectorImpl : TagSelector {
    private val _tagsFilterFlow = MutableStateFlow<List<String>>(emptyList())

    override val tagsFilterFlow
        get() = _tagsFilterFlow.asStateFlow()

    override suspend fun checkTag(id: String) {
        withIO {
            val newList = mutableListOf<String>().apply {
                addAll(_tagsFilterFlow.value)
            }

            val index = newList.indexOf(id)
            if (index != -1)
                newList.removeAt(index)
            else
                newList.add(id)

            _tagsFilterFlow.value = newList
        }
    }

    override suspend fun forceUncheck(id: String) {
        val newList = mutableListOf<String>().apply {
            addAll(_tagsFilterFlow.value)
        }

        newList.remove(id)
        _tagsFilterFlow.value = newList
    }

    override suspend fun cleanSelection() {
        _tagsFilterFlow.value = emptyList()
    }
}
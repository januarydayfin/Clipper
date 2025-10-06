package com.krayapp.buffercompanion.bargen.domain.selector

import android.util.Log
import com.krayapp.buffercompanion.bargen.data.TagsRepo
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TagSelectorImpl() : TagSelector {
    private val _tagsFilterFlow = MutableStateFlow<List<String>>(emptyList())
    override val tagFilterFlow
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

    override suspend fun uncheckTag(id: String) {
        val newList = _tagsFilterFlow.value.toMutableList()
        newList.remove(id)
        _tagsFilterFlow.emit(newList)
    }
}
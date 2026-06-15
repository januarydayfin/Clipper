package com.krayapp.buffercompanion.bargen.presentation.mvi.tags

import com.krayapp.buffercompanion.bargen.domain.usecase.tags.TagsUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toTagUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TagFounder(private val tagsUsecase: TagsUsecase) {
    private val _existTags = MutableStateFlow<List<TagUiModel>>(emptyList())
    private val _applyingTags = MutableStateFlow<List<TagUiModel>>(emptyList())

    private val applyingTagList = mutableListOf<TagUiModel>()
    val existTags = _existTags.asStateFlow()
    val applyingTags = _applyingTags.asStateFlow()

    private val allTags = mutableListOf<TagUiModel>()


    suspend fun findTags(rawString: String) {
        withIO {
            if (allTags.isEmpty()) {
                val foundTags = tagsUsecase.getTags("").map { it.toTagUiModel() }
                allTags.addAll(foundTags)
            }

            val tagsNames = rawString.trim().splitRawTagsForNames()

            searchFromExistingTags(runCatching { tagsNames.last() }.getOrElse { "" })

            applyingTagList.clear()

            tagsNames
                .filter { it.isNotEmpty() && it.isNotBlank() }
                .forEach { name ->
                    val exactTag = allTags.find { it.name == name }
                    applyingTagList.add(exactTag ?: TagUiModel(name = name))
                }
            _applyingTags.emit(applyingTagList.toList())
        }

    }

    private suspend fun searchFromExistingTags(name: String) {
        val existTagList = mutableListOf<TagUiModel>()
        withIO {
            if (name.isEmpty() || name.isBlank()) {
                _existTags.emit(emptyList())
                return@withIO
            }
            val applyNamed = applyingTagList.map { it.name }
            val fetchedTags = allTags.filter {
                it.name.contains(name, ignoreCase = true)
                        && applyNamed.contains(it.name).not()
            }
            existTagList.clear()
            existTagList.addAll(fetchedTags)
            _existTags.emit(existTagList.toList())
        }
    }

    fun onDispose() {
        allTags.clear()
    }

    private fun CharSequence.splitRawTagsForNames() =
        if (isEmpty()) emptyList() else this.toString().split(",").map { it.trim() }

}
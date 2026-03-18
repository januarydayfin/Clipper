package com.krayapp.buffercompanion.bargen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.usecase.tags.TagsUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toEntity
import com.krayapp.buffercompanion.bargen.presentation.mapper.toTagUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.tags.TagIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.tags.TagSideEffect
import com.krayapp.buffercompanion.bargen.presentation.mvi.tags.TagState
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class TagsViewModel(
    private val tagSelector: TagSelector,
    private val tagsUsecase: TagsUsecase
) : ViewModel(), ContainerHost<TagState, TagSideEffect> {
    override val container: Container<TagState, TagSideEffect> = container(TagState(emptyList()))
    val state: StateFlow<TagState>
        get() = container.stateFlow

    private val allTags = mutableListOf<TagUiModel>()
    private val checkedTagsIds = mutableListOf<String>()

    init {
        launchInIO {
            allTags.addAll(tagsUsecase.getTags("").map { it.toTagUiModel() })
            tagSelector.tagsFilterFlow.collectLatest { checkedList ->
                checkedTagsIds.clear()
                checkedTagsIds.addAll(checkedList)
                val checkedList = allTags.map { it.copy(checked = it.id in checkedTagsIds) }
                intent {
                    reduce {
                        state.copy(list = checkedList)
                    }
                }
            }
        }
    }

    fun onIntent(intent: TagIntent) {
        launchInIO {
            when (intent) {
                is TagIntent.Init -> {
                    launchInIO {
                        val list = tagsUsecase.getTags("").map { it.toTagUiModel() }
                        allTags.clear()
                        allTags.addAll(list)
                        val checkedList = list.map { it.copy(checked = it.id in checkedTagsIds) }
                        intent {
                            reduce {
                                state.copy(list = checkedList)
                            }
                        }
                    }
                }

                is TagIntent.CheckTag -> {
                    tagSelector.checkTag(intent.id)
                }

                is TagIntent.DeleteTag -> {
                    tagsUsecase.removeTagById(intent.id)
                    removeTagFromState(intent.id)
                }

                is TagIntent.UncheckTag -> {
                    tagSelector.forceUncheck(intent.id)
                }

                is TagIntent.SaveTag -> {
                    tagsUsecase.saveTags(intent.models.map { it.toEntity() })

                    if (intent.models.isNotEmpty())
                        updateTagInState(intent.models.first())
                }

                is TagIntent.FilterTags -> {
                    filterTags(intent.filter)
                }
            }
        }
    }

    private fun filterTags(filter: String) {
        intent {
            reduce {
                val filtered = allTags.filter { it.name.contains(filter) }
                state.copy(list = filtered)
            }
        }
    }

    private fun removeTagFromState(id: String) {
        intent {
            reduce {
                val removedList = state.list.toMutableList().apply {
                    removeIf { it.id == id }
                }
                state.copy(list = removedList)
            }
        }
    }

    private fun updateTagInState(model: TagUiModel) {
        intent {
            reduce {
                val indexOfModel = state.list.indexOfFirst { it.id == model.id }
                val replaced = state.list.toMutableList().apply {
                    set(indexOfModel, model)
                }
                state.copy(list = replaced)
            }
        }
    }
}
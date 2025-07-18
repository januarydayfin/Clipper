package com.krayapp.buffercompanion

import androidx.lifecycle.ViewModel
import com.krayapp.buffercompanion.data.BargenRepo
import com.krayapp.buffercompanion.data.FilterState
import com.krayapp.buffercompanion.data.SortType
import com.krayapp.buffercompanion.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.ui.models.TagUiModel
import com.krayapp.buffercompanion.utils.launchInIO
import com.krayapp.buffercompanion.utils.toBarcodeUiModel
import com.krayapp.buffercompanion.utils.toTagUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BargenViewModel : ViewModel() {
    private val repo = BargenRepo()
    private val _barcodeFlow = MutableStateFlow<List<BarcodeUiModel>>(emptyList())
    private val _tagsFlow = MutableStateFlow<List<TagUiModel>>(emptyList())

    val barcodeFlow = _barcodeFlow.asStateFlow()
    val tagsFlow = _tagsFlow.asStateFlow()

    private var sortType = SortType.DATE_ASC
        set(value) {
            field = value
            updateBarcodeFlow()
        }

    private var filterState = FilterState()
        set(value) {
            field = value
            updateBarcodeFlow()
            updateTagsFlow()
        }

    fun updateTagFilter(tagIds: List<String>) {
        filterState = filterState.copy(tagIds = tagIds)
    }

    fun updateNameFilter(name: String) {
        filterState = filterState.copy(nameFilter = name)
    }

    private fun updateTagsFlow() {
        launchInIO {
            val tags = filterState.tagIds.run {
                repo.getTagsWithIds(this)
            }.map { it.toTagUiModel() }

            _tagsFlow.emit(tags)
        }
    }

    private fun updateBarcodeFlow() {
        launchInIO {
            val nameFilter = filterState.nameFilter

            //Сначала пытаемся фильтровать по имени
            val filteredWithName = if (nameFilter != null)
                repo.searchBarcodesByName(nameFilter)
            else
                null

            //Потом пытаемся отфильтровать полученный список еще и по тегам
            val dataToEmit = if (filterState.tagIds.isNotEmpty())
                repo.filterBarcodesWithTags(
                    tags = filterState.tagIds,
                    sort = sortType,
                    sourceList = filteredWithName
                )
            else
                filteredWithName ?: repo.getAllBarcodes(sortType)

            _barcodeFlow.emit(dataToEmit.map { it.toBarcodeUiModel() })
        }
    }
}
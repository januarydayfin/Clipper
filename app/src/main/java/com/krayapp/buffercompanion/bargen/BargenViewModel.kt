package com.krayapp.buffercompanion.bargen

import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.data.BargenRepo
import com.krayapp.buffercompanion.bargen.data.FilterState
import com.krayapp.buffercompanion.bargen.data.SortType
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import com.krayapp.buffercompanion.bargen.utils.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.utils.toTagUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BargenViewModel : ViewModel() {
    private val repo = BargenRepo()
    private val _barcodeFlow = MutableStateFlow<List<BarcodeUiModel>>(emptyList())
    private val _tagsFlow = MutableStateFlow<List<TagUiModel>>(emptyList())

    val barcodeFlow = _barcodeFlow.asStateFlow()
    val tagsFlow = _tagsFlow.asStateFlow()

    private var sortType: SortType = SortType.DATE_ASC
        get() = SortType.valueOf(ClipperApp.getPrefs().sortType)
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

    fun createBarcodeRecord(
        text: String,
        format: BarcodeFormat = BarcodeFormat.QR_CODE,
        onCreated: (BarcodeUiModel) -> Unit = { }
    ) {
        launchInIO {
            val entity = BarcodeEntity(
                content = text,
                type = format.toString()
            )
            repo.upsertBarcode(entity)
            onCreated(entity.toBarcodeUiModel())
            updateBarcodeFlow()
        }
    }

    fun createBarcodeRecord(
        text: String,
        name: String,
        description: String = "",
        tags: List<String> = emptyList(),
        format: BarcodeFormat = BarcodeFormat.QR_CODE,
        onCreated: (BarcodeUiModel) -> Unit = { }
    ) {
        launchInIO {
            val entity = BarcodeEntity(
                content = text,
                name = name,
                description = description,
                tags = tags,
                type = format.toString()
            )
            repo.upsertBarcode(entity)
            onCreated(entity.toBarcodeUiModel())
            updateBarcodeFlow()
        }
    }

    fun incrementUsageCount(id: String) {
        launchInIO {
            repo.incrementUsageCount(id)

            if (sortType == SortType.USAGE)
                updateBarcodeFlow()
        }
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
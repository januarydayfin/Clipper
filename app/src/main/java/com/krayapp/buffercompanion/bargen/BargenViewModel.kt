package com.krayapp.buffercompanion.bargen

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.data.BargenRepo
import com.krayapp.buffercompanion.bargen.data.FilterState
import com.krayapp.buffercompanion.bargen.data.SortType
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import com.krayapp.buffercompanion.bargen.utils.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.utils.toTagUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class BargenViewModel : ViewModel() {
    private val repo = BargenRepo()
    private val _barcodeFlow = MutableStateFlow<List<BarcodeUiModel>>(emptyList())
    private val _tagsFilterFlow = MutableStateFlow<List<TagUiModel>>(emptyList())

    val barcodeFlow = _barcodeFlow.asStateFlow()
    val tagFilterFlow = _tagsFilterFlow.asStateFlow()

    private var sortType: SortType
        get() = SortType.valueOf(ClipperApp.getPrefs().sortType)
        set(value) {
            ClipperApp.getPrefs().sortType = value.toString()
            updateBarcodeFlow()
        }

    private var filterState = FilterState()
        set(value) {
            field = value
            updateBarcodeFlow()
            updateTagsFlow()
        }

    init {
        updateBarcodeFlow()
    }

    fun loadAllTags(onLoaded: suspend (List<TagUiModel>) -> Unit) {
        launchInIO {
            onLoaded(repo.getAllTags().map { it.toTagUiModel() })
        }
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

    suspend fun findTagWithName(name: String) =
        withContext(Dispatchers.IO) {
            repo.findTagWithName(name)?.toTagUiModel()
        }


    fun removeTagById(id: String) {
        launchInIO {
            repo.removeTagById(id)
            repo.removeTagFromBarcodes(id)
            updateBarcodeFlow()
        }
    }

    fun changeSort(sortType: SortType) {
        this.sortType = sortType
    }

    fun clearTagFilter() {
        launchInIO {
            filterState = filterState.copy(tagIds = emptyList())
        }
    }

    fun removeBarcode(id: String) {
        launchInIO {
            repo.removeBarcodeById(id)
            updateBarcodeFlow()
        }
    }

    fun removeBarcodes(ids: List<String>) {
        launchInIO {
            repo.removeBarcodesByIds(ids)
            updateBarcodeFlow()
        }
    }

    fun createBarcodeRecord(
        entity: BarcodeEntity,
        onCreated: (BarcodeUiModel) -> Unit = { }
    ) {
        launchInIO {
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

    fun removeChipFromFilter(id: String) {
        val withoutTag = filterState.tagIds.filter { it != id }
        filterState = filterState.copy(tagIds = withoutTag)
    }

    fun updateNameFilter(name: String) {
        filterState = filterState.copy(nameFilter = name)
    }

    private fun updateTagsFlow() {
        launchInIO {
            val tags = filterState.tagIds.run {
                repo.getTagsWithIds(this)
            }.map { it.toTagUiModel() }

            _tagsFilterFlow.emit(tags)
        }
    }

    fun recordTags(tags: List<TagEntity>) {
        launchInIO {
            repo.upsertTags(tags)
        }
    }

    private fun updateBarcodeFlow() {
        launchInIO {
            val nameFilter = filterState.nameFilter

            Log.d("FATA", String.format("%s", sortType))

            //Сначала пытаемся фильтровать по имени
            val filteredWithName = if (!nameFilter.isNullOrEmpty())
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

            val allTags = repo.getAllTags()

            _barcodeFlow.emit(dataToEmit.map { it.toBarcodeUiModel(allTags) })
        }
    }
}
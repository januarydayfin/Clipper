package com.krayapp.buffercompanion.bargen.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.data.BargenRepo
import com.krayapp.buffercompanion.bargen.data.FilterState
import com.krayapp.buffercompanion.bargen.data.SortType
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.ui.mvi.stateManager.Effect
import com.krayapp.buffercompanion.bargen.ui.mvi.stateManager.StateManager
import com.krayapp.buffercompanion.bargen.utils.currentSortType
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import com.krayapp.buffercompanion.bargen.utils.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.utils.toEntity
import com.krayapp.buffercompanion.bargen.utils.toTagUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class BargenViewModel : ViewModel() {
    private val repo = BargenRepo()

    private val _tagsFilterFlow = MutableStateFlow<List<TagUiModel>>(emptyList())
    val tagFilterFlow = _tagsFilterFlow.asStateFlow()

    private val stateManager = StateManager(viewModelScope)

    val uiState = stateManager.state

    private var sortType: SortType
        get() = currentSortType
        set(value) {
            ClipperApp.getPrefs().sortType = value.toString()
        }

    private var filterState = FilterState()
        set(value) {
            field = value
            updateTagsFlow()
        }

    init {
    }

    fun barcodePagingData() =
        Pager(
            config = PagingConfig(pageSize = 25, prefetchDistance = 10, enablePlaceholders = true),
            pagingSourceFactory = {
                repo.getAllBarcodesPaging(sortType)
            }).flow.cachedIn(viewModelScope)

    fun onIntent(intent: MainIntent) {
        stateManager.onIntent(intent)
    }

    fun recycleEffect(effect: Effect) {
        stateManager.recycleEffect(effect)
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
        }
    }

    suspend fun findTagWithName(name: String): List<TagUiModel> =
        withContext(Dispatchers.IO) {
            repo.findTagWithName(name).map { it.toTagUiModel() }
        }

    suspend fun findBarcodeById(id: String) =
        withContext(Dispatchers.IO) {
            repo.getBarcodeById(id)
        }

    fun removeTagById(id: String) {
        launchInIO {
            repo.removeTagById(id)
            repo.removeTagFromBarcodes(id)
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
        }
    }

    fun removeBarcodes(ids: List<String>) {
        launchInIO {
            repo.removeBarcodesByIds(ids)
        }
    }


    fun createBarcodeRecord(
        entity: BarcodeEntity,
        onCreated: (BarcodeUiModel) -> Unit = { }
    ) {
        launchInIO {
            repo.upsertBarcode(entity)
            onCreated(entity.toBarcodeUiModel())
        }
    }

    fun incrementUsageCount(id: String) {
        launchInIO {
            repo.incrementUsageCount(id)
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
        filterState = filterState.copy(searchFilter = name)
    }

    private fun updateTagsFlow() {
        launchInIO {
            val tags = filterState.tagIds.run {
                repo.getTagsWithIds(this)
            }.map { it.toTagUiModel() }

            _tagsFilterFlow.emit(tags)
        }
    }

    fun recordTags(tags: List<TagUiModel>) {
        launchInIO {
            repo.upsertTags(tags.map { it.toEntity() })
        }
    }

    fun recordTag(tag: TagUiModel) {
        launchInIO {
            repo.upsertTag(tag.toEntity())
        }
    }

}
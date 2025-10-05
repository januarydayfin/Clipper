package com.krayapp.buffercompanion.bargen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.data.BarcodeRepo
import com.krayapp.buffercompanion.bargen.data.FilterState
import com.krayapp.buffercompanion.bargen.data.SortType
import com.krayapp.buffercompanion.bargen.data.TagsRepo
import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.presentation.mvi.Effect
import com.krayapp.buffercompanion.bargen.presentation.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.uiModels.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.uiModels.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager.StateManager
import com.krayapp.buffercompanion.bargen.utils.currentSortType
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import com.krayapp.buffercompanion.bargen.utils.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.utils.toTagUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class BargenViewModel : ViewModel() {
    private val barcodeRepo = BarcodeRepo()
    private val tagsRepo = TagsRepo()

    private val _tagsFilterFlow = MutableStateFlow<List<TagUiModel>>(emptyList())
    val tagFilterFlow = _tagsFilterFlow.asStateFlow()

    private val stateManager = StateManager(viewModelScope)

    val uiState = stateManager.state

    private val _sortType = MutableStateFlow(currentSortType)
    val sortType = _sortType.asStateFlow()

    private val filterState = MutableStateFlow(FilterState())


    @OptIn(ExperimentalCoroutinesApi::class)
    val barcodePagingData: Flow<PagingData<BarcodeUiModel>> =
        filterState.combine(_sortType) { filter, sort ->
            filter to sort
        }.flatMapLatest { filterSort ->
            val filterState = filterSort.first
            val sort = filterSort.second
            // Создаём новый Pager при каждом изменении filterState
            Pager(
                config = PagingConfig(
                    pageSize = 25,
                    prefetchDistance = 10,
                    enablePlaceholders = true
                ),
                pagingSourceFactory = {
                    when {
                        filterState.searchFilter.isNotEmpty() -> {
                            barcodeRepo.getFilteredBarcodesByNamePaging(filterState.searchFilter)
                        }
//                            currentFilterState.tagIds.isNotEmpty() -> {
//                                barcodeRepo.getfi
//                            }
                        else -> barcodeRepo.getAllBarcodesPaging(sort)
                    }
                }
            )
                .flow
                .map { page ->
                    page.map { it.toBarcodeUiModel() }
                }
        }
            // Сохраняем результат в кэше ViewModel для обработки конфигурационных изменений
            .cachedIn(viewModelScope)

    fun onIntent(intent: MainIntent) {
        stateManager.onIntent(intent)
    }

    fun recycleEffect(effect: Effect) {
        stateManager.recycleEffect(effect)
    }

    fun updatePager() {
        launchInIO {
            filterState.emit(filterState.value.copy(manualUpdate = UUID.randomUUID().toString()))
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
            barcodeRepo.upsertBarcode(entity)
            onCreated(entity.toBarcodeUiModel())
        }
    }

    suspend fun findTagWithName(name: String): List<TagUiModel> =
        withContext(Dispatchers.IO) {
            tagsRepo.findTagWithName(name).map { it.toTagUiModel() }
        }


    fun removeTagById(id: String) {
        launchInIO {
            tagsRepo.removeTagById(id)
            barcodeRepo.removeTagFromBarcodes(id)
        }
    }

    fun changeSort(sortType: SortType) {
        this._sortType.value = sortType
        ClipperApp.getPrefs().sortType = sortType.toString()
    }

    fun clearTagFilter() {
        launchInIO {
            filterState.emit(filterState.value.copy(tagIds = emptyList()))
        }
    }

    fun removeBarcodes(vararg ids: String) {
        launchInIO {
            barcodeRepo.removeBarcodesByIds(ids.toList())
        }
    }


    fun createBarcodeRecord(
        entity: BarcodeEntity,
        onCreated: (BarcodeUiModel) -> Unit = { }
    ) {
        launchInIO {
            barcodeRepo.upsertBarcode(entity)
            onCreated(entity.toBarcodeUiModel())
        }
    }

    fun incrementUsageCount(id: String) {
        launchInIO {
            barcodeRepo.incrementUsageCount(id)
            updatePager()
        }
    }

    fun updateTagFilter(tagIds: List<String>) {
        launchInIO {
            filterState.emit(filterState.value.copy(tagIds = tagIds))
        }
    }

    fun removeChipFromFilter(id: String) {
        launchInIO {
            val withoutTag = filterState.value.tagIds.filter { it != id }
            filterState.emit(filterState.value.copy(tagIds = withoutTag))
        }

    }

    fun updateNameFilter(name: String) {
        launchInIO {
            filterState.emit(filterState.value.copy(searchFilter = name))
        }
    }

    private fun updateTagsFlow() {
        launchInIO {
            val tags = filterState.value.tagIds.run {
                tagsRepo.getTagsWithIds(this)
            }.map { it.toTagUiModel() }

            _tagsFilterFlow.emit(tags)
        }
    }
}
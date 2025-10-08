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
import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.pagingSource.BarcodeFilterTagsPagingSource
import com.krayapp.buffercompanion.bargen.domain.repository.BarcodeRepo
import com.krayapp.buffercompanion.bargen.domain.repository.TagsRepo
import com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector.CardSelector
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.type.SortType
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mapper.toTagUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.Effect
import com.krayapp.buffercompanion.bargen.presentation.mvi.FilterState
import com.krayapp.buffercompanion.bargen.presentation.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager.StateManager
import com.krayapp.buffercompanion.bargen.presentation.utils.currentSortType
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class BargenViewModel : ViewModel(), KoinComponent {
    private val barcodeRepo: BarcodeRepo by inject()
    private val tagsRepo: TagsRepo by inject()
    private val stateManager: StateManager by inject()

    val cardSelector: CardSelector by inject()
    private val tagsSelector: TagSelector by inject()

    val uiState = stateManager.state

    private val _sortType = MutableStateFlow(currentSortType)
    val sortType = _sortType.asStateFlow()

    private val filterState = MutableStateFlow(FilterState())

    val inSelection
        get() = cardSelector.selectedBarcodes.value.isNotEmpty()

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

                        filterState.tagIds.isNotEmpty() -> {
                            BarcodeFilterTagsPagingSource(filterState.tagIds)
                        }

                        else -> barcodeRepo.getAllBarcodesPaging(sort)
                    }
                }
            )
                .flow
                .map { page ->
                    page.map { it.toBarcodeUiModel() }
                }
        }
            .cachedIn(viewModelScope)

    init {
        launchInIO {
            tagsSelector.tagsFilterFlow.collectLatest {
                filterState.value = filterState.value.copy(tagIds = it.map { tag -> tag })
            }
        }
    }

    fun onIntent(intent: MainIntent) {
        launchInIO {
            stateManager.onIntent(intent)
        }
    }


    fun recycleEffect(effect: Effect) {
        launchInIO {
            stateManager.recycleEffect(effect)
        }
    }

    fun updatePager() {
        launchInIO {
            filterState.emit(filterState.value.copy(manualUpdate = UUID.randomUUID().toString()))
        }
    }

    fun createBarcodeRecord(
        text: String,
        format: BarcodeFormat = BarcodeFormat.QR_CODE,
        tagIds: List<String> = emptyList(),
        onCreated: (BarcodeUiModel) -> Unit = { }
    ) {
        launchInIO {
            val entity = BarcodeEntity(
                content = text,
                type = format.toString(),
                tags = tagIds
            )
            barcodeRepo.upsertBarcode(entity)
            onCreated(entity.toBarcodeUiModel())
        }
    }

    fun onNotEmptyData(onNotEmpty: () -> Unit) {
        launchInIO {
            val count = barcodeRepo.recordsCount()

            if (count != 0)
                onNotEmpty()
        }
    }

    suspend fun findTagWithName(name: String): List<TagUiModel> =
        withContext(Dispatchers.IO) {
            tagsRepo.findTagWithName(name).map { it.toTagUiModel() }
        }


    fun changeSort(sortType: SortType) {
        this._sortType.value = sortType
        ClipperApp.getPrefs().sortType = sortType.toString()
    }


    fun deleteBarcodes(vararg ids: String) {
        launchInIO {
            barcodeRepo.removeBarcodesByIds(ids.toList())
            updatePager()
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

    fun updateNameFilter(name: String) {
        launchInIO {
            filterState.emit(filterState.value.copy(searchFilter = name))
        }
    }
}
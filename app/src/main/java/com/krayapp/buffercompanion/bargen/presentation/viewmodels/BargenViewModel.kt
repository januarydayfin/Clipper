package com.krayapp.buffercompanion.bargen.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.domain.pagingSource.BarcodeFilterTagsPagingSource
import com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector.CardSelector
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.type.SortType
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.CreateBarcodeUsecase
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.GetAllBarcodesPaging
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.GetFilteredByNamePagingSource
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.IncrementBarcodeUsageCountUsecase
import com.krayapp.buffercompanion.bargen.domain.usecase.barcode.RemoveBarcodesByIds
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.FilterState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MviState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.SideEffect
import com.krayapp.buffercompanion.bargen.presentation.mvi.processing.MviProcessor
import com.krayapp.buffercompanion.bargen.presentation.mvi.processing.MviProcessorHandler
import com.krayapp.buffercompanion.bargen.presentation.utils.currentSortType
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class BargenViewModel : ViewModel(), KoinComponent {
    private val tagsSelector: TagSelector by inject()
    private val mviProcessor =
        MviProcessor(viewModel = this,
            handler = MviProcessorHandler(),
            updatePager = { updatePager() }
        )

    val state: StateFlow<MviState>
        get() = mviProcessor.container.stateFlow

    val sideEffects: Flow<SideEffect>
        get() = mviProcessor.container.sideEffectFlow

    private val _sortType = MutableStateFlow(currentSortType)
    val sortType = _sortType.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState = _filterState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val barcodePagingData: Flow<PagingData<BarcodeUiModel>> =
        _filterState.combine(_sortType) { filter, sort ->
            filter to sort
        }.flatMapLatest { filterSort ->
            val filterState = filterSort.first
            val sort = filterSort.second
            Pager(
                config = PagingConfig(
                    pageSize = 25,
                    prefetchDistance = 10,
                    enablePlaceholders = true
                ),
                pagingSourceFactory = {
                    when {
                        filterState.searchFilter.isNotEmpty() -> {
                            GetFilteredByNamePagingSource(filterState.searchFilter)
                        }

                        filterState.tagIds.isNotEmpty() -> {
                            BarcodeFilterTagsPagingSource(filterState.tagIds)
                        }

                        else -> GetAllBarcodesPaging(sort)
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
                _filterState.value = _filterState.value.copy(tagIds = it.map { tag -> tag })
            }
        }
        launchInIO {
            mviProcessor.onIntent(MainIntent.Init)
        }
    }

    fun onIntent(intent: MainIntent) {
        viewModelScope.launch(Dispatchers.IO) {
            mviProcessor.onIntent(intent)
        }
    }


    private fun updatePager() {
        launchInIO {
            _filterState.emit(
                _filterState.value.copy(
                    manualUpdate = UUID.randomUUID().toString()
                )
            )
        }
    }

    fun changeSort(sortType: SortType) {
        this._sortType.value = sortType
        ClipperApp.getPrefs().sortType = sortType.toString()
    }


    fun deleteBarcodes(vararg ids: String) {
        launchInIO {
            RemoveBarcodesByIds(ids.toList())
            updatePager()
        }
    }


    fun incrementUsageCount(id: String) {
        launchInIO {
            IncrementBarcodeUsageCountUsecase(id)
            updatePager()
        }
    }

    fun updateNameFilter(name: String) {
        launchInIO {
            _filterState.emit(_filterState.value.copy(searchFilter = name))
        }
    }
}
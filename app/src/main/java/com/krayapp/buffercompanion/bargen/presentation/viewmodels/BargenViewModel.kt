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
import com.krayapp.buffercompanion.bargen.domain.usecase.tags.TagsUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.FilterState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MviState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.SideEffect
import com.krayapp.buffercompanion.bargen.presentation.utils.currentSortType
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.UUID

class BargenViewModel : ContainerHost<MviState, SideEffect>, ViewModel(), KoinComponent {
    override val container = container<MviState, SideEffect>(MviState())

    val cardSelector: CardSelector by inject()
    private val tagsSelector: TagSelector by inject()


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
                filterState.value = filterState.value.copy(tagIds = it.map { tag -> tag })
            }
        }
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.ShowEmptyMainBottomSheet -> showMainBottomSheet()
            is MainIntent.HideBottomSheet -> hideBottomSheets()
            is MainIntent.ShowExistCodeBottomsheet -> showMainBottomSheet(intent.uiModel)
            is MainIntent.ShowSettingsBottomsheet -> showSettingsBottomSheet()
            is MainIntent.ShowTagsMenu -> showTagsBottomsheet()
            is MainIntent.CreateNewRecordFromRawData -> createBarcodeRecord(intent)
            is MainIntent.CreateNewRecordFromEntity -> createBarcodeRecord(intent)
        }
    }


    private fun showMainBottomSheet(uiModel: BarcodeUiModel = BarcodeUiModel.UNDEFINED) = intent {
        reduce {
            state.copy(mainBottomSheetState = BottomSheetStateData(uiModel))
        }
    }

    private fun showSettingsBottomSheet() = intent {
        reduce {
            state.copy(showSettingsBottomSheet = true)
        }
    }

    private fun showTagsBottomsheet() = intent {
        reduce {
            state.copy(showTagBottomSheet = true)
        }
    }

    private fun hideBottomSheets() = intent {
        reduce {
            state.copy(
                mainBottomSheetState = null,
                showTagBottomSheet = false,
                showSettingsBottomSheet = false,
                showSortBottomSheet = false
            )
        }
    }


    fun updatePager() {
        launchInIO {
            filterState.emit(
                filterState.value.copy(
                    manualUpdate = UUID.randomUUID().toString()
                )
            )
        }
    }


    private fun createBarcodeRecord(
        intent: MainIntent.CreateNewRecordFromRawData,
    ) {
        val text = intent.text
        val format = intent.format.toString()
        val tagIds = intent.tagIds

        launchInIO {
            val entity = CreateBarcodeUsecase(text = text, format = format, tagIds = tagIds)

            updatePager()

            if (ClipperApp.getPrefs().openCardAfterScan)
                showMainBottomSheet(entity.toBarcodeUiModel())
        }
    }

    private fun createBarcodeRecord(
        intent: MainIntent.CreateNewRecordFromEntity,
    ) {
        launchInIO {
            val entity = CreateBarcodeUsecase(intent.barcodeEntity)

            updatePager()

            if (ClipperApp.getPrefs().openCardAfterScan)
                showMainBottomSheet(entity.toBarcodeUiModel())
        }
    }


    suspend fun findTagWithName(name: String) = TagsUsecase.findTagsWithName(name)


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
            filterState.emit(filterState.value.copy(searchFilter = name))
        }
    }
}
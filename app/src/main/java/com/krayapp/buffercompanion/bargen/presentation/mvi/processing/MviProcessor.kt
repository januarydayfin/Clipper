package com.krayapp.buffercompanion.bargen.presentation.mvi.processing

import androidx.lifecycle.viewModelScope
import com.krayapp.buffercompanion.bargen.GlobalPrefs
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MviState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.SideEffect
import com.krayapp.buffercompanion.bargen.presentation.mvi.processing.pins.PinHandler
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class MviProcessor(
    private val viewModel: BargenViewModel,
    private val handler: MviProcessorHandler,
    private val updatePager: () -> Unit,
    private val prefs: GlobalPrefs
) : ContainerHost<MviState, SideEffect> {
    override val container = viewModel.container<MviState, SideEffect>(MviState())
    private val pinHandler = PinHandler(this)

    init {
        viewModel.launchInIO {
            pinHandler.init()
        }

        prefs.swipeToDeleteFlow.onEach { swipe ->
            intent {
                reduce {
                    state.copy(swipeToDelete = swipe)
                }
            }
        }.launchIn(viewModel.viewModelScope)

        viewModel.launchInIO {
            handler.cardSelectionFlow.collectLatest {
                intent {
                    reduce {
                        state.copy(selectedBarcodesIds = it)
                    }
                }
            }
        }

        /**
         * Фильтруются только закрепы, остальные карточки фильтруются в BargenViewModel в пейджере
         */
        viewModel.launchInIO {
            handler.tagFilterFlow.collectLatest { pinnedIds ->
                val filtered =
                    pinHandler.getAllPinnedBarcodes().map { it.toBarcodeUiModel() }.toMutableList()

                if (pinnedIds.isNotEmpty())
                    filtered.removeIf { it.id !in pinnedIds }

                intent {
                    reduce {
                        state.copy(pinnedBarcodes = filtered)
                    }
                }
            }
        }
    }

    suspend fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.PinIntent -> pinHandler.onIntent(intent)
            is MainIntent.ShowEmptyMainBottomSheet -> showMainBottomSheet()
            is MainIntent.HideBottomSheet -> {
                hideBottomSheets()
                updatePager()
            }

            is MainIntent.ShowExistCodeBottomsheet -> showMainBottomSheet(intent.uiModel)
            is MainIntent.ShowSettingsBottomsheet -> showSettingsBottomSheet()
            is MainIntent.ShowTagsMenu -> showTagsBottomsheet()
            is MainIntent.CreateNewRecord -> {
                val createdEntity = handler.createBarcodeRecord(intent)
                if (intent.showAfterCreate && prefs.openCardAfterScan)
                    showMainBottomSheet(createdEntity.toBarcodeUiModel())

                updatePager()
            }

            is MainIntent.CleanCardSelection -> handler.onCleanCardSelection()
            is MainIntent.CleanTagsSelection -> handler.onCleanTagsSelection()
            is MainIntent.DeleteAllSelectedCards -> {
                handler.onDeleteAllSelectedCards()
                updatePager()
                pinHandler.refreshPinnedBarcodes()
            }

            is MainIntent.CheckBarcodeForSelection -> handler.checkBarcodeForSelection(intent.id)
            is MainIntent.DeleteBarcode -> {
                handler.deleteBarcodeById(intent.id)
                delay(100)
                updatePager()
                pinHandler.refreshPinnedBarcodes()
            }
        }
    }

    fun hidePinned() {
        intent {
            reduce {
                state.copy(pinnedBarcodes = emptyList())
            }
        }
    }

    suspend fun showPinned() {
        pinHandler.refreshPinnedBarcodes()
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

}
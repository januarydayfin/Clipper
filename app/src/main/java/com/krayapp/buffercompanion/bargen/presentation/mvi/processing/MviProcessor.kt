package com.krayapp.buffercompanion.bargen.presentation.mvi.processing

import androidx.lifecycle.viewModelScope
import com.krayapp.buffercompanion.bargen.GlobalPrefs
import com.krayapp.buffercompanion.bargen.data.room.MAX_PINNED_COUNT
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetUiState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent.PinIntent.*
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MviState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.SideEffect
import com.krayapp.buffercompanion.bargen.presentation.mvi.processing.pins.PinHandler
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class MviProcessor(
    viewModel: BargenViewModel,
    private val handler: MviProcessorHandler,
    private val updatePager: () -> Unit,
    private val prefs: GlobalPrefs
) : ContainerHost<MviState, SideEffect> {
    override val container = viewModel.container<MviState, SideEffect>(MviState())
    private val pinHandler = PinHandler(this)

    private val pinUpdateTrigger = MutableSharedFlow<Unit>()

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
            handler.tagFilterFlow.combine(pinUpdateTrigger) { ids, _ ->
                ids
            }.collectLatest { pinnedIds ->
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
            is MainIntent.ShowTagsMenu -> showTagsBottomSheet()
            is MainIntent.CreateNewRecord -> {
                val pinnedSize = container.stateFlow.value.pinnedBarcodes.size
                var createdEntity = handler.createBarcodeRecord(intent)
                if (prefs.autoPinOnScan &&
                    pinnedSize < MAX_PINNED_COUNT
                ) {
                    pinHandler.onIntent(PinBarcode(createdEntity.id))
                    createdEntity = createdEntity.copy(pinnedPosition = pinnedSize)
                }

                if (intent.showAfterCreate && prefs.openCardAfterScan)
                    showMainBottomSheet(createdEntity.toBarcodeUiModel())

                onIntent(MainIntent.RefreshList)
            }

            is MainIntent.CleanCardSelection -> handler.onCleanCardSelection()
            is MainIntent.CleanTagsSelection -> handler.onCleanTagsSelection()
            is MainIntent.DeleteAllSelectedCards -> {
                handler.onDeleteAllSelectedCards()
                onIntent(MainIntent.RefreshList)

            }

            is MainIntent.CheckBarcodeForSelection -> handler.checkBarcodeForSelection(intent.id)
            is MainIntent.DeleteBarcode -> {
                handler.deleteBarcodeById(intent.id)
                onIntent(MainIntent.RefreshList)
            }

            MainIntent.RefreshList -> {
                updatePager()
                pinUpdateTrigger.emit(Unit)
            }
        }
    }

    fun enterSearchMode() {
        intent {
            reduce {
                state.copy(searchMode = true)
            }
        }
    }

    fun exitSearchMode() {
        intent {
            reduce {
                state.copy(searchMode = false)
            }
        }
    }

    private fun showMainBottomSheet(uiModel: BarcodeUiModel = BarcodeUiModel.UNDEFINED) = intent {
        reduce {
            val bsState = BottomSheetUiState.Barcode(BottomSheetStateData(uiModel))
            state.copy(bottomSheetUiState = bsState)
        }
    }

    private fun showSettingsBottomSheet() = intent {
        reduce {
            state.copy(bottomSheetUiState = BottomSheetUiState.Settings)
        }
    }

    private fun showTagsBottomSheet() = intent {
        reduce {
            state.copy(bottomSheetUiState = BottomSheetUiState.Tags)
        }
    }

    private fun hideBottomSheets() = intent {
        reduce {
            state.copy(
                bottomSheetUiState = BottomSheetUiState.None
            )
        }
    }

}
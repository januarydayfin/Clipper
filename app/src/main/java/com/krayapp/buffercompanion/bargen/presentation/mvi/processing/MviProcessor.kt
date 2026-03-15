package com.krayapp.buffercompanion.bargen.presentation.mvi.processing

import android.util.Log
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MviState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.SideEffect
import com.krayapp.buffercompanion.bargen.presentation.mvi.processing.pins.PinHandler
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class MviProcessor(
    viewModel: BargenViewModel,
    private val handler: MviProcessorHandler,
    private val updatePager: () -> Unit,
) : ContainerHost<MviState, SideEffect> {
    override val container = viewModel.container<MviState, SideEffect>(MviState())
    private val pinHandler = PinHandler(this)

    init {
        viewModel.launchInIO {
            pinHandler.init()
        }
        viewModel.launchInIO {
            handler.cardSelectionFlow.collectLatest {
                intent {
                    reduce {
                        state.copy(selectedBarcodesIds = it)
                    }
                }
            }
        }

        viewModel.launchInIO {
            handler.tagSelectionFlow.collectLatest {
                //todo добавить выбор тегов
            }
        }
    }

    suspend fun onIntent(intent: MainIntent) {
        Log.d("FATA", String.format("%s", intent))
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
                if (intent.showAfterCreate && ClipperApp.getPrefs().openCardAfterScan)
                    showMainBottomSheet(createdEntity.toBarcodeUiModel())

                updatePager()
            }

            is MainIntent.CleanCardSelection -> handler.onCleanCardSelection()
            is MainIntent.CleanTagsSelection -> handler.onCleanTagsSelection()
            is MainIntent.DeleteAllSelectedCards -> {
                handler.onDeleteAllSelectedCards()
                updatePager()
            }

            is MainIntent.CheckBarcodeForSelection -> handler.checkBarcodeForSelection(intent.id)
        }
    }

    suspend fun refreshPins() {
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
package com.krayapp.buffercompanion.bargen.presentation.mvi.processing

import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.MviState
import com.krayapp.buffercompanion.bargen.presentation.mvi.main.SideEffect
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.utils.launchInIO
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class MviProcessor(
    viewModel: BargenViewModel,
    private val handler: MviProcessorHandler,
) : ContainerHost<MviState, SideEffect> {
    override val container = viewModel.container<MviState, SideEffect>(MviState())

    val inCardSelectionMode: Boolean get() = container.stateFlow.value.selectedBarcodesIds.isNotEmpty()

    init {
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

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.ShowEmptyMainBottomSheet -> showMainBottomSheet()
            is MainIntent.HideBottomSheet -> hideBottomSheets()
            is MainIntent.ShowExistCodeBottomsheet -> showMainBottomSheet(intent.uiModel)
            is MainIntent.ShowSettingsBottomsheet -> showSettingsBottomSheet()
            is MainIntent.ShowTagsMenu -> showTagsBottomsheet()
            is MainIntent.CreateNewRecord -> handler.createBarcodeRecord(intent)
            is MainIntent.CleanCardSelection -> handler.onCleanCardSelection()
            is MainIntent.CleanTagsSelection -> handler.onCleanTagsSelection()
            is MainIntent.DeleteAllSelectedCards -> handler.onDeleteAllSelectedCards()
            is MainIntent.CheckBarcodeForSelection -> handler.checkBarcodeForSelection(intent.id)
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

}
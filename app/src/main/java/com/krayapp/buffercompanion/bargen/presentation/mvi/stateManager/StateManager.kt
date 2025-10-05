package com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager

import com.krayapp.buffercompanion.bargen.presentation.uiModels.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.mvi.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.Effect
import com.krayapp.buffercompanion.bargen.presentation.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.MviState
import com.krayapp.buffercompanion.bargen.presentation.mvi.ShowTagsBottomsheet
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StateManager(private val scope: CoroutineScope) {
    private val _state = MutableStateFlow(MviState())
    private val remover = EffectRemover
    val state = _state.asStateFlow()

    fun onIntent(intent: MainIntent) {
        scope.launch {
            when (intent) {
                is MainIntent.ShowBottomsheet -> showBottomSheet(intent)
                is MainIntent.CreateNewBarcode -> createNewBarcode()
                is MainIntent.ShowTagsMenu -> showTagsBottomsheet()
            }
        }
    }

    private suspend fun showBottomSheet(intent: MainIntent.ShowBottomsheet) {
        withIO {
            val currentState = _state.value
            val newState =
                currentState.copy(bottomSheetData = BottomSheetStateData(model = intent.uiModel))
            _state.emit(newState)
        }
    }

    private suspend fun showTagsBottomsheet() {
        withIO {
            val currentState = _state.value
            val newState =
                currentState.copy(showTagBottomSheet = ShowTagsBottomsheet(show = true))
            _state.emit(newState)
        }
    }

    private suspend fun createNewBarcode() {
        withIO {
            val currentState = _state.value
            val newState =
                currentState.copy(bottomSheetData = BottomSheetStateData(model = BarcodeUiModel.UNDEFINED))
            _state.emit(newState)
        }
    }

    fun recycleEffect(effect: Effect) {
        scope.launch(Dispatchers.IO) {
            val newState = remover.removeEffect(_state.value, effect)
            _state.emit(newState)
        }

    }

}
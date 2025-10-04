package com.krayapp.buffercompanion.bargen.ui.mvi.stateManager

import com.krayapp.buffercompanion.bargen.ui.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.ui.mvi.MviState
import com.krayapp.buffercompanion.bargen.ui.mvi.PopupShowInfo
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
                is MainIntent.ShowPopup -> updatePopupShowState(intent)
            }
        }
    }

    private suspend fun updatePopupShowState(
        intent: MainIntent.ShowPopup
    ) {
        withIO {
            val newInfo = PopupShowInfo(intent.intOffset, intent.uiModel)
            val currentValue = _state.value

            _state.emit(currentValue.copy(popupShowInfo = newInfo))
        }
    }

    fun recycleEffect(effect: Effect) {
        scope.launch(Dispatchers.IO) {
            val newState = remover.removeEffect(_state.value, effect)
            _state.emit(newState)
        }

    }

}
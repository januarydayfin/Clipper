package com.krayapp.buffercompanion.bargen.ui.mvi.stateManager

import com.krayapp.buffercompanion.bargen.ui.mvi.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.ui.mvi.MviState

object EffectRemover {
    fun removeEffect(state: MviState, effect: Effect): MviState {
        return when (effect) {
            is BottomSheetStateData -> removeBottomsheetEffect(state)
            else -> state
        }
    }

    private fun removeBottomsheetEffect(state: MviState) = state.copy(
        bottomSheetData = null
    )

}
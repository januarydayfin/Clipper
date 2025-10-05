package com.krayapp.buffercompanion.bargen.ui.mvi.stateManager

import com.krayapp.buffercompanion.bargen.ui.mvi.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.ui.mvi.Effect
import com.krayapp.buffercompanion.bargen.ui.mvi.MviState
import com.krayapp.buffercompanion.bargen.ui.mvi.ShowTagsBottomsheet

object EffectRemover {
    fun removeEffect(state: MviState, effect: Effect): MviState {
        return when (effect) {
            is BottomSheetStateData -> removeBottomsheetEffect(state)
            is ShowTagsBottomsheet -> removeTagBottomsheetEffect(state)
        }
    }

    private fun removeBottomsheetEffect(state: MviState) = state.copy(
        bottomSheetData = null
    )
    private fun removeTagBottomsheetEffect(state: MviState) = state.copy(
        showTagBottomSheet = ShowTagsBottomsheet(show = false)
    )

}
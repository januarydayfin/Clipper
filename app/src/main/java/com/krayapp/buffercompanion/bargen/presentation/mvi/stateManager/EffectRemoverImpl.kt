package com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager

import com.krayapp.buffercompanion.bargen.presentation.mvi.BottomSheetStateData
import com.krayapp.buffercompanion.bargen.presentation.mvi.Effect
import com.krayapp.buffercompanion.bargen.presentation.mvi.MviState
import com.krayapp.buffercompanion.bargen.presentation.mvi.ShowSettingsBottomsheet
import com.krayapp.buffercompanion.bargen.presentation.mvi.ShowSortBottomsheet
import com.krayapp.buffercompanion.bargen.presentation.mvi.ShowTagsBottomsheet

class EffectRemoverImpl: EffectRemover {
    override fun removeEffect(state: MviState, effect: Effect): MviState {
        return when (effect) {
            is BottomSheetStateData -> removeBottomsheetEffect(state)
            is ShowTagsBottomsheet -> removeTagBottomsheetEffect(state)
            is ShowSettingsBottomsheet -> removeSettingsBottomsheetEffect(state)
            is ShowSortBottomsheet -> removeSortBottomsheetEffect(state)
        }
    }

    private fun removeBottomsheetEffect(state: MviState) = state.copy(
        bottomSheetData = null
    )

    private fun removeTagBottomsheetEffect(state: MviState) = state.copy(
        showTagBottomSheet = ShowTagsBottomsheet(show = false)

    )

    private fun removeSettingsBottomsheetEffect(state: MviState) = state.copy(
        showSettingsBottomSheet = ShowSettingsBottomsheet(show = false)
    )
    private fun removeSortBottomsheetEffect(state: MviState) = state.copy(
        showSortBottomSheet = ShowSortBottomsheet(show = false)
    )

}
package com.krayapp.buffercompanion.bargen.ui.mvi.stateManager

import com.krayapp.buffercompanion.bargen.ui.mvi.MviState

object EffectRemover {
    fun removeEffect(state: MviState, effect: Effect): MviState {
        return when (effect) {
            Effect.SHOW_POPUP -> removePopupEffect(state)
        }
    }

    private fun removePopupEffect(state: MviState) = state.copy(popupShowInfo = null)

}
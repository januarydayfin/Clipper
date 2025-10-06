package com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager

import com.krayapp.buffercompanion.bargen.presentation.mvi.Effect
import com.krayapp.buffercompanion.bargen.presentation.mvi.MviState

interface EffectRemover {
    fun removeEffect(state: MviState, effect: Effect): MviState
}
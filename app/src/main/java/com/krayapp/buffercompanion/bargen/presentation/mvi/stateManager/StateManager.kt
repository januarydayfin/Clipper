package com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager

import com.krayapp.buffercompanion.bargen.presentation.mvi.Effect
import com.krayapp.buffercompanion.bargen.presentation.mvi.MainIntent
import com.krayapp.buffercompanion.bargen.presentation.mvi.MviState
import kotlinx.coroutines.flow.StateFlow
import java.lang.Thread.State

interface StateManager {
    val state: StateFlow<MviState>
    suspend fun onIntent(intent: MainIntent)
    suspend fun recycleEffect(effect: Effect)
}
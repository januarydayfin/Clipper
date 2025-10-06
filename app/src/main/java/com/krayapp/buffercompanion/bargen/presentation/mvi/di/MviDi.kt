package com.krayapp.buffercompanion.bargen.presentation.mvi.di

import com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager.EffectRemover
import com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager.EffectRemoverImpl
import com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager.StateManager
import com.krayapp.buffercompanion.bargen.presentation.mvi.stateManager.StateManagerImpl
import org.koin.dsl.module

val mviModule = module {
    single<StateManager> {
        StateManagerImpl(get())
    }

    single<EffectRemover> {
        EffectRemoverImpl()
    }
}
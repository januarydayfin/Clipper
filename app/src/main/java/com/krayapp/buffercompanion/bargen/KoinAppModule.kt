package com.krayapp.buffercompanion.bargen

import com.krayapp.buffercompanion.bargen.domain.bargenCore.di.bargenCoreModule
import com.krayapp.buffercompanion.bargen.domain.repository.di.repositoryModule
import com.krayapp.buffercompanion.bargen.domain.selector.di.selectorModule
import com.krayapp.buffercompanion.bargen.presentation.mvi.di.mviModule
import org.koin.dsl.module

val appModule = module {
    includes(selectorModule, bargenCoreModule, repositoryModule, mviModule)
}
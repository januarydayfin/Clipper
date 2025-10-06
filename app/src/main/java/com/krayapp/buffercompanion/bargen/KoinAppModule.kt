package com.krayapp.buffercompanion.bargen

import com.krayapp.buffercompanion.bargen.domain.selector.di.selectorModule
import org.koin.dsl.module

val appModule = module {
    includes(selectorModule)
}
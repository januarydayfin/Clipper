package com.krayapp.buffercompanion.bargen.domain.di

import com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector.CardSelector
import com.krayapp.buffercompanion.bargen.domain.selector.barcodeSelector.CardSelectorImpl
import org.koin.dsl.module

val selectorModule = module {
    single<CardSelector> {
        CardSelectorImpl(get())
    }


}


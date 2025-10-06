package com.krayapp.buffercompanion.bargen.domain.selector.di

import com.krayapp.buffercompanion.bargen.data.BarcodeRepo
import com.krayapp.buffercompanion.bargen.data.TagsRepo
import com.krayapp.buffercompanion.bargen.domain.selector.CardSelector
import com.krayapp.buffercompanion.bargen.domain.selector.CardSelectorImpl
import com.krayapp.buffercompanion.bargen.domain.selector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.selector.TagSelectorImpl
import org.koin.dsl.module

val selectorModule = module {
    single<CardSelector> {
        CardSelectorImpl(get())
    }

    single<TagSelector> {
        TagSelectorImpl()
    }

    single {
        BarcodeRepo()
    }

    single {
        TagsRepo()
    }
}


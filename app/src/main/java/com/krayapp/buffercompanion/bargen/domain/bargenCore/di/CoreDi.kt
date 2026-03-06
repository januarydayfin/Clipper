package com.krayapp.buffercompanion.bargen.domain.bargenCore.di

import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarReader
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BitmapCache
import com.krayapp.buffercompanion.bargen.domain.bargenCore.generator.BarcodeGeneratorImpl
import com.krayapp.buffercompanion.bargen.domain.bargenCore.reader.BargenReaderImpl
import com.krayapp.buffercompanion.bargen.presentation.mvi.processing.MviProcessorHandler
import org.koin.dsl.module

val bargenCoreModule = module {
    factory<BarReader> {
        BargenReaderImpl()
    }

    single<BarGenerator> {
        BarcodeGeneratorImpl()
    }

    single<BitmapCache> {
        BitmapCache()
    }

}
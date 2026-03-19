package com.krayapp.buffercompanion.bargen.domain.di

import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarGenerator
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BarReader
import com.krayapp.buffercompanion.bargen.domain.bargenCore.BitmapCache
import com.krayapp.buffercompanion.bargen.domain.bargenCore.generator.BarcodeGeneratorImpl
import com.krayapp.buffercompanion.bargen.domain.bargenCore.reader.BargenReaderImpl
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.SettingsViewModel
import org.koin.core.module.dsl.viewModel
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

    viewModel {
        BargenViewModel(tagsSelector = get())
    }

    viewModel {
        SettingsViewModel()
    }

}
package com.krayapp.buffercompanion.bargen.domain.di

import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelectorImpl
import com.krayapp.buffercompanion.bargen.domain.usecase.tags.TagsUsecase
import com.krayapp.buffercompanion.bargen.presentation.mvi.tags.TagFounder
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.TagsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val tagsModule = module {
    single<TagSelector> {
        TagSelectorImpl()
    }

    single { TagsUsecase(tagsRepo = get(), barcodeRepo = get()) }
    single { TagFounder(tagsUsecase = get()) }

    viewModel {
        TagsViewModel(tagSelector = get(), tagsUsecase = get())
    }
}

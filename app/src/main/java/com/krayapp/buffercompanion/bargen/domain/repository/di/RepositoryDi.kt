package com.krayapp.buffercompanion.bargen.domain.repository.di

import com.krayapp.buffercompanion.bargen.domain.repository.BarcodeRepo
import com.krayapp.buffercompanion.bargen.domain.repository.BarcodeRepoImpl
import com.krayapp.buffercompanion.bargen.domain.repository.TagsRepo
import com.krayapp.buffercompanion.bargen.domain.repository.TagsRepoImpl
import org.koin.dsl.module

val repositoryModule = module { 
    single<BarcodeRepo> {
        BarcodeRepoImpl()
    }
    
    single<TagsRepo> { 
        TagsRepoImpl()
    }
}
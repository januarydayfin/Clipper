package com.krayapp.buffercompanion.bargen.data.room.repository.di

import com.krayapp.buffercompanion.bargen.data.room.repository.BarcodeRepo
import com.krayapp.buffercompanion.bargen.data.room.repository.BarcodeRepoImpl
import com.krayapp.buffercompanion.bargen.data.room.repository.TagsRepo
import com.krayapp.buffercompanion.bargen.data.room.repository.TagsRepoImpl
import org.koin.dsl.module

val repositoryModule = module { 
    single<BarcodeRepo> {
        BarcodeRepoImpl()
    }
    
    single<TagsRepo> { 
        TagsRepoImpl()
    }
}
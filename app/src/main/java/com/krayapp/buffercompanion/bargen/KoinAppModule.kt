package com.krayapp.buffercompanion.bargen

import com.krayapp.buffercompanion.bargen.data.room.di.repositoryModule
import com.krayapp.buffercompanion.bargen.domain.di.bargenCoreModule
import com.krayapp.buffercompanion.bargen.domain.di.selectorModule
import com.krayapp.buffercompanion.bargen.domain.di.tagsModule
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    includes(selectorModule, bargenCoreModule, repositoryModule, tagsModule)
    single { GlobalPrefs(androidContext()) }
}

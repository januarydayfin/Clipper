package com.krayapp.buffercompanion.bargen.presentation.utils

import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.domain.type.SortType

val currentSortType
    get() = SortType.valueOf(ClipperApp.getPrefs().sortType)
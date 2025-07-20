package com.krayapp.buffercompanion.bargen.utils

import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.data.SearchType
import com.krayapp.buffercompanion.bargen.data.SortType

val currentSearchType
    get() = SearchType.valueOf(ClipperApp.getPrefs().searchType)

val currentBarcodeFormat
    get() = BarcodeFormat.valueOf(ClipperApp.getPrefs().lastBarFormat)

val currentSortType
    get() = SortType.valueOf(ClipperApp.getPrefs().sortType)
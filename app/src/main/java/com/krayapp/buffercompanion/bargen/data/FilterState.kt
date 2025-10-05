package com.krayapp.buffercompanion.bargen.data

data class FilterState(
    val tagIds: List<String> = emptyList(),
    val searchFilter: String = "",
    val manualUpdate: String = "", //поле для обновления pager
)

package com.krayapp.buffercompanion.bargen.data

data class FilterState(
    val tagIds: List<String> = emptyList(),
    val searchFilter: String? = null,
)

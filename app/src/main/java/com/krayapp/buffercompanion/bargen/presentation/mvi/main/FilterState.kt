package com.krayapp.buffercompanion.bargen.presentation.mvi.main

data class FilterState(
    val tagIds: List<String> = emptyList(),
    val searchFilter: String = "",
)

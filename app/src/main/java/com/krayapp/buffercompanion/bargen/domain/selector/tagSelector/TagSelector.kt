package com.krayapp.buffercompanion.bargen.domain.selector.tagSelector

import kotlinx.coroutines.flow.StateFlow

interface TagSelector {
    val tagsFilterFlow: StateFlow<List<String>>
    suspend fun checkTag(id: String)

    suspend fun forceUncheck(id: String)
}
package com.krayapp.buffercompanion.bargen.domain.selector

import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import kotlinx.coroutines.flow.StateFlow

interface TagSelector {
    val tagFilterFlow: StateFlow<List<String>>

    suspend fun checkTag(id: String)

    suspend fun uncheckTag(id: String)
}
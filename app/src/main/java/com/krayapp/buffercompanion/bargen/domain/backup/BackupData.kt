package com.krayapp.buffercompanion.bargen.domain.backup

import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.data.room.entity.TagEntity
import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val barcodes: List<BarcodeEntity>,
    val tags: List<TagEntity>,
)
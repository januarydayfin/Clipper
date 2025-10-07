package com.krayapp.buffercompanion.bargen.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.krayapp.buffercompanion.bargen.data.room.bargen.converter.StringListConverter
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Entity("barcodes")
@Serializable
@TypeConverters(StringListConverter::class)
data class BarcodeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val content: String,
    val description: String = "",
    val usageCount: Int = 0,
    val modificationTime: Long = Date().time,
    val name: String = "",
    val type: String,
    val tags: List<String> = emptyList()
)


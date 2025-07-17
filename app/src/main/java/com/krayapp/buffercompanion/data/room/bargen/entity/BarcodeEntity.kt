package com.krayapp.buffercompanion.data.room.bargen.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.krayapp.buffercompanion.data.room.bargen.converter.StringListConverter

@Entity("barcodes")
@TypeConverters(StringListConverter::class)
data class BarcodeEntity(
    @PrimaryKey val id: String,
    val content: String,
    val description: String,
    val tags: List<String>
)


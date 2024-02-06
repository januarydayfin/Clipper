package com.krayapp.buffercompanion.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buffered")
data class StringEntity(
    @PrimaryKey val text: String
)
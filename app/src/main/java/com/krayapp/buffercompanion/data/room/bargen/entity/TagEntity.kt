package com.krayapp.buffercompanion.data.room.bargen.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tags")
class TagEntity(
    @PrimaryKey val id: String,
    val name: String,
    val color: Int,
    val textLight: Boolean,
)
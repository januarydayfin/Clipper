package com.krayapp.buffercompanion.bargen.data.room.bargen.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tags")
class TagEntity(
    @PrimaryKey val id: String,
    val name: String,
    val backgroundColor: Int,
    val fontColor: Int,
)
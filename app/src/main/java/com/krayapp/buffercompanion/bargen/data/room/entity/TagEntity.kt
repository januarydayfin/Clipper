package com.krayapp.buffercompanion.bargen.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity("tags")
@Serializable
class TagEntity(
    @PrimaryKey val id: String,
    val name: String,
    val backgroundColor: Int? = null,
    val fontColor: Int? = null,
)
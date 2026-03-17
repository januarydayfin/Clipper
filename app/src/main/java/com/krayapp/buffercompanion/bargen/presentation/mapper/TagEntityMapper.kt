package com.krayapp.buffercompanion.bargen.presentation.mapper

import com.krayapp.buffercompanion.bargen.data.room.entity.TagEntity
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel

fun TagEntity.toTagUiModel() =
    TagUiModel(
        id = this.id,
        backgroundColor = this.backgroundColor,
        fontColor = fontColor,
        name = this.name
    )

fun TagUiModel.toEntity() =
    TagEntity(
        id = this.id,
        name = this.name,
        backgroundColor = this.backgroundColor,
        fontColor = this.fontColor
    )
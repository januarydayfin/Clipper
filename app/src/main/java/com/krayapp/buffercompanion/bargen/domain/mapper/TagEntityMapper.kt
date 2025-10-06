package com.krayapp.buffercompanion.bargen.domain.mapper

import com.krayapp.buffercompanion.bargen.data.room.bargen.entity.TagEntity
import com.krayapp.buffercompanion.bargen.presentation.uiModels.TagUiModel

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
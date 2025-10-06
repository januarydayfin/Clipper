package com.krayapp.buffercompanion.bargen.presentation.models

import java.util.UUID

data class TagUiModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val backgroundColor: Int? = null,
    val fontColor: Int? = null,
    val checked: Boolean = false
)

fun TagUiModel.setChecked(): TagUiModel = copy(checked = true)
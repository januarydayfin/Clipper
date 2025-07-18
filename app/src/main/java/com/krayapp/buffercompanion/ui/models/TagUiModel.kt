package com.krayapp.buffercompanion.ui.models

import android.graphics.Color

data class TagUiModel(
    val id: String,
    val name: String,
    val backgroundColor: Color,
    val fontColor: Color,
    val checked: Boolean = false
)

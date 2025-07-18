package com.krayapp.buffercompanion.bargen.ui.models

import android.graphics.Color

data class TagUiModel(
    val id: String,
    val name: String,
    val backgroundColor: Int,
    val fontColor: Int,
    val checked: Boolean = false
)

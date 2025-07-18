package com.krayapp.buffercompanion.bargen.ui.models

import android.graphics.Bitmap

data class BarcodeUiModel(
    val id: String,
    val name: String,
    val description: String?,
    val image: Bitmap?,
    val tags: List<TagUiModel>,
    val selectionMode : Boolean = false,
    val checkedForDeletion: Boolean = false
)
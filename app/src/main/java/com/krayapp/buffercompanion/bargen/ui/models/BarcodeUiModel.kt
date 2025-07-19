package com.krayapp.buffercompanion.bargen.ui.models

data class BarcodeUiModel(
    val id: String,
    val name: String,
    val barcodeType: String,
    val description: String?,
    val tags: List<TagUiModel>,
    val content: String,
    val selectionMode: Boolean = false,
    val checkedForDeletion: Boolean = false
)
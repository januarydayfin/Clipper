package com.krayapp.buffercompanion.bargen.presentation.uiModels

import java.util.UUID

data class BarcodeUiModel(
    val id: String,
    val name: String,
    val barcodeType: String,
    val description: String,
    val tags: List<TagUiModel>,
    val content: String,
    val selectionMode: Boolean = false,
    val checkedForDeletion: Boolean = false
) {
    companion object {
        val UNDEFINED
            get() = BarcodeUiModel(
                id = UUID.randomUUID().toString(),
                name = "",
                barcodeType = "QR_CODE",
                tags = emptyList(),
                description = "",
                content = ""
            )
    }
}


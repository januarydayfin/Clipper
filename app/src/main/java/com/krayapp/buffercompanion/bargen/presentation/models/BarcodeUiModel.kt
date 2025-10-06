package com.krayapp.buffercompanion.bargen.presentation.models

import java.util.UUID

data class BarcodeUiModel(
    val id: String,
    val name: String,
    val barcodeType: String,
    val description: String,
    val tags: List<TagUiModel>,
    val content: String,
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


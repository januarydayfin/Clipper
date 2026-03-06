package com.krayapp.buffercompanion.bargen.presentation.models

import java.util.UUID

data class BarcodeUiModel(
    val id: String,
    val name: String,
    val barcodeType: String,
    val description: String,
    val tags: List<TagUiModel>,
    val content: String,
    val pinOrder: Int,
) {
    val isPinned: Boolean
        get() = pinOrder != NOT_PINNED

    companion object {
        const val NOT_PINNED = -1
        val UNDEFINED
            get() = BarcodeUiModel(
                id = UUID.randomUUID().toString(),
                name = "",
                barcodeType = "QR_CODE",
                tags = emptyList(),
                description = "",
                content = "",
                pinOrder = NOT_PINNED
            )
    }
}


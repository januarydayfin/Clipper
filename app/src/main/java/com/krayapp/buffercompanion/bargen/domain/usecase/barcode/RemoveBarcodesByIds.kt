package com.krayapp.buffercompanion.bargen.domain.usecase.barcode

import com.krayapp.buffercompanion.bargen.data.room.repository.BarcodeRepo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object RemoveBarcodesByIds : KoinComponent {
    private val barcodeRepo: BarcodeRepo by inject()

    suspend operator fun invoke(ids: List<String>) {
        barcodeRepo.removeBarcodesByIds(ids)
    }
}
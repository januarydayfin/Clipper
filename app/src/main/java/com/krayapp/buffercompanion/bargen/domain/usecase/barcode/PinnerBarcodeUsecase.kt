package com.krayapp.buffercompanion.bargen.domain.usecase.barcode

import com.krayapp.buffercompanion.bargen.domain.repository.BarcodeRepo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

object PinnerBarcodeUsecase : KoinComponent {
    private val barcodeRepo: BarcodeRepo by inject()

    suspend fun pinBarcode(id: String, position: Int) =
        barcodeRepo.pinBarcode(id = id, position = position)

    suspend fun unpinBarcode(id: String) = barcodeRepo.unpinBarcode(id)

    suspend fun getPinnedBarcodes() = barcodeRepo.getPinnedBarcodes()

}
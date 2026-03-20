package com.krayapp.buffercompanion.bargen.domain.usecase.barcode

import com.krayapp.buffercompanion.bargen.data.room.repository.BarcodeRepo
import com.krayapp.buffercompanion.bargen.domain.type.SortType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

object GetAllBarcodesPaging : KoinComponent {
    private val barcodeRepo: BarcodeRepo by inject()
    operator fun invoke(sort: SortType) = barcodeRepo.getAllBarcodesPaging(sort)
}
package com.krayapp.buffercompanion.bargen.domain.usecase.barcode

import com.krayapp.buffercompanion.bargen.domain.repository.BarcodeRepo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object GetFilteredByNamePagingSource : KoinComponent {
    private val barcodeRepo: BarcodeRepo by inject()
    operator fun invoke(filter: String) = barcodeRepo.getFilteredBarcodesByNamePaging(filter)
}
package com.krayapp.buffercompanion.bargen.domain.usecase.barcode

import com.krayapp.buffercompanion.bargen.data.room.repository.BarcodeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

object CheckDataExistUsecase : KoinComponent {
    private val barcodeRepo: BarcodeRepo by inject()
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        barcodeRepo.recordsCount() != 0
    }
}
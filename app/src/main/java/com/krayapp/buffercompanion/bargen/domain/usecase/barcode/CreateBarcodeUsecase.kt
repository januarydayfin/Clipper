package com.krayapp.buffercompanion.bargen.domain.usecase.barcode

import com.krayapp.buffercompanion.bargen.data.room.entity.BarcodeEntity
import com.krayapp.buffercompanion.bargen.domain.repository.BarcodeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CreateBarcodeUsecase : KoinComponent {
    private val barcodeRepo: BarcodeRepo by inject()

    suspend operator fun invoke(text: String, format: String, tagIds: List<String>) =
        withContext(Dispatchers.IO) {
            val entity = BarcodeEntity(
                content = text,
                type = format,
                tags = tagIds
            )
            barcodeRepo.upsertBarcode(entity)
            entity
        }

    suspend operator fun invoke(entity: BarcodeEntity) =
        withContext(Dispatchers.IO) {
            barcodeRepo.upsertBarcode(entity)
            entity
        }
}
package com.krayapp.buffercompanion.bargen.domain.backup

import com.krayapp.buffercompanion.bargen.data.room.BargenDB
import com.krayapp.buffercompanion.bargen.domain.provideDatabase
import com.krayapp.buffercompanion.bargen.utils.withIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

suspend fun convertAllDbToJson() =
    withContext(Dispatchers.IO) {
        val db = provideDatabase<BargenDB>()
        val barcodes = db.barcodeDao().getAll()
        val tags = db.tagsDao().getTags()


        return@withContext Json.Default.encodeToString(
            BackupData(
                barcodes, tags
            )
        )
    }

suspend fun applyBackupToDatabase(json: String) {
    withIO {
        val db = provideDatabase<BargenDB>()
        val barcodes = db.barcodeDao()
        val tags = db.tagsDao()

        runCatching {
            val data = Json.Default.decodeFromString<BackupData>(json)

            barcodes.clean()
            tags.clean()

            tags.upsertTags(data.tags)
            barcodes.upsertBarcodes(data.barcodes)
        }
    }
}
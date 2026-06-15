package com.krayapp.buffercompanion.bargen.presentation.utils

import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.krayapp.buffercompanion.bargen.ClipperApp
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

object ContentFromUriImage {
    suspend operator fun invoke(uri: Uri): Pair<String, String>? =
        suspendCancellableCoroutine { continuation ->
            val image = try {
                InputImage.fromFilePath(ClipperApp.getApplication(), uri)
            } catch (e: Exception) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            BarcodeScanning.getClient().process(image)
                .addOnSuccessListener { barcodes ->
                    val barcode = barcodes.firstOrNull()
                    if (barcode == null) {
                        continuation.resume(null)
                        return@addOnSuccessListener
                    }
                    val rawValue = barcode.rawValue ?: run {
                        continuation.resume(null)
                        return@addOnSuccessListener
                    }
                    val formatName = barcode.format.toZxingFormatName() ?: run {
                        continuation.resume(null)
                        return@addOnSuccessListener
                    }
                    continuation.resume(Pair(rawValue, formatName))
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }

    private fun Int.toZxingFormatName(): String? = when (this) {
        Barcode.FORMAT_QR_CODE -> "QR_CODE"
        Barcode.FORMAT_EAN_13 -> "EAN_13"
        Barcode.FORMAT_EAN_8 -> "EAN_8"
        Barcode.FORMAT_UPC_A -> "UPC_A"
        Barcode.FORMAT_UPC_E -> "UPC_E"
        Barcode.FORMAT_CODE_128 -> "CODE_128"
        Barcode.FORMAT_CODE_39 -> "CODE_39"
        Barcode.FORMAT_CODE_93 -> "CODE_93"
        Barcode.FORMAT_ITF -> "ITF"
        Barcode.FORMAT_PDF417 -> "PDF_417"
        Barcode.FORMAT_DATA_MATRIX -> "DATA_MATRIX"
        Barcode.FORMAT_AZTEC -> "AZTEC"
        else -> null
    }
}

package com.krayapp.buffercompanion.bargen.presentation.backup

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun Context.writeToFile(data: String, uri: Uri, onError: () -> Unit) {
    val tempFileName = "tempFileName"
    val file = File(cacheDir, tempFileName)

    runCatching {
        file.writeText(data)
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            FileInputStream(file).use { inputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        file.delete()
    }.onFailure {
        onError()
    }
}

suspend fun Context.readFromFile(
    uri: Uri,
    onSuccess: suspend (String) -> Unit,
    onError: () -> Unit
) {
    runCatching {
        val tempFileName = "tempFileName"
        val file = File(cacheDir, tempFileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        onSuccess(file.readText())

        file.delete()
    }.onFailure {
        onError()
    }
}

val bargen_backup_filename = "bargen_backup_${System.currentTimeMillis()}.json"
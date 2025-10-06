package com.krayapp.buffercompanion.bargen.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.data.room.bargen.BargenDB.Companion.DB_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

suspend fun exportDatabaseToUri(context: Context, outputUri: Uri, dbName: String = DB_NAME) =
    withContext(
        Dispatchers.IO
    ) {
        runCatching {
            val sourceFile = context.getDatabasePath(dbName)
            if (!sourceFile.exists()) {
                throw FileNotFoundException("Файл БД не найден: ${sourceFile.path}")
            }

            context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            println("База данных успешно выгружена в $outputUri")

        }.onFailure {
            it.printStackTrace()
            println("Ошибка при выгрузке базы данных: ${it.message}")
        }
    }

suspend fun Activity.restoreDatabaseFromUri(sourceUri: Uri, dbName: String = DB_NAME) =
    withContext(Dispatchers.IO) {
        val context = this@restoreDatabaseFromUri
        runCatching {
            val targetFile = context.getDatabasePath(dbName)
            context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            File(targetFile.path + "-wal").delete()
            File(targetFile.path + "-shm").delete()
            println("База данных успешно заменена на файл из URI: $sourceUri")
            withMain {
                Toast.makeText(
                    this@restoreDatabaseFromUri,
                    R.string.app_will_close,
                    Toast.LENGTH_SHORT
                ).show()
            }
            launchWithDelay(1000) {
                finish()
            }

        }.onFailure {
            it.printStackTrace()
            println("Ошибка при восстановлении базы данных: ${it.message}")
        }

    }

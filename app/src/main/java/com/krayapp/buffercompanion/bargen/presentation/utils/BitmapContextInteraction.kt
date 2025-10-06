package com.krayapp.buffercompanion.bargen.presentation.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

fun Context.savePictureInStorage(bmp: Bitmap?, filename: String, onSaved: (String) -> Unit) {
    val contentResolver = contentResolver
    val collection: Uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Bargen")
    }

    var imageUri: Uri? = null
    var outputStream: OutputStream? = null

    try {
        imageUri = contentResolver.insert(collection, contentValues) ?: return
        outputStream = contentResolver.openOutputStream(imageUri) ?: return
        bmp?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) ?: return
        outputStream.flush()
        onSaved(filename)
    } catch (e: IOException) {
        e.printStackTrace()
        if (imageUri != null) {
            contentResolver.delete(imageUri, null, null)
        }
    } finally {
        outputStream?.close()
    }
}

fun Context.shareBitmap(bitmap: Bitmap?, title: String) {
    val cachePath = File(cacheDir, "images")
    cachePath.mkdirs()

    val file = File(cachePath, "$title.png")
    try {
        val stream = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream) ?: return
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
        return
    }

    // 2. Получаем Uri для файла с помощью FileProvider
    val fileUri: Uri? = try {
        FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }

    if (fileUri == null) {
        return
    }

    // 3. Создаем и запускаем Intent для обмена
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Даем разрешение на чтение Uri
    }

    val chooserIntent = Intent.createChooser(shareIntent, title)

    // Проверяем, есть ли приложения, которые могут обработать этот Intent
    if (chooserIntent.resolveActivity(packageManager) != null) {
        startActivity(chooserIntent)
    }
}

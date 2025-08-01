package com.krayapp.buffercompanion.bargen.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.widget.FrameLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


val decodedSize = ClipperApp.displayWidth to ClipperApp.displayWidth / 2

val dialogWidth = (ClipperApp.displayWidth * 0.9).toInt()

fun DialogFragment.setupDialogWidth() {
    view?.layoutParams = FrameLayout.LayoutParams(dialogWidth, -1).apply {
        gravity = Gravity.CENTER
    }
}

fun Context.filterChip(tagUiModel: TagUiModel, canChecked: Boolean = true): Chip {
    val drawable = ChipDrawable.createFromAttributes(this, null, 0, filterChipStyle)


    return Chip(this).apply {
        val backgroundColor = tagUiModel.backgroundColor.toColorStateList()
        val fontColor = tagUiModel.fontColor.toColorStateList()

        setChipDrawable(drawable)
        text = tagUiModel.name
        chipBackgroundColor = backgroundColor

        if (fontColor != null)
            setTextColor(fontColor)

        ensureAccessibleTouchTarget(15)
        checkedIconTint = fontColor
        isCheckable = canChecked
        isClickable = canChecked
        isFocusable = canChecked
        chipStrokeColor = backgroundColor
        isChecked = tagUiModel.checked
        tag = tagUiModel
    }
}

private fun Int?.toColorStateList() =
    if (this == null)
        null
    else {
        ColorStateList.valueOf(this)
    }

val filterChipStyle: Int
    get() = com.google.android.material.R.style.Widget_Material3_Chip_Filter


fun Context.showDeleteConfirmationDialog(onDelete: () -> Unit) {
    MaterialAlertDialogBuilder(this)
        .setTitle(R.string.delete)
        .setMessage(R.string.are_you_sure)
        .setPositiveButton(R.string.delete) { dialog, _ ->
            onDelete()
            dialog.dismiss()
        }
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

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

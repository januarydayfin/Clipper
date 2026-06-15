package com.krayapp.buffercompanion.bargen.presentation.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

/**
 * Создаёт и запоминает лаунчер системного пикера изображений (Photo Picker).
 *
 * Возвращает функцию-триггер: вызов открывает системный UI выбора изображения.
 * [onImageSelected] вызывается с выбранным URI, или с `null` если пользователь отменил выбор.
 */
@Composable
fun rememberImagePicker(onImageSelected: (Uri?) -> Unit): () -> Unit {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onImageSelected(uri)
    }

    return {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
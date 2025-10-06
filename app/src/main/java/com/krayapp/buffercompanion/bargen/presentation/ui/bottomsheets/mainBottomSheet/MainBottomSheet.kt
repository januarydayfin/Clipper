package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.mapper.toBarcodeEntity
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.BarcodeFormatDialog
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.TagsViewModel
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomSheet(
    model: BarcodeUiModel,
    onDismiss: () -> Unit = {},
    onSharePicture: (Bitmap?) -> Unit = {},
    onSaveStoragePicture: (Bitmap?) -> Unit = {},
) {
    val viewmodel: BargenViewModel = viewModel()
    val tagsViewModel: TagsViewModel = viewModel()
    val sheetState = rememberModalBottomSheetState()
    val modelState = remember { mutableStateOf(model) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val formatDialogOpened = remember { mutableStateOf(false) }
    if (formatDialogOpened.value)
        BarcodeFormatDialog(
            initialState = modelState.value.barcodeType,
            onPicked = {
                modelState.value = modelState.value.copy(barcodeType = it)
            }, onDismiss = {
                formatDialogOpened.value = false
            })

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()
        }) {

        Row(Modifier.padding(horizontal = mSize)) {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(text = stringResource(R.string.cancel))
            }
            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = {
                scope.launch {
                    viewmodel.createBarcodeRecord(modelState.value.toBarcodeEntity())
                    tagsViewModel.saveTags(modelState.value.tags)
                    onDismiss()
                }

            }) { Text(text = stringResource(R.string.apply)) }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = lSize)
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageBlock(
                state = modelState,
                onSharePicture = onSharePicture,
                onSaveStoragePicture = onSaveStoragePicture
            )
            BarcodeFormatBlock(modelState) {
                formatDialogOpened.value = true
            }
            StringedInfo(modelState) {
                modelState.value = it
            }
            TagBlock(
                initialValue = model.tags,
                viewModel = viewModel(),
                onScrollToBottom = {
                    scope.launch {
                        scrollState.scrollTo(scrollState.maxValue)
                    }
                },
                onTagsAdded = { tags ->
                    modelState.value = modelState.value.copy(tags = tags)
                })
        }

    }
}

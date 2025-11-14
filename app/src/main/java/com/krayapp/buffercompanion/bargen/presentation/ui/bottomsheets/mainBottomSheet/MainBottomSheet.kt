package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.SheetDragger
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.BarcodeFormatDialog
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainBottomSheet(
    model: BarcodeUiModel,
    onDismiss: () -> Unit = {},
    onSharePicture: (Bitmap?) -> Unit = {},
    onSaveStoragePicture: (Bitmap?) -> Unit = {},
    onApplyBarcode: (BarcodeUiModel) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()
    val modelState = remember { mutableStateOf(model) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val formatDialogOpened = remember { mutableStateOf(false) }
    val isKeyboardVisible by rememberUpdatedState(WindowInsets.isImeVisible)


    SideEffect {
        if (isKeyboardVisible)
            scope.launch {
                sheetState.expand()
            }
    }

    if (formatDialogOpened.value)
        BarcodeFormatDialog(
            initialState = modelState.value.barcodeType,
            onPicked = {
                modelState.value = modelState.value.copy(barcodeType = it)
            }, onDismiss = {
                formatDialogOpened.value = false
            })

    ModalBottomSheet(
        dragHandle = {
            SheetDragger()
        },
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
                onApplyBarcode(modelState.value)
                onDismiss()

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

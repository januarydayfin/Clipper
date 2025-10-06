package com.krayapp.buffercompanion.bargen.presentation.bottomsheets

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.zxing.BarcodeFormat
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.bargenCore.generator.BarcodeGenerator
import com.krayapp.buffercompanion.bargen.presentation.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.dialogs.BarcodeFormatDialog
import com.krayapp.buffercompanion.bargen.presentation.uiModels.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.uiModels.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.TagsViewModel
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.utils.toBarcodeEntity
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
        val keyboard = LocalSoftwareKeyboardController.current

        fun dismissBottomSheet() {
            scope.launch {
                keyboard?.hide()
                sheetState.hide()
            }
        }

        Row {
            TextButton(onClick = {
                dismissBottomSheet()
            }) {
                Text(text = stringResource(R.string.cancel))
            }
            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = {
                scope.launch {
                    viewmodel.createBarcodeRecord(modelState.value.toBarcodeEntity())
                    tagsViewModel.saveTags(modelState.value.tags)
                    dismissBottomSheet()
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun BarcodeFormatBlock(state: State<BarcodeUiModel>, openDialog: () -> Unit) {
    val model = state.value
    SplitButtonLayout(leadingButton = {
        SplitButtonDefaults.LeadingButton(onClick = { openDialog() }) {
            Text(text = model.barcodeType)
        }
    }, trailingButton = {
        SplitButtonDefaults.TrailingButton(onClick = {
            openDialog()
        }) {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_arrow_down),
                modifier =
                    Modifier.size(SplitButtonDefaults.TrailingIconSize),
                contentDescription = "Localized description",
            )
        }
    })
}

@Composable
private fun ImageBlock(
    state: State<BarcodeUiModel>,
    onSharePicture: (Bitmap?) -> Unit = {},
    onSaveStoragePicture: (Bitmap?) -> Unit = {},
) {
    val model = state.value
    val scope = rememberCoroutineScope()
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    val bmp = bitmapState.value
    val generateBitmap: () -> Unit = {
        scope.launch {
            bitmapState.value = BarcodeGenerator.generate(
                model.content,
                BarcodeFormat.valueOf(model.barcodeType)
            )
        }
    }
    SideEffect {
        generateBitmap()
    }

    if (bmp != null) {
        Image(bitmap = bmp.asImageBitmap(), contentDescription = "image")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedIconButton(
                onClick = { onSaveStoragePicture(bmp) },
                textRes = R.string.save,
                iconRes = R.drawable.ic_download
            )

            Spacer(Modifier.width(mSize))

            OutlinedIconButton(
                onClick = { onSharePicture(bmp) },
                textRes = R.string.share,
                iconRes = R.drawable.ic_share
            )
        }
    }
}

@Composable
private fun StringedInfo(
    state: State<BarcodeUiModel>,
    onUpdate: (BarcodeUiModel) -> Unit = { }
) {
    val model = state.value

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        value = model.content,
        modifier = Modifier.fillMaxWidth(),
        onValueChange = {
            onUpdate(model.copy(content = it))
        },
        maxLines = 2,
        label = {
            Text(text = stringResource(R.string.content))
        })

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
        value = model.name,
        onValueChange = {
            onUpdate(model.copy(name = it))
        },
        label = {
            Text(text = stringResource(R.string.name))
        })

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
        value = model.description ?: "",
        onValueChange = {
            onUpdate(model.copy(description = it))
        },
        label = {
            Text(text = stringResource(R.string.description))
        })
}

@Composable
private fun TagBlock(
    initialValue: List<TagUiModel> = emptyList(),
    viewModel: BargenViewModel,
    onScrollToBottom: () -> Unit = {},
    onTagsAdded: (List<TagUiModel>) -> Unit
) {
    val tagsTextFieldState =
        rememberTextFieldState(initialText = initialValue.joinToString { it.name })
    val scope = rememberCoroutineScope()
    val newTags = mutableListOf<TagUiModel>().toMutableStateList()
    val previewTags = mutableListOf<TagUiModel>().toMutableStateList()

    val nameList = tagsTextFieldState.text.splitRawTagsForNames()

    val flowRowAnimate = Modifier.animateContentSize(
        animationSpec = tween(
            durationMillis = 200,
            delayMillis = 50
        )
    ) { _, _ -> }

    suspend fun foundTagsInDb(name: String) = viewModel.findTagWithName(name)


    SideEffect {
        scope.launch {
            nameList.forEach { stringName ->
                if (stringName.isNotEmpty()) {
                    val foundTags = foundTagsInDb(stringName)
                    val exactTag =
                        runCatching { foundTags.first { stringName == it.name } }.getOrNull()
                    newTags.add(exactTag ?: TagUiModel(name = stringName))
                    previewTags.addAll(foundTags.filter { it !in newTags })
                    onTagsAdded(newTags.toList())
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            state = tagsTextFieldState,
            label = {
                Text(text = stringResource(R.string.tags))
            })

        //добавляемые теги
        FlowRow(
            horizontalArrangement = Arrangement.Start,
            modifier = flowRowAnimate
        ) {
            newTags.toList().forEach {
                BargenChip(it)
            }
        }

        Text(
            text = stringResource(R.string.found_tags),
            style = MaterialTheme.typography.labelMedium
        )
        //превью
        FlowRow(
            modifier = flowRowAnimate,
            horizontalArrangement = Arrangement.Start,
        ) {
            previewTags.toList().distinct().forEach {
                BargenChip(onClick = {
                    val prevName = it.name
                    val substringed =
                        tagsTextFieldState.text.toString().removeSuffix(nameList.last())
                    tagsTextFieldState.setTextAndPlaceCursorAtEnd(substringed + prevName)
                }, model = it)
            }
        }
        onScrollToBottom()
    }
}

@Composable
private fun OutlinedIconButton(onClick: () -> Unit, textRes: Int, iconRes: Int) {
    OutlinedButton(onClick = {
        onClick()
    }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(end = 4.dp),
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = stringResource(textRes),
            )
            Text(
                text = stringResource(textRes),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun CharSequence.splitRawTagsForNames() = this.toString().split(",").map { it.trim() }
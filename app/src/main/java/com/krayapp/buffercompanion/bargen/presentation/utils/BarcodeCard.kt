package com.krayapp.buffercompanion.bargen.presentation.utils

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.ScanOptions.DATA_MATRIX
import com.journeyapps.barcodescanner.ScanOptions.PDF_417
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.CardPinner
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.ConfirmationDialog
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.theme.barcodePreviewSize
import com.krayapp.buffercompanion.bargen.theme.cardColorsSelector
import com.krayapp.buffercompanion.bargen.theme.keepPinSize
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.theme.xsSize
import kotlinx.coroutines.launch

@Composable
fun BarcodeCard(
    modifier: Modifier = Modifier,
    uiModel: BarcodeUiModel,
    inSelectionMode: Boolean,
    isCheckedForDeletion: Boolean,
    onDeleteClicked: (String) -> Unit = {},
    onCardClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onPin: () -> Unit = {},
    onUnpin: () -> Unit = {},
    reorderModifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState()
    val deleteDialogShowState = remember { mutableStateOf("") }

    if (deleteDialogShowState.value.isNotEmpty()) {
        ConfirmationDialog(onDismiss = {
            deleteDialogShowState.value = ""
        }) {
            onDeleteClicked(deleteDialogShowState.value)
        }
    }
    SwipeToDismissBox(
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        state = dismissState,
        onDismiss = {
            scope.launch {
                dismissState.reset()
            }
            deleteDialogShowState.value = uiModel.id
        },
        backgroundContent = {
            RemoveCardBackground()
        }) {
        Card(
            shape = RoundedCornerShape(size = lSize),
            colors = cardColorsSelector(model = uiModel, isCheckedForDelete = isCheckedForDeletion),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .pointerInput(inSelectionMode) {
                    detectTapGestures(
                        onTap = {
                            if (inSelectionMode)
                                onSelectClick()
                            else
                                onCardClick()
                        },
                        onLongPress = if (inSelectionMode) null else {
                            {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSelectClick()
                            }
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(sSize),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BarcodeInfo(
                    barcodePreviewRes =
                        uiModel.barcodeType.getPreviewDrawableFromType(),
                    barcodeTypeText = uiModel.barcodeType
                )

                Space(width = sSize)
                ContentInfo(
                    modifier = Modifier
                        .weight(1f),
                    name = uiModel.name,
                    content = uiModel.content,
                    tags = uiModel.tags
                )

                if (uiModel.isPinned && !inSelectionMode)
                    CardPinner(modifier = Modifier.size(keepPinSize), pinned = !uiModel.isPinned)

                if (inSelectionMode) {
                    CardPinner(
                        modifier = Modifier.size(keepPinSize),
                        pinned = uiModel.isPinned,
                        onClick = {
                            if (uiModel.isPinned)
                                onUnpin()
                            else
                                onPin()
                        })

                    if (uiModel.isPinned) {
                        Space(sSize)
                        Icon(
                            modifier = Modifier
                                .size(keepPinSize)
                                .then(reorderModifier),
                            painter = painterResource(R.drawable.menu),
                            contentDescription = null,
                            tint = CardDefaults.cardColors().contentColor
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun RemoveCardBackground() {
    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor =
                MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = xsSize)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = mSize),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )

                Text(
                    text = stringResource(R.string.delete),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun BarcodeInfo(
    barcodePreviewRes: Int = R.drawable.pdf417_example,
    barcodeTypeText: String = "pdf"
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(barcodePreviewRes),
            modifier = Modifier.size(barcodePreviewSize),
            contentDescription = "barcode_preview",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Text(text = barcodeTypeText, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ContentInfo(
    modifier: Modifier = Modifier,
    content: String = "content",
    name: String = "name",
    tags: List<TagUiModel>
) {
    Column(modifier = modifier) {
        val textStyle = MaterialTheme.typography.titleMedium
        Text(text = content, style = textStyle, maxLines = 2)
        Text(text = name, style = textStyle, maxLines = 2)

        FlowRow(Modifier.fillMaxWidth()) {
            tags.forEach { BargenChip(model = it, selectable = false) }
        }
    }
}

private fun String.getPreviewDrawableFromType() =
    when (this) {
        PDF_417 -> R.drawable.pdf417_example
        BarcodeFormat.AZTEC.toString() -> R.drawable.aztec_example
        QR_CODE -> R.drawable.qr_example_list
        DATA_MATRIX -> R.drawable.datamatrix_example
        else -> R.drawable.ean_example
    }

@Preview(showBackground = true)
@Composable
private fun BarcodeCardPreview() {
    AppTheme {
        Column {
            BarcodeCard(
                uiModel = BarcodeUiModel(
                    id = "1",
                    name = "Sample Barcode",
                    barcodeType = QR_CODE,
                    description = "Sample Description",
                    tags = listOf(
                        TagUiModel(name = "Work"),
                        TagUiModel(name = "Personal")
                    ),
                    content = "https://github.com/Kray-Man",
                    pinOrder = 2
                ),
                inSelectionMode = false,
                isCheckedForDeletion = true
            )
            BarcodeCard(
                uiModel = BarcodeUiModel(
                    id = "1",
                    name = "Sample Barcode",
                    barcodeType = QR_CODE,
                    description = "Sample Description",
                    tags = listOf(
                        TagUiModel(name = "Work"),
                        TagUiModel(name = "Personal")
                    ),
                    content = "https://github.com/Kray-Man",
                    pinOrder = 3

                ),
                inSelectionMode = true,
                isCheckedForDeletion = true
            )
        }

    }
}

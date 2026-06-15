package com.krayapp.buffercompanion.bargen.presentation.ui.barcodeCard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.ScanOptions.DATA_MATRIX
import com.journeyapps.barcodescanner.ScanOptions.PDF_417
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.ui.dialogs.ConfirmationDialog
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.AppTheme
import com.krayapp.buffercompanion.bargen.theme.barcodePreviewSize
import com.krayapp.buffercompanion.bargen.theme.cardColorsSelector
import com.krayapp.buffercompanion.bargen.theme.lRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape
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
    pinAvailable: Boolean,
    swipeToDeleteAvailable: Boolean = true,
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
        enableDismissFromEndToStart = swipeToDeleteAvailable && !inSelectionMode,
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
        Column(modifier = Modifier.fillMaxWidth()) {
            Card(
                shape = selectionShape(inSelectionMode),
                colors = cardColorsSelector(
                    model = uiModel,
                    isCheckedForDelete = isCheckedForDeletion
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .zIndex(5f)
                    .pointerInput(uiModel, inSelectionMode) {
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
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (uiModel.isPinned)
                        Box(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.secondary)
                                .fillMaxHeight()
                                .width(7.dp)
                        )

                    Space(width = sSize)
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(vertical = xsSize)) {
                        StringInfo(
                            name = uiModel.name,
                            content = uiModel.content,
                        )

                        Space(sSize)
                        TagsRow(modifier = Modifier.padding(end = mSize), tags = uiModel.tags)
                    }

                    BarcodeInfo(
                        modifier = Modifier.padding(vertical = sSize),
                        barcodePreviewRes =
                            uiModel.barcodeType.getPreviewDrawableFromType(),
                    )
                    Space(width = sSize)

                }
            }
            if (inSelectionMode) {
                val menus = buildList {
                    add(MenuOption.DELETE)
                    if (!uiModel.isPinned && pinAvailable)
                        add(MenuOption.PIN)

                    if (uiModel.isPinned)
                        add(MenuOption.UNPIN)

                    if (uiModel.isPinned)
                        add(MenuOption.DRAG)
                }
                ContextCardComponent(
                    reorderModifier = reorderModifier,
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        when (it) {
                            MenuOption.PIN -> onPin()
                            MenuOption.UNPIN -> onUnpin()
                            MenuOption.DELETE -> {
                                deleteDialogShowState.value = uiModel.id
                            }

                            else -> {}
                        }
                    },
                    menus = menus
                )
            }
        }

    }


}

@Composable
private fun RemoveCardBackground() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(lSize)
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        Column(
            modifier = Modifier.padding(horizontal = mSize),
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

@Composable
private fun BarcodeInfo(
    modifier: Modifier = Modifier,
    barcodePreviewRes: Int = R.drawable.pdf417_example,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = mRoundedCornerShape
            )
            .padding(mSize)
    ) {
        Image(
            painter = painterResource(barcodePreviewRes),
            modifier = Modifier.size(barcodePreviewSize),
            contentDescription = "barcode_preview",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
    }

}

@Composable
private fun StringInfo(
    modifier: Modifier = Modifier,
    content: String = "content",
    name: String = "name",
) {
    Column(modifier = modifier) {
        Text(text = name, style = MaterialTheme.typography.bodyLarge, maxLines = 2)
        Text(
            modifier = Modifier.alpha(0.7f),
            text = content,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2
        )
    }
}

@Composable
private fun TagsRow(
    modifier: Modifier = Modifier,
    tags: List<TagUiModel>
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = mRoundedCornerShape,
                )
        )
        FlowRow {
            tags.forEach { BargenChip(model = it, selectable = false) }
        }
    }

}

private fun selectionShape(inSelection: Boolean): RoundedCornerShape {
    return if (inSelection)
        RoundedCornerShape(topEnd = lSize, topStart = lSize)
    else
        lRoundedCornerShape
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
                isCheckedForDeletion = true,
                pinAvailable = true
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
                isCheckedForDeletion = true,
                pinAvailable = true
            )
        }

    }
}

package com.krayapp.buffercompanion.bargen.ui.adapter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.ScanOptions.DATA_MATRIX
import com.journeyapps.barcodescanner.ScanOptions.PDF_417
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.testBarcodeUiModel
import com.krayapp.buffercompanion.bargen.testTagUiModel
import com.krayapp.buffercompanion.bargen.theme.barcodePreviewSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.theme.xsSize
import com.krayapp.buffercompanion.bargen.ui.BargenChip
import com.krayapp.buffercompanion.bargen.ui.menu.ContextMenu
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel
import com.krayapp.buffercompanion.bargen.utils.Space
import com.krayapp.buffercompanion.bargen.utils.modifiers.onCombinedTapScreenOffset
import kotlinx.coroutines.launch

@Preview
@Composable
fun BarcodeCard(
    uiModel: BarcodeUiModel = testBarcodeUiModel.first(),
    onDeleteClick: (BarcodeUiModel) -> Unit = {},
    onCardClick: (BarcodeUiModel) -> Unit = {},
    onSelectClick: (BarcodeUiModel) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val showPopup = remember { mutableStateOf(false) }
    val popupOffset = remember { mutableStateOf(Offset(0f, 0f)) }
    val haptic = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        enableDismissFromStartToEnd = false,
        state = dismissState,
        onDismiss = {
            scope.launch {
                dismissState.reset()
                onDeleteClick(uiModel)
            }
        },
        backgroundContent = {
            RemoveCardBackground()
        }) {
        Card(
            shape = RoundedCornerShape(size = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .onCombinedTapScreenOffset(
                    onLong = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        popupOffset.value = it
                        showPopup.value = true
                        onSelectClick(uiModel)
                    },

                    onTap = {
                        onCardClick(uiModel)
                    })
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = sSize),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
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
                    content = uiModel.content
                )
            }
        }
    }


    if (showPopup.value)
        ContextMenu(offset = popupOffset.value, uiModel) {
            showPopup.value = false
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
                    imageVector = ImageVector.vectorResource(R.drawable.ic_delete),
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
            contentDescription = "barcode_preview"
        )
        Text(text = barcodeTypeText, style = MaterialTheme.typography.labelSmall)
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentInfo(
    modifier: Modifier = Modifier,
    content: String = "content",
    name: String = "name",
    tags: List<TagUiModel> = testTagUiModel
) {
    Column(modifier = modifier.padding(vertical = sSize)) {
        val textStyle = MaterialTheme.typography.titleMedium
        Text(text = content, style = textStyle)
        Text(text = name, style = textStyle)

        FlowRow(Modifier.fillMaxWidth()) {
            tags.forEach { BargenChip(it, selectable = false) }
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
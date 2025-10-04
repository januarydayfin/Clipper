package com.krayapp.buffercompanion.bargen.ui.adapter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.ScanOptions.DATA_MATRIX
import com.journeyapps.barcodescanner.ScanOptions.PDF_417
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.testBarcodeUiModel
import com.krayapp.buffercompanion.bargen.testTagUiModel
import com.krayapp.buffercompanion.bargen.theme.barcodePreviewSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.ui.BargenChip
import com.krayapp.buffercompanion.bargen.ui.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.ui.models.TagUiModel

@Preview
@Composable
fun BarcodeCard(
    uiModel: BarcodeUiModel = testBarcodeUiModel.first(),
    onContextMenuCalled: (IntOffset, BarcodeUiModel) -> Unit = { _, _ -> }
) {
    val contextMenuCoordinates = remember { mutableStateOf(IntOffset(0, 0)) }
    Card(
        shape = RoundedCornerShape(size = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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

            Spacer(Modifier.width(sSize))
            ContentInfo(
                modifier = Modifier
                    .weight(1f),
                name = uiModel.name,
                content = uiModel.content
            )
            Image(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .onGloballyPositioned {
                        val offset = IntOffset(
                            x = it.positionInWindow().x.toInt(),
                            y = it.positionInWindow().y.toInt()
                        )
                        contextMenuCoordinates.value = offset
                    }
                    .clickable {
                        onContextMenuCalled(contextMenuCoordinates.value, uiModel)
                    },
                painter = painterResource(R.drawable.ic_menu_ellipsis),
                contentDescription = "context_menu",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer)
            )
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

        FlowRow {
            tags.forEach { BargenChip(it) }
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
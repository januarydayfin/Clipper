package com.krayapp.buffercompanion.bargen.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.ScanOptions.CODE_128
import com.journeyapps.barcodescanner.ScanOptions.CODE_39
import com.journeyapps.barcodescanner.ScanOptions.CODE_93
import com.journeyapps.barcodescanner.ScanOptions.DATA_MATRIX
import com.journeyapps.barcodescanner.ScanOptions.EAN_13
import com.journeyapps.barcodescanner.ScanOptions.EAN_8
import com.journeyapps.barcodescanner.ScanOptions.ITF
import com.journeyapps.barcodescanner.ScanOptions.PDF_417
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE
import com.journeyapps.barcodescanner.ScanOptions.RSS_14
import com.journeyapps.barcodescanner.ScanOptions.RSS_EXPANDED
import com.journeyapps.barcodescanner.ScanOptions.UPC_A
import com.journeyapps.barcodescanner.ScanOptions.UPC_E
import com.krayapp.buffercompanion.bargen.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeFormatDialog(
    initialState: String = QR_CODE,
    onPicked: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val entries = listOf(
        BarcodeFormat.AZTEC.toString(),
        UPC_A,
        UPC_E,
        EAN_8,
        EAN_13,
        RSS_14,
        CODE_39,
        CODE_93,
        CODE_128,
        ITF,
        RSS_EXPANDED,
        QR_CODE,
        DATA_MATRIX,
        PDF_417
    )
    val selectedState = remember { mutableStateOf(initialState) }
    BasicAlertDialog(onDismissRequest = {

    }) {
        Card {
            Column {
                entries.forEach { entry ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                            selectedState.value = entry
                        }) {
                        RadioButton(
                            selected = selectedState.value == entry, onClick = {}
                        )


                        Text(text = entry, style = MaterialTheme.typography.labelLarge)
                    }

                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    TextButton(onClick = {
                        onPicked(selectedState.value)
                        onDismiss()
                    }) {
                        Text(text = stringResource(R.string.apply))
                    }
                }
            }
        }

    }

}
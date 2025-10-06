package com.krayapp.buffercompanion.bargen.presentation.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.lSize
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.presentation.utils.Space

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(shape = RoundedCornerShape(size = lSize)) {
            Column(
                modifier = Modifier.padding(all = mSize),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.delete_record), style = MaterialTheme.typography.headlineSmall)

                Space(height = mSize * 2)
                Text(
                    text = stringResource(R.string.action_unrevertable),
                    style = MaterialTheme.typography.bodyMedium
                )
                Space(height = mSize)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        onDismiss()
                    }) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    TextButton(onClick = {
                        onConfirm()
                        onDismiss()
                    }) {
                        Text(
                            text = stringResource(R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
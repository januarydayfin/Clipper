package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun BarcodeFormatBlock(state: State<BarcodeUiModel>, openDialog: () -> Unit) {
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
                painter = painterResource(R.drawable.ic_arrow_down),
                modifier =
                    Modifier.size(SplitButtonDefaults.TrailingIconSize),
                contentDescription = "Localized description",
            )
        }
    })
}
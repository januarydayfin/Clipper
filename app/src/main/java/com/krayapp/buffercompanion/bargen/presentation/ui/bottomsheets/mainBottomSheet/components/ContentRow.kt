package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.mRoundedCornerShape
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize

@Composable
fun ContentRow(
    modifier: Modifier = Modifier,
    uiModel: BarcodeUiModel,
    onTextChange: (String) -> Unit
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_edit),
            tint = colorScheme.onSurface.copy(alpha = 0.3f), contentDescription = null
        )
        Space(sSize)
        BasicTextField(
            modifier = Modifier
                .background(
                    color = colorScheme.surfaceContainerHighest,
                    shape = mRoundedCornerShape
                )
                .padding(mSize),
            value = uiModel.content,
            onValueChange = onTextChange,
            textStyle = typography.bodyLarge.copy(
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
        )
    }
}
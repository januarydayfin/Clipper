package com.krayapp.buffercompanion.bargen.theme

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import com.krayapp.buffercompanion.bargen.presentation.models.BarcodeUiModel


@Composable
private fun selectedCardColors() = CardDefaults.cardColors().copy(
    containerColor = colorScheme.secondaryContainer,
    contentColor = colorScheme.onSecondaryContainer
)

@Composable
fun cardColorsSelector(model: BarcodeUiModel, isCheckedForDelete: Boolean) =
    when {
        isCheckedForDelete -> selectedCardColors()
        else -> CardDefaults.cardColors().copy(containerColor = colorScheme.surfaceContainer)
    }


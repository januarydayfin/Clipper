package com.krayapp.buffercompanion.bargen.presentation.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.xsSize

@Composable
fun SheetDragger() {
    Card(
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.onSurfaceVariant),
        modifier = Modifier
            .width(40.dp)
            .padding(vertical = mSize)
            .height(xsSize)
            .clickable {  }
    ) {}
}